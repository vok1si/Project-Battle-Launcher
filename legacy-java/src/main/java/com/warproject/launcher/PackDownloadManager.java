package com.warproject.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

final class PackDownloadManager {
    private final Object pauseLock = new Object();
    private final List<String> history = new ArrayList<>();
    private volatile boolean paused;
    private volatile boolean active;
    private volatile String current = "";
    private volatile String state = "Idle";
    private volatile long received;
    private volatile long total;
    private volatile double speed;
    private volatile Consumer<String> listener;

    void setListener(Consumer<String> callback) { listener = callback; publish(); }
    void pause() { if (active) { paused = true; state = "Paused"; publish(); } }
    void resume() {
        synchronized (pauseLock) { paused = false; pauseLock.notifyAll(); }
        if (active) { state = "Downloading"; publish(); }
    }
    boolean isActive() { return active; }

    synchronized void begin(List<String> names) {
        history.clear();
        for (String name : names) history.add("Waiting  " + name);
        active = true;
        state = "Checking";
        publish();
    }

    synchronized void mark(String name, String status) {
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).endsWith("  " + name)) { history.set(i, status + "  " + name); break; }
        }
        publish();
    }

    void finish() { active = false; paused = false; current = ""; state = "Complete"; publish(); }
    void fail(String message) { active = false; paused = false; state = "Failed: " + message; publish(); }

    String snapshot() {
        StringBuilder view = new StringBuilder(state);
        if (!current.isBlank()) view.append("\n").append(current).append("  ")
                .append(String.format(Locale.ROOT, "%.1f/%.1f MB  %.1f MB/s", received / 1048576.0, total / 1048576.0, speed / 1048576.0));
        synchronized (this) {
            List<String> visible = history.stream().filter(x -> !x.startsWith("Waiting")).toList();
            for (String entry : visible.stream().skip(Math.max(0, visible.size() - 12)).toList()) view.append("\n").append(entry);
            view.append("\nFiles: ").append(visible.size()).append("/").append(history.size());
        }
        return view.toString();
    }

    private void publish() {
        Consumer<String> callback = listener;
        if (callback != null) callback.accept(snapshot());
    }

    private void waitIfPaused() throws IOException {
        synchronized (pauseLock) {
            while (paused) {
                try { pauseLock.wait(1000); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); throw new IOException("Download interrupted", ex); }
            }
        }
    }

    void download(String name, URL url, Path target, long expectedSize, String expectedHash) throws IOException {
        Files.createDirectories(target.getParent());
        Path part = target.resolveSibling(target.getFileName() + ".part");
        current = name;
        total = Math.max(0, expectedSize);
        mark(name, "Downloading");
        IOException last = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            HttpURLConnection connection = null;
            try {
                waitIfPaused();
                long existing = Files.isRegularFile(part) ? Files.size(part) : 0;
                if (expectedSize >= 0 && existing > expectedSize) { Files.deleteIfExists(part); existing = 0; }
                if (expectedSize >= 0 && existing == expectedSize && existing > 0) {
                    if (expectedHash == null || expectedHash.isBlank() || expectedHash.equalsIgnoreCase(PackMaintenance.sha256(part))) {
                        try { Files.move(part, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
                        catch (AtomicMoveNotSupportedException ex) { Files.move(part, target, StandardCopyOption.REPLACE_EXISTING); }
                        mark(name, "Ready");
                        current = "";
                        speed = 0;
                        return;
                    }
                    Files.deleteIfExists(part);
                    existing = 0;
                }
                connection = (HttpURLConnection)url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(45000);
                connection.setReadTimeout(45000);
                connection.setRequestProperty("User-Agent", "Project Battle Alpha/0.1.78.13");
                if (existing > 0) connection.setRequestProperty("Range", "bytes=" + existing + "-");
                int response = connection.getResponseCode();
                if (response == 416) { Files.deleteIfExists(part); attempt--; continue; }
                if (response != 200 && response != 206) throw new IOException("HTTP " + response + " downloading " + name);
                boolean append = existing > 0 && response == 206;
                if (!append) existing = 0;
                received = existing;
                long windowBytes = 0;
                long windowStart = System.nanoTime();
                try (InputStream input = connection.getInputStream();
                     var output = Files.newOutputStream(part, append
                             ? new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                             : new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING})) {
                    byte[] buffer = new byte[65536];
                    int read;
                    while ((read = input.read(buffer)) >= 0) {
                        if (read == 0) continue;
                        output.write(buffer, 0, read);
                        received += read;
                        windowBytes += read;
                        long now = System.nanoTime();
                        if (now - windowStart >= 250_000_000L) {
                            speed = windowBytes * 1_000_000_000.0 / (now - windowStart);
                            windowBytes = 0;
                            windowStart = now;
                            publish();
                        }
                        if (paused) { state = "Paused"; publish(); break; }
                        if (Thread.currentThread().isInterrupted()) throw new IOException("Download interrupted");
                    }
                }
                if (paused) { attempt--; waitIfPaused(); state = "Downloading"; continue; }
                if (expectedSize >= 0 && Files.size(part) != expectedSize) throw new IOException("Incomplete download: " + name);
                if (expectedHash != null && !expectedHash.isBlank() && !expectedHash.equalsIgnoreCase(PackMaintenance.sha256(part))) {
                    Files.deleteIfExists(part);
                    throw new IOException("SHA-256 mismatch: " + name);
                }
                try { Files.move(part, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
                catch (AtomicMoveNotSupportedException ex) { Files.move(part, target, StandardCopyOption.REPLACE_EXISTING); }
                mark(name, "Ready");
                current = "";
                speed = 0;
                return;
            } catch (IOException ex) {
                last = ex;
                mark(name, "Retry " + attempt + "/3");
                if (attempt < 3) {
                    try { Thread.sleep(500L * attempt); }
                    catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); throw new IOException("Download interrupted", interrupted); }
                }
            } finally { if (connection != null) connection.disconnect(); }
        }
        mark(name, "Failed");
        throw new IOException("Could not download " + name + ": " + (last == null ? "unknown error" : last.getMessage()), last);
    }
}
