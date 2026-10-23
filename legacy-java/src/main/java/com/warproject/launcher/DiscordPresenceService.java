package com.warproject.launcher;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class DiscordPresenceService implements AutoCloseable {
   private static final String APPLICATION_ID = "1525242294096367768";
   private static final String LARGE_IMAGE_KEY = "pb_halloween_large_20261019";
   private static final String LARGE_IMAGE_TEXT = "Project Battle";
   private static final String SMALL_IMAGE_KEY = "pb_classic_small_20261019";
   private static final String SMALL_IMAGE_TEXT = "Project Battle";
   private static final String WEBSITE_URL = "https://project-battle-portal.vokisu9.workers.dev/";
   private static final String NEWS_URL = "https://project-battle-portal.vokisu9.workers.dev/news";
   private final Object lock = new Object();
   private final Consumer<String> logger;
   private final long startTimestamp = System.currentTimeMillis() / 1000L;
   private final Thread worker;
   private volatile boolean running = true;
   private String details = "Находится в лаунчере";
   private String state = "Главное меню";
   private boolean dirty = true;
   private volatile boolean enabled = true;
   private DiscordPresenceService.Connection connection;

   public DiscordPresenceService(Consumer<String> var1) {
      this.logger = var1 == null ? var0 -> {} : var1;
      this.worker = new Thread(this::runLoop, "project-battle-discord-presence");
      this.worker.setDaemon(true);
      this.worker.start();
      Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "project-battle-discord-presence-shutdown"));
   }

   public void setState(String var1) {
      this.setActivity(this.details, var1);
   }

   public void setDetails(String var1) {
      this.setActivity(var1, this.state);
   }

   public void setActivity(String var1, String var2) {
      synchronized (this.lock) {
         if (var1 != null && !var1.isBlank()) {
            this.details = var1;
         }

         if (var2 != null && !var2.isBlank()) {
            this.state = var2;
         }

         this.dirty = true;
         this.lock.notifyAll();
      }
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
      synchronized (this.lock) {
         this.dirty = true;
         this.lock.notifyAll();
      }

      if (!var1) {
         this.closeConnection();
      }
   }

   private void runLoop() {
      while (this.running) {
         try {
            if (!this.enabled) {
               synchronized (this.lock) {
                  if (this.running && !this.enabled) {
                     this.lock.wait(15000L);
                  }
               }
            } else {
               if (this.connection == null) {
                  this.connection = this.connect();
                  if (this.connection != null) {
                     this.sendActivity();
                  }
               }

               synchronized (this.lock) {
                  if (this.running && !this.dirty) {
                     this.lock.wait(15000L);
                  }
               }

               if (this.running && this.dirty && this.connection != null) {
                  this.sendActivity();
               }
            }
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
            break;
         } catch (Exception var7) {
            this.closeConnection();
            if (this.running) {
               this.logger.accept("[Discord] " + safeMessage(var7));
               sleepQuietly(5000L);
            }
         }
      }

      this.closeConnection();
   }

   private DiscordPresenceService.Connection connect() throws IOException {
      if (isWindows()) {
         for (int var15 = 0; var15 <= 9; var15++) {
            try {
               RandomAccessFile var16 = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + var15, "rw");
               DiscordPresenceService.Connection var17 = new DiscordPresenceService.Connection(var16, new FileOutputStream(var16.getFD()), var16);
               var17.write(0, "{\"v\":1,\"client_id\":\"1525242294096367768\"}");
               return var17;
            } catch (IOException var13) {
            }
         }

         return null;
      } else {
         Path var1 = Path.of(System.getProperty("user.home", ""));
         Path[] var2 = new Path[]{var1.resolve(".config/discord"), var1.resolve(".config/discordcanary"), var1.resolve(".config/discordptb")};

         for (Path var6 : var2) {
            if (Files.isDirectory(var6)) {
               try (Stream<Path> var7 = Files.list(var6)) {
                  Path var8 = var7.filter(var0 -> var0.getFileName().toString().startsWith("discord-ipc-")).findFirst().orElse(null);
                  if (var8 != null) {
                     SocketChannel var9 = SocketChannel.open(UnixDomainSocketAddress.of(var8));
                     DiscordPresenceService.Connection var10 = new DiscordPresenceService.Connection(var9, Channels.newOutputStream(var9), var9);
                     var10.write(0, "{\"v\":1,\"client_id\":\"1525242294096367768\"}");
                     return var10;
                  }
               }
            }
         }

         return null;
      }
   }

   private void sendActivity() throws IOException {
      if (this.enabled) {
         String var1;
         String var2;
         synchronized (this.lock) {
            var1 = this.details;
            var2 = this.state;
         }

         String var9 = UUID.randomUUID().toString();
         String var4 = "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":"
            + ProcessHandle.current().pid()
            + ",\"activity\":{\"type\":0,\"name\":\"Project Battle\",\"details\":\""
            + jsonEscape(var1)
            + "\",\"state\":\""
            + jsonEscape(var2)
            + "\",\"timestamps\":{\"start\":"
            + this.startTimestamp
            + "},\"assets\":{\"large_image\":\"pb_halloween_large_20261019\",\"large_text\":\"Project Battle\",\"small_image\":\"pb_classic_small_20261019\",\"small_text\":\"Project Battle\"},\"buttons\":[{\"label\":\"Открыть сайт\",\"url\":\"https://project-battle-portal.vokisu9.workers.dev/\"},{\"label\":\"Новости\",\"url\":\"https://project-battle-portal.vokisu9.workers.dev/news\"}]}},\"nonce\":\""
            + var9
            + "\"}";
         this.connection.write(1, var4);
         synchronized (this.lock) {
            this.dirty = false;
         }
      }
   }

   @Override
   public void close() {
      this.shutdown();
   }

   public void shutdown() {
      this.running = false;
      synchronized (this.lock) {
         this.lock.notifyAll();
      }

      this.worker.interrupt();
      this.closeConnection();
   }

   private void closeConnection() {
      DiscordPresenceService.Connection var1 = this.connection;
      this.connection = null;
      if (var1 != null) {
         try {
            var1.write(1, "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":" + ProcessHandle.current().pid() + ",\"activity\":null},\"nonce\":\"clear\"}");
         } catch (Exception var3) {
         }

         var1.close();
      }
   }

   private static String jsonEscape(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 16);

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\t':
               var1.append("\\t");
               break;
            case '\n':
               var1.append("\\n");
               break;
            case '\r':
               var1.append("\\r");
               break;
            case '"':
               var1.append("\\\"");
               break;
            case '\\':
               var1.append("\\\\");
               break;
            default:
               var1.append(var3);
         }
      }

      return var1.toString();
   }

   private static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   }

   private static void sleepQuietly(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   private static String safeMessage(Exception var0) {
      String var1 = var0.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : var0.getClass().getSimpleName();
   }

   private static final class Connection implements Closeable {
      private final OutputStream output;
      private final Closeable closeable;

      private Connection(Object var1, OutputStream var2, Closeable var3) {
         this.output = var2;
         this.closeable = var3;
      }

      private void write(int var1, String var2) throws IOException {
         byte[] var3 = var2.getBytes(StandardCharsets.UTF_8);
         ByteBuffer var4 = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
         var4.putInt(var1).putInt(var3.length).flip();
         this.output.write(var4.array());
         this.output.write(var3);
         this.output.flush();
      }

      @Override
      public void close() {
         try {
            this.output.close();
         } catch (IOException var3) {
         }

         try {
            this.closeable.close();
         } catch (IOException var2) {
         }
      }
   }
}
