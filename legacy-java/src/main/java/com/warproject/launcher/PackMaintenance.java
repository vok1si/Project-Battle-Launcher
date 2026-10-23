package com.warproject.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

final class PackMaintenance {
    record FileSpec(String path, String url, String sha256, long size, boolean required) {}
    record FileIssue(FileSpec file, String reason) {}
    record Storage(long mods, long libraries, long java, long cache, long logs) {}

    private PackMaintenance() {}

    static Path inside(Path root, String relative) throws IOException {
        if (relative == null || relative.isBlank() || Path.of(relative).isAbsolute())
            throw new IOException("Invalid manifest path: " + relative);
        Path base = root.toAbsolutePath().normalize();
        Path target = base.resolve(relative).normalize();
        if (!target.startsWith(base)) throw new IOException("Manifest path escapes game folder: " + relative);
        Path cursor = base;
        for (Path component : base.relativize(target)) {
            cursor = cursor.resolve(component);
            if (Files.isSymbolicLink(cursor)) throw new IOException("Symbolic link in game path: " + relative);
        }
        return target;
    }

    static List<FileIssue> inspect(Path root, List<FileSpec> files) throws IOException {
        List<FileIssue> issues = new ArrayList<>();
        for (FileSpec file : files) {
            Path target = inside(root, file.path());
            if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
                if (file.required() || file.url() != null && !file.url().isBlank()) issues.add(new FileIssue(file, "missing"));
                continue;
            }
            if (file.size() >= 0 && Files.size(target) != file.size()) {
                issues.add(new FileIssue(file, "size"));
                continue;
            }
            if (file.sha256() != null && !file.sha256().isBlank()
                    && !file.sha256().equalsIgnoreCase(sha256(target))) {
                issues.add(new FileIssue(file, "sha256"));
            }
        }
        return issues;
    }

    static String sha256(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream input = Files.newInputStream(file)) {
                byte[] buffer = new byte[65536];
                int read;
                while ((read = input.read(buffer)) >= 0) {
                    if (read > 0) digest.update(buffer, 0, read);
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 unavailable", e);
        }
    }

    static long size(Path directory) throws IOException {
        if (!Files.exists(directory)) return 0;
        long total = 0;
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path file : paths.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)).toList()) {
                total += Files.size(file);
            }
        }
        return total;
    }

    static Storage storage(Path instance, Path minecraft) throws IOException {
        return new Storage(
                size(instance.resolve("mods")),
                size(minecraft.resolve("libraries")),
                size(minecraft.resolve("runtime")),
                size(instance.resolve("cache")) + size(minecraft.resolve("assets")),
                size(instance.resolve("logs")));
    }

    static List<Path> temporaryFiles(Path instance) throws IOException {
        if (!Files.isDirectory(instance)) return List.of();
        List<Path> candidates = new ArrayList<>();
        Instant cutoff = Instant.now().minus(Duration.ofHours(1));
        try (Stream<Path> paths = Files.walk(instance)) {
            for (Path file : paths.filter(p -> Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS)).toList()) {
                Path relative = instance.toAbsolutePath().normalize().relativize(file.toAbsolutePath().normalize());
                if (relative.getNameCount() == 0 || Set.of("saves", "screenshots", "config", "resourcepacks", "shaderpacks")
                        .contains(relative.getName(0).toString().toLowerCase())) continue;
                String name = file.getFileName().toString();
                FileTime modified = Files.getLastModifiedTime(file, LinkOption.NOFOLLOW_LINKS);
                if ((name.endsWith(".part") || name.contains(".projectbattle-update-check-"))
                        && modified.toInstant().isBefore(cutoff)) candidates.add(file);
            }
        }
        return candidates;
    }

    static List<Path> unmanagedMods(Path instance, List<FileSpec> manifest) throws IOException {
        Path mods = instance.resolve("mods");
        if (!Files.isDirectory(mods)) return List.of();
        Set<Path> managed = manifest.stream().map(file -> instance.resolve(file.path()).toAbsolutePath().normalize())
                .collect(java.util.stream.Collectors.toSet());
        List<Path> extras = new ArrayList<>();
        try (Stream<Path> entries = Files.list(mods)) {
            for (Path file : entries.toList()) {
                if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) || !file.getFileName().toString().toLowerCase().endsWith(".jar")) continue;
                if (file.getFileName().toString().equals("projectbattle-settings-test-0.3.5-test.jar")) continue;
                if (!managed.contains(file.toAbsolutePath().normalize())) extras.add(file);
            }
        }
        return extras;
    }

    static Path quarantineMods(Path instance, Path configDir, List<Path> extras) throws IOException {
        Path mods = instance.resolve("mods").toAbsolutePath().normalize();
        Path backup = configDir.resolve("mod-backups").resolve(String.valueOf(System.currentTimeMillis()));
        Files.createDirectories(backup);
        for (Path file : extras) {
            Path original = file.toAbsolutePath().normalize();
            if (!original.getParent().equals(mods) || Files.isSymbolicLink(original))
                throw new IOException("Unsafe mod path: " + original);
            Path copy = backup.resolve(original.getFileName());
            Files.copy(original, copy);
            if (Files.size(original) != Files.size(copy) || !sha256(original).equals(sha256(copy))) {
                Files.deleteIfExists(copy);
                throw new IOException("Mod backup verification failed: " + original.getFileName());
            }
            Files.delete(original);
        }
        return backup;
    }

    static void deleteTemporary(Path instance, List<Path> files) throws IOException {
        Path base = instance.toAbsolutePath().normalize();
        for (Path file : files) {
            Path target = file.toAbsolutePath().normalize();
            if (!target.startsWith(base) || Files.isSymbolicLink(target)) throw new IOException("Unsafe cleanup path: " + target);
            if (!temporaryFiles(base).contains(target)) throw new IOException("Temporary file changed during cleanup: " + target);
            Files.deleteIfExists(target);
        }
    }

    static long migrate(Path source, Path destination) throws IOException {
        Path from = source.toAbsolutePath().normalize();
        Path to = destination.toAbsolutePath().normalize();
        if (!Files.isDirectory(from) || to.startsWith(from) || from.startsWith(to) || from.equals(to))
            throw new IOException("Choose another empty game folder outside the current folder.");
        if (Files.exists(to)) {
            try (Stream<Path> existing = Files.list(to)) {
                if (existing.findAny().isPresent()) throw new IOException("Destination folder is not empty.");
            }
        }
        Path parent = to.getParent();
        if (parent == null) throw new IOException("Invalid destination.");
        Files.createDirectories(parent);
        long bytes = size(from);
        FileStore store = Files.getFileStore(parent);
        long reserve = Math.max(512L * 1024 * 1024, bytes / 20);
        if (store.getUsableSpace() < bytes + reserve) throw new IOException("Not enough free disk space for safe copy.");
        Files.createDirectories(to);
        try (Stream<Path> paths = Files.walk(from)) {
            for (Path entry : paths.toList()) {
                if (Files.isSymbolicLink(entry)) throw new IOException("Symbolic link in game folder: " + entry);
                Path target = to.resolve(from.relativize(entry));
                if (Files.isDirectory(entry)) Files.createDirectories(target);
                else if (Files.isRegularFile(entry)) {
                    Files.createDirectories(target.getParent());
                    Files.copy(entry, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                    if (Files.size(entry) != Files.size(target) || !sha256(entry).equals(sha256(target)))
                        throw new IOException("Copy verification failed: " + entry);
                }
            }
        }
        return bytes;
    }

    static long migrateGame(Path instance, Path minecraft, Path destinationRoot) throws IOException {
        Path root = destinationRoot.toAbsolutePath().normalize();
        Path profile = instance.toAbsolutePath().normalize();
        Path shared = minecraft.toAbsolutePath().normalize();
        if (!Files.isDirectory(profile) || !Files.isDirectory(shared) || root.startsWith(profile) || root.startsWith(shared)
                || profile.startsWith(root) || shared.startsWith(root))
            throw new IOException("Choose an empty destination outside the current game folders.");
        if (Files.exists(root)) {
            try (Stream<Path> contents = Files.list(root)) {
                if (contents.findAny().isPresent()) throw new IOException("Destination folder is not empty.");
            }
        }
        Files.createDirectories(root);
        long required = size(profile) + size(shared);
        long reserve = Math.max(512L * 1024 * 1024, required / 20);
        if (Files.getFileStore(root).getUsableSpace() < required + reserve)
            throw new IOException("Not enough free disk space for game, libraries, Java and a safety reserve.");
        migrate(profile, root.resolve("profile"));
        migrate(shared, root.resolve("minecraft"));
        return required;
    }
}
