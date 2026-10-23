package com.warproject.launcher;

import com.sun.management.OperatingSystemMXBean;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Desktop.Action;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D.Float;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

public final class ProjectBattleAlpha extends JFrame {
   private static final String APP_NAME = "Project Battle Alpha";
   private static final String APP_VERSION = "0.1.78.13";
   private static final Cursor LAUNCHER_CURSOR = null;
   private static volatile ProjectBattleAlpha activeLauncher;
   private static final String PROJECT_NAME = "PROJECT BATTLE";
   private static final String CONFIG_DIR = "ProjectBattleAlpha";
   private static final String INSTANCE_DIR = ".projectbattle";
   private static final String STANDARD_MANIFEST_URL = "https://github.com/vok1si/project-battle-pack-NOW/releases/download/v0.1.0/launcher-manifest.project-battle.generated.json";
   private static final String MANIFEST_OVERRIDE_URL = System.getProperty("projectbattle.manifest.url", "").trim();
   private static final String DEFAULT_MANIFEST_URL = MANIFEST_OVERRIDE_URL.isEmpty()
      ? "https://github.com/vok1si/project-battle-pack-NOW/releases/download/v0.1.0/launcher-manifest.project-battle.generated.json"
      : MANIFEST_OVERRIDE_URL;
   private static final String MANIFEST_RELEASE_API_URL = "https://api.github.com/repos/vok1si/project-battle-pack-NOW/releases/tags/v0.1.0";
   private static final String MANIFEST_ASSET_PREFIX = "launcher-manifest.project-battle.generated";
   private static final String FORGE_INSTALLER_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.4.28/forge-1.20.1-47.4.28-installer.jar";
   private static final String FORGE_INSTALLER_FILE = "forge-1.20.1-47.4.28-installer.jar";
   private static final String DEFAULT_FORGE_VERSION_ID = "1.20.1-forge-47.4.28";
   private static final String VANILLA_VERSION_ID = "1.20.1";
   private static final int MIN_RAM_MB = 2048;
   private static final int DEFAULT_RAM_MB = 8192;
   private static final int HTTP_CONNECT_TIMEOUT_MS = 45000;
   private static final int HTTP_READ_TIMEOUT_MS = 180000;
   private static final int HTTP_MAX_ATTEMPTS = 3;
   private static final int MAX_RAM_MB = detectMaxRamMb();
   private static final String MOJANG_VERSION_MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
   private static final String DEFAULT_SERVER = "ProjectBattle.exaroton.me:33061";
   private static final String DEFAULT_AUTH_API_URL = "https://project-battle-auth-api-yp7n.onrender.com";
   private static final String SUPPORT_EMAIL = "ourffers@gmail.com";
   private static final String TELEGRAM_SUPPORT_URL = "https://t.me/ProjectBattleSupport_bot";
   private static final String PORTAL_URL = "https://project-battle-portal.vokisu9.workers.dev/";
   private static final String PORTAL_NEWS_URL = "https://project-battle-portal.vokisu9.workers.dev/#news";
   private static final String PORTAL_FORUM_URL = "https://project-battle-portal.vokisu9.workers.dev/#forum";
   private static final String PORTAL_FAQ_URL = "https://project-battle-portal.vokisu9.workers.dev/#faq";
   private static final String SETTINGS_MOD_RESOURCE = "/assets/extensions/projectbattle-settings-test-0.3.5-test.jar";
   private static final String SETTINGS_MOD_FILE = "projectbattle-settings-test-0.3.5-test.jar";
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
   private final ExecutorService worker = Executors.newSingleThreadExecutor();
   private final ExecutorService profileWorker = Executors.newFixedThreadPool(2);
   private final Set<String> extensionInstalls = Collections.synchronizedSet(new HashSet<>());
   private final AtomicBoolean maintenanceRunning = new AtomicBoolean(false);
   private final PackDownloadManager packDownloads = new PackDownloadManager();
   private final ProjectBattleAlpha.Config config;
   private final BufferedImage logoImage = loadImage("/assets/project-battle-logo-wide.png");
   private final BufferedImage alphaMainLogo = loadImage("/assets/alpha-main-logo.png");
   private final BufferedImage alphaSidebarLogo = loadImage("/assets/alpha-sidebar-logo.png");
   private final BufferedImage alphaHeroLogo = loadImage("/assets/alpha-hero-logo.png");
   private final BufferedImage alphaTestBackground = loadImage("/assets/alpha-test-background.png");
   private final BufferedImage startNewsBackground = loadImage("/assets/start-news-background.png");
   private final BufferedImage bannerImage = loadImage("/assets/project-battle-banner.png");
   private final BufferedImage backgroundImage = loadImage("/assets/project-battle-background.png");
   private final BufferedImage arenaBackgroundImage = loadImage("/assets/arena-background.png");
   private final BufferedImage arenaTransitionImage = loadImage("/assets/arena-transition.png");
   private final BufferedImage standardTransitionImage = loadImage("/assets/standard-transition.png");
   private final BufferedImage newsBackgroundImage = loadImage("/assets/news-background.png");
   private final BufferedImage rulesImage = loadImage("/assets/project-battle-rules-background.png");
   private final BufferedImage extensionsBackgroundImage = loadImage("/assets/extensions-background.png");
   private final BufferedImage iconImage = loadImage("/assets/project-battle-app-icon.png");
   private final BufferedImage avatarImage = loadImage("/assets/profile-placeholder.png");
   private final BufferedImage playButtonImage = loadImage("/assets/play-button-background.png");
   private final BufferedImage settingsImage = loadImage("/assets/settings-background.png");
   private final BufferedImage accountLoginImage = loadImage("/assets/account-login-background.png");
   private final BufferedImage accountRegisterImage = loadImage("/assets/account-register-background.png");
   private final BufferedImage bodycamShaderImage = loadImage("/assets/extensions/bodycam-shader-icon.png");
   private final BufferedImage complementaryShaderImage = loadImage("/assets/extensions/complementary-reimagined-icon.png");
   private final BufferedImage iterationShaderImage = loadImage("/assets/extensions/iteration-icon.png");
   private final BufferedImage insanityShaderImage = loadImage("/assets/extensions/insanity-icon.png");
   private JTextField nameField;
   private ProjectBattleAlpha.RamSelector ramSelector;
   private JTextField forgeField;
   private JTextField serverField;
   private JTextField manifestField;
   private JCheckBox cleanBox;
   private JCheckBox joinBox;
   private JButton playButton;
   private JButton updateButton;
   private JButton refreshButton;
   private JButton saveButton;
   private JButton nickSaveButton;
   private JTextArea logArea;
   private JProgressBar progress;
   private JLabel updateNoticeLabel;
   private JLabel versionLabel;
   private JLabel editionLabel;
   private JLabel serverStatusLabel;
   private JLabel accountNameLabel;
   private JLabel accountStatusLabel;
   private JLabel accountAvatarLabel;
   private JLabel accountOnlineDot;
   private JPanel accountOnlinePanel;
   private CardLayout viewLayout;
   private ProjectBattleAlpha.FadingViewPanel viewPanel;
   private JLabel gameNav;
   private JLabel newsNav;
   private JLabel rulesNav;
   private JLabel etsNav;
   private JLabel faqNav;
   private JLabel forumNav;
   private JLabel extensionsNav;
   private ProjectBattleAlpha.SidebarProjectItem projectBattleSidebarItem;
   private ProjectBattleAlpha.SidebarProjectItem arenaSidebarItem;
   private boolean arenaMode;
   private boolean projectTransitionRunning;
   private String currentView = "game";
   private Point windowDragOffset;
   private JWindow navTooltip;
   private Timer navTooltipTimer;
   private volatile boolean serverCheckInFlight;
   private volatile boolean minecraftRunning;
   private volatile boolean updateAvailable;
   private volatile boolean busy;
   private volatile boolean updateRunning;
   private volatile int lastProgressValue;
   private volatile String lastProgressText = "0%";
   private boolean cursorListenerInstalled;
   private volatile String detectedManifestVersion = "";
   private final DiscordPresenceService discordPresence;

   public static void main(String[] var0) {
      SwingUtilities.invokeLater(() -> {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception var7) {
         }

         try {
            final ProjectBattleAlpha var0x = new ProjectBattleAlpha(ProjectBattleAlpha.Config.load());
            activeLauncher = var0x;
            var0x.setState(0);
            var0x.setLocationRelativeTo(null);
            final ProjectBattleAlpha.StartupSplashPanel var1 = new ProjectBattleAlpha.StartupSplashPanel();
            var0x.setGlassPane(var1);
            var1.setVisible(true);
            var0x.setVisible(true);
            var0x.toFront();
            var0x.requestFocus();
            final AtomicBoolean var2 = new AtomicBoolean(false);
            Runnable var3 = () -> {
               if (var2.compareAndSet(false, true)) {
                  var1.stopAnimation();
                  var1.setVisible(false);
                  var0x.getGlassPane().setVisible(false);
                  var0x.toFront();
                  var0x.requestFocus();
                  var0x.repaint();
               }
            };
            Timer var4 = new Timer(4000, var1x -> var3.run());
            var4.setRepeats(false);
            var4.start();
            final java.util.Timer var5 = new java.util.Timer("ProjectBattle-SplashFallback", true);
            var5.schedule(new TimerTask() {
               @Override
               public void run() {
                  if (var2.compareAndSet(false, true)) {
                     var1.stopAnimation();
                     var1.setVisible(false);
                     var0x.getGlassPane().setVisible(false);
                     SwingUtilities.invokeLater(() -> {
                        var0x.toFront();
                        var0x.requestFocus();
                        var0x.repaint();
                     });
                  }

                  var5.cancel();
               }
            }, 4300L);
         } catch (Throwable var6) {
            writeStartupFailure(var6);
            var6.printStackTrace();
            JOptionPane.showMessageDialog(null, "Launcher could not open. Details were saved to launcher-startup.log.", "Project Battle Alpha", 0);
         }
      });
   }

   private static void writeStartupFailure(Throwable var0) {
      try {
         Path var1 = Paths.get(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home"))).resolve(".projectbattle");
         Files.createDirectories(var1);
         StringWriter var2 = new StringWriter();
         PrintWriter var3 = new PrintWriter(var2);
         var0.printStackTrace(var3);
         Files.writeString(
            var1.resolve("launcher-startup.log"),
            LocalDateTime.now() + System.lineSeparator() + var2 + System.lineSeparator(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
         );
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   private ProjectBattleAlpha(ProjectBattleAlpha.Config var1) {
      super("Project Battle Alpha");
      this.config = var1;
      this.discordPresence = new DiscordPresenceService(this::log);
      this.discordPresence.setEnabled(var1.discordPresenceEnabled);
      this.buildUi();
      this.applyLauncherCursor(this);
      this.installGlobalLauncherCursor();
      this.loadConfig();
      this.startServerStatusMonitor();
      this.refreshManifestStatusAsync();
      this.refreshSessionProfileAsync();
      this.discordPresence.setActivity("Находится в лаунчере", "Главное меню");
      this.log("Launcher ready.");
   }

   private static Cursor createLauncherCursor() {
      try {
         BufferedImage var0 = loadImage("/assets/launcher-cursor.png");
         if (var0 == null) {
            return null;
         } else {
            Dimension var1 = Toolkit.getDefaultToolkit().getBestCursorSize(var0.getWidth(), var0.getHeight());
            if (var1.width > 0 && var1.height > 0) {
               BufferedImage var2 = new BufferedImage(var1.width, var1.height, 2);
               Graphics2D var3 = var2.createGraphics();
               var3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
               var3.drawImage(var0, 0, 0, var0.getWidth(), var0.getHeight(), null);
               var3.dispose();
               return Toolkit.getDefaultToolkit().createCustomCursor(var2, new Point(1, 1), "Project Battle cursor");
            } else {
               return null;
            }
         }
      } catch (Throwable var4) {
         return null;
      }
   }

   private static Cursor launcherCursor(int var0) {
      return LAUNCHER_CURSOR != null ? LAUNCHER_CURSOR : Cursor.getPredefinedCursor(var0);
   }

   private static Cursor launcherDefaultCursor() {
      return LAUNCHER_CURSOR != null ? LAUNCHER_CURSOR : Cursor.getDefaultCursor();
   }

   private void applyLauncherCursor(Component var1) {
      if (LAUNCHER_CURSOR != null && var1 != null) {
         var1.setCursor(LAUNCHER_CURSOR);
         if (var1 instanceof Container var2) {
            for (Component var6 : var2.getComponents()) {
               this.applyLauncherCursor(var6);
            }
         }
      }
   }

   private void installGlobalLauncherCursor() {
      if (LAUNCHER_CURSOR != null && !this.cursorListenerInstalled) {
         this.cursorListenerInstalled = true;
         Toolkit.getDefaultToolkit().addAWTEventListener(var1 -> {
            if (var1.getSource() instanceof Component var3 && this.belongsToLauncher(var3)) {
               var3.setCursor(LAUNCHER_CURSOR);
            }
         }, 114L);
      }
   }

   private boolean belongsToLauncher(Component var1) {
      for (Window var2 = var1 instanceof Window ? (Window)var1 : SwingUtilities.getWindowAncestor(var1); var2 != null; var2 = var2.getOwner()) {
         if (var2 == this) {
            return true;
         }
      }

      return false;
   }

   private void buildUi() {
      this.setUndecorated(true);
      this.setDefaultCloseOperation(3);
      this.setResizable(false);
      this.setMinimumSize(new Dimension(1198, 646));
      this.setPreferredSize(new Dimension(1426, 759));
      if (this.iconImage != null) {
         this.setIconImage(this.iconImage);
      }

      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(), this.backgroundImage);
      this.setContentPane(var1);
      var1.add(this.buildSidebar(), "West");
      JPanel var2 = new JPanel(new BorderLayout());
      var2.setOpaque(false);
      var2.add(this.buildTopBar(), "North");
      this.viewLayout = new CardLayout();
      this.viewPanel = new ProjectBattleAlpha.FadingViewPanel(this.viewLayout, this.backgroundImage);
      this.viewPanel.setOpaque(false);
      this.viewPanel.add(this.animatedView(this.buildStage()), "game");
      this.viewPanel.add(this.animatedView(this.buildNewsStage()), "news");
      this.viewPanel.add(this.animatedView(this.buildRulesStage()), "rules");
      this.viewPanel.add(this.animatedView(this.buildEtsStage()), "ets");
      this.viewPanel.add(this.animatedView(this.buildFaqStage()), "faq");
      this.viewPanel.add(this.animatedView(this.buildExtensionsStage()), "extensions");
      var2.add(this.viewPanel, "Center");
      var2.add(this.buildBottomBar(), "South");
      var1.add(var2, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
      this.showView("game");
   }

   private JPanel buildSidebar() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.setPreferredSize(new Dimension(228, 759));
      var1.setBackground(ProjectBattleAlpha.Colors.SIDEBAR);
      var1.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ProjectBattleAlpha.Colors.BORDER));
      JPanel var2 = new JPanel();
      var2.setOpaque(false);
      var2.setBorder(new EmptyBorder(42, 26, 0, 26));
      var2.setLayout(new BoxLayout(var2, 1));
      this.projectBattleSidebarItem = (ProjectBattleAlpha.SidebarProjectItem)this.sidebarItem("PB", "Project Battle", true);
      this.arenaSidebarItem = (ProjectBattleAlpha.SidebarProjectItem)this.sidebarItem("PB", "Project Battle: Arena", false);
      this.installSidebarAction(this.projectBattleSidebarItem, () -> this.switchProjectMode(false));
      this.installSidebarAction(this.arenaSidebarItem, () -> this.switchProjectMode(true));
      var2.add(this.projectBattleSidebarItem);
      var2.add(this.arenaSidebarItem);
      var1.add(var2, "North");
      JPanel var3 = new JPanel(new BorderLayout(0, 10));
      var3.setOpaque(false);
      var3.setBorder(new EmptyBorder(0, 26, 22, 26));
      this.updateButton = new ProjectBattleAlpha.DownloadButton(this.tr("Загрузить", "Download"));
      this.updateButton.setPreferredSize(new Dimension(176, 52));
      this.updateButton.addActionListener(this::onUpdate);
      this.progress = new JProgressBar(0, 100);
      this.progress.setStringPainted(true);
      this.progress.setString("0%");
      this.progress.setValue(0);
      this.progress.setPreferredSize(new Dimension(176, 18));
      this.progress.setForeground(ProjectBattleAlpha.Colors.PLAY);
      this.progress.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
      this.progress.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
      var3.add(this.updateButton, "North");
      var3.add(this.progress, "South");
      var1.add(var3, "South");
      return var1;
   }

   private JPanel buildTopBar() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.setPreferredSize(new Dimension(1198, 104));
      var1.setBackground(ProjectBattleAlpha.Colors.TOP);
      var1.setBorder(new EmptyBorder(0, 26, 0, 6));
      JPanel var2 = new JPanel(new FlowLayout(0, 24, 38));
      var2.setOpaque(true);
      var2.setBackground(ProjectBattleAlpha.Colors.TOP);
      this.gameNav = this.navLabel(this.tr("ИГРА", "GAME"), true);
      this.newsNav = this.navLabel(this.tr("НОВОСТИ ↗", "NEWS ↗"), false);
      this.rulesNav = this.navLabel(this.tr("ПРАВИЛА", "RULES"), false);
      this.etsNav = this.navLabel("ETS", false);
      this.faqNav = this.navLabel("FAQ", false);
      this.extensionsNav = this.navLabel(this.tr("РАСШИРЕНИЯ", "EXTENSIONS"), false);
      this.gameNav.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showView("game");
         }
      });
      this.newsNav
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent var1) {
                  ProjectBattleAlpha.this.openPortal(
                     "https://project-battle-portal.vokisu9.workers.dev/#news", ProjectBattleAlpha.this.tr("Не удалось открыть новости", "Could not open news")
                  );
               }
            }
         );
      this.rulesNav.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showView("rules");
         }
      });
      this.etsNav.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showView("ets");
         }
      });
      this.faqNav.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showView("faq");
         }
      });
      this.forumNav = this.navLabel(this.tr("ФОРУМ ↗", "FORUM ↗"), false);
      this.forumNav
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mouseClicked(MouseEvent var1) {
                  ProjectBattleAlpha.this.openPortal(
                     "https://project-battle-portal.vokisu9.workers.dev/#forum", ProjectBattleAlpha.this.tr("Не удалось открыть форум", "Could not open forum")
                  );
               }
            }
         );
      this.extensionsNav.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showView("extensions");
         }
      });
      var2.add(this.gameNav);
      var2.add(this.newsNav);
      var2.add(this.rulesNav);
      var2.add(this.etsNav);
      var2.add(this.faqNav);
      this.etsNav.setVisible(!this.arenaMode);
      var2.add(this.forumNav);
      var2.add(this.extensionsNav);
      var1.add(var2, "West");
      JPanel var3 = new JPanel(new BorderLayout());
      var3.setOpaque(true);
      var3.setBackground(ProjectBattleAlpha.Colors.TOP);
      var3.add(this.buildWindowControls(), "North");
      var3.add(this.buildAccountPanel(), "Center");
      var1.add(var3, "East");
      this.enableWindowDrag(var1);
      this.enableWindowDrag(var2);
      return var1;
   }

   private JPanel buildWindowControls() {
      JPanel var1 = new JPanel(new FlowLayout(2, 5, 4));
      var1.setOpaque(true);
      var1.setBackground(ProjectBattleAlpha.Colors.TOP);
      JButton var2 = this.windowButton("−");
      JButton var3 = this.windowButton("×");
      var2.addActionListener(var1x -> this.setState(1));
      var3.addActionListener(var1x -> this.closeLauncher());
      var1.add(var2);
      var1.add(var3);
      return var1;
   }

   private JButton windowButton(String var1) {
      final JButton var2 = new JButton(var1);
      var2.setPreferredSize(new Dimension(22, 18));
      var2.setOpaque(false);
      var2.setContentAreaFilled(false);
      var2.setFocusPainted(false);
      var2.setBorder(new EmptyBorder(0, 0, 1, 0));
      var2.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var2.setFont(new Font("SansSerif", 1, 14));
      var2.setCursor(launcherCursor(12));
      var2.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1) {
            var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
         }

         @Override
         public void mouseExited(MouseEvent var1) {
            var2.setForeground(ProjectBattleAlpha.Colors.MUTED);
         }
      });
      return var2;
   }

   private void enableWindowDrag(final Component var1) {
      var1.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent var1x) {
            if (SwingUtilities.isLeftMouseButton(var1x)) {
               ProjectBattleAlpha.this.windowDragOffset = SwingUtilities.convertPoint(var1, var1x.getPoint(), ProjectBattleAlpha.this);
            }
         }
      });
      var1.addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseDragged(MouseEvent var1) {
            if (ProjectBattleAlpha.this.windowDragOffset != null && (ProjectBattleAlpha.this.getExtendedState() & 6) != 6) {
               Point var2 = var1.getLocationOnScreen();
               int var3 = var2.x - ProjectBattleAlpha.this.windowDragOffset.x;
               int var4 = var2.y - ProjectBattleAlpha.this.windowDragOffset.y;
               GraphicsConfiguration var5 = ProjectBattleAlpha.this.getGraphicsConfiguration();
               Rectangle var6 = var5 == null ? new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) : var5.getBounds();
               var3 = Math.max(var6.x, Math.min(var3, var6.x + var6.width - ProjectBattleAlpha.this.getWidth()));
               var4 = Math.max(var6.y, Math.min(var4, var6.y + var6.height - 48));
               ProjectBattleAlpha.this.setLocation(var3, var4);
            }
         }
      });
   }

   private void showView(String var1) {
      if (var1 != null) {
         if (this.arenaMode && "ets".equals(var1)) {
            var1 = "game";
         }

         boolean var2 = !var1.equals(this.currentView);
         if (var2) {
            this.currentView = var1;
         }

         DiscordPresenceService var3 = this.discordPresence;

         var3.setState(switch (var1) {
            case "news" -> "Читает новости";
            case "rules" -> "Читает правила";
            case "ets" -> "Читает ETS";
            case "faq" -> "Читает FAQ";
            case "extensions" -> "Просматривает расширения";
            default -> "Главное меню";
         });
         if (var2 && this.viewLayout != null && this.viewPanel != null) {
            this.viewLayout.show(this.viewPanel, var1);
            this.viewPanel.revalidate();
            this.viewPanel.doLayout();
            this.viewPanel.repaint();
            this.viewPanel.startTransition(null);
         }

         boolean var10 = "game".equals(var1);
         boolean var11 = "news".equals(var1);
         boolean var6 = "rules".equals(var1);
         boolean var7 = "ets".equals(var1);
         boolean var8 = "extensions".equals(var1);
         boolean var9 = "faq".equals(var1);
         this.setNavActive(this.gameNav, var10);
         this.setNavActive(this.newsNav, var11);
         this.setNavActive(this.rulesNav, var6);
         this.setNavActive(this.etsNav, var7);
         this.setNavActive(this.faqNav, var9);
         this.setNavActive(this.extensionsNav, var8);
         this.repaint();
      }
   }

   private JComponent animatedView(JComponent var1) {
      return new ProjectBattleAlpha.RevealPanel(var1);
   }

   private void rebuildViews() {
      if (this.viewPanel != null && this.viewLayout != null) {
         this.viewPanel.removeAll();
         this.viewPanel.add(this.animatedView(this.buildStage()), "game");
         this.viewPanel.add(this.animatedView(this.buildNewsStage()), "news");
         this.viewPanel.add(this.animatedView(this.buildRulesStage()), "rules");
         this.viewPanel.add(this.animatedView(this.buildEtsStage()), "ets");
         this.viewPanel.add(this.animatedView(this.buildFaqStage()), "faq");
         this.viewPanel.add(this.animatedView(this.buildExtensionsStage()), "extensions");
         this.loadConfig();
         this.showView(this.currentView);
         this.applyProjectModeControls();
         this.viewPanel.revalidate();
         this.viewPanel.repaint();
      }
   }

   private BufferedImage activeStageBackground() {
      return this.arenaMode && this.arenaBackgroundImage != null ? this.arenaBackgroundImage : this.backgroundImage;
   }

   private void applyProjectModeControls() {
      if (this.etsNav != null) {
         this.etsNav.setVisible(!this.arenaMode);
      }

      if (this.updateButton != null) {
         this.updateButton.setText(this.tr("Загрузить", "Download"));
         this.updateButton.setEnabled(!this.arenaMode && !this.busy);
         this.updateButton.setCursor(this.arenaMode ? launcherDefaultCursor() : launcherCursor(12));
         this.updateButton.setHorizontalAlignment(2);
         this.updateButton.setBorder(new EmptyBorder(8, 52, 8, 12));
      }

      if (this.progress != null) {
         this.progress.setVisible(true);
         this.progress.setValue(this.lastProgressValue);
         this.progress.setString(this.lastProgressText);
      }

      if (this.playButton != null) {
         if (this.arenaMode) {
            this.playButton.setText(this.tr("В разработке...", "In development..."));
            this.playButton.setEnabled(false);
            this.playButton.setCursor(launcherDefaultCursor());
         } else {
            this.playButton
               .setText(
                  this.minecraftRunning
                     ? this.tr("Запущено", "Running")
                     : (this.updateAvailable ? this.tr("Скачать \ud83d\udce5", "Download \ud83d\udce5") : this.tr("Играть", "Play"))
               );
            this.playButton.setEnabled(!this.minecraftRunning);
            this.playButton.setCursor(launcherCursor(12));
         }
      }

      if (this.versionLabel != null) {
         this.versionLabel
            .setText(
               infoHtml(
                  this.tr("Версия лаунчера:", "Launcher version:"),
                  this.arenaMode ? this.tr("Альфа 0.1", "Alpha 0.1") : this.tr("0.1.78.13", "0.1.78.13"),
                  ProjectBattleAlpha.Colors.MUTED
               )
            );
      }

      if (this.editionLabel != null) {
         this.editionLabel.setText(infoHtml(this.tr("Издание:", "Edition:"), "Standard", ProjectBattleAlpha.Colors.MUTED));
      }

      if (this.serverStatusLabel != null) {
         String var1 = this.arenaMode ? "Arena" : "ETS ▾";
         Color var2 = this.arenaMode ? ProjectBattleAlpha.Colors.MUTED : ProjectBattleAlpha.Colors.PLAY;
         this.serverStatusLabel.setText(infoHtml(this.tr("Сервер:", "Server:"), var1, var2));
         this.serverStatusLabel.setCursor(this.arenaMode ? launcherDefaultCursor() : launcherCursor(12));
      }

      if (this.updateNoticeLabel != null) {
         this.updateNoticeLabel.setVisible(!this.arenaMode && this.updateAvailable);
      }
   }

   private void switchProjectMode(boolean var1) {
      if (!this.projectTransitionRunning && this.arenaMode != var1) {
         this.projectTransitionRunning = true;
         BufferedImage var2 = var1 ? this.arenaTransitionImage : this.standardTransitionImage;
         ProjectBattleAlpha.ArenaTransitionOverlay var3 = new ProjectBattleAlpha.ArenaTransitionOverlay(var2, var1);
         this.setGlassPane(var3);
         var3.start(
            () -> {
               this.arenaMode = var1;
               this.currentView = "";
               if (this.etsNav != null) {
                  this.etsNav.setVisible(!this.arenaMode);
                  this.etsNav.getParent().revalidate();
               }

               if (this.projectBattleSidebarItem != null) {
                  this.projectBattleSidebarItem.setActive(!this.arenaMode);
               }

               if (this.arenaSidebarItem != null) {
                  this.arenaSidebarItem.setActive(this.arenaMode);
               }

               this.rebuildViews();
               this.showView("game");
               this.discordPresence
                  .setActivity(this.arenaMode ? "Project Battle Arena" : "Project Battle", this.arenaMode ? "Arena — в разработке" : "Главное меню");
            },
            () -> {
               this.projectTransitionRunning = false;
               var3.setVisible(false);
               this.repaint();
            }
         );
         var3.setVisible(true);
      }
   }

   private void setNavActive(JLabel var1, boolean var2) {
      if (var1 != null) {
         if (var1 instanceof ProjectBattleAlpha.NavItemLabel var3) {
            var3.setActive(var2);
         } else {
            var1.setForeground(var2 ? ProjectBattleAlpha.Colors.TEXT : ProjectBattleAlpha.Colors.MUTED);
            var1.setFont(new Font("SansSerif", var2 ? 1 : 0, 20));
            var1.setBorder(var2 ? BorderFactory.createMatteBorder(0, 0, 3, 0, ProjectBattleAlpha.Colors.PLAY) : new EmptyBorder(0, 0, 3, 0));
         }
      }
   }

   private JPanel buildAccountPanel() {
      JPanel var1 = new JPanel(new BorderLayout(10, 0));
      var1.setPreferredSize(new Dimension(214, 74));
      var1.setMinimumSize(new Dimension(214, 74));
      var1.setOpaque(true);
      var1.setBackground(ProjectBattleAlpha.Colors.TOP);
      var1.setBorder(new EmptyBorder(18, 0, 16, 0));
      this.accountAvatarLabel = new JLabel();
      this.accountAvatarLabel.setPreferredSize(new Dimension(48, 48));
      this.accountAvatarLabel.setMinimumSize(new Dimension(48, 48));
      this.accountAvatarLabel.setMaximumSize(new Dimension(48, 48));
      this.accountAvatarLabel.setBorder(null);
      this.accountAvatarLabel.setIcon(new ImageIcon(makeAvatarImage(this.avatarImage, 48)));
      var1.add(this.accountAvatarLabel, "West");
      JPanel var2 = new JPanel(new BorderLayout(0, 4));
      var2.setOpaque(true);
      var2.setBackground(ProjectBattleAlpha.Colors.TOP);
      var2.setBorder(new EmptyBorder(0, 0, 0, 3));
      this.nameField = this.input();
      this.nameField.setVisible(false);
      this.nameField.setFont(new Font("SansSerif", 1, 13));
      this.accountNameLabel = new JLabel(this.config.playerName);
      this.accountNameLabel.setForeground(ProjectBattleAlpha.Colors.TEXT);
      this.accountNameLabel.setFont(new Font("SansSerif", 1, 15));
      this.accountOnlinePanel = new JPanel(new FlowLayout(0, 5, 0));
      this.accountOnlinePanel.setOpaque(true);
      this.accountOnlinePanel.setBackground(ProjectBattleAlpha.Colors.TOP);
      this.accountOnlineDot = new JLabel("●");
      this.accountOnlineDot.setForeground(ProjectBattleAlpha.Colors.ERROR);
      this.accountOnlineDot.setFont(new Font("SansSerif", 1, 12));
      this.accountStatusLabel = new JLabel("Live");
      this.accountStatusLabel.setForeground(ProjectBattleAlpha.Colors.TEXT);
      this.accountStatusLabel.setFont(new Font("SansSerif", 1, 12));
      this.accountOnlinePanel.add(this.accountOnlineDot);
      this.accountOnlinePanel.add(this.accountStatusLabel);
      var2.add(this.accountNameLabel, "North");
      var2.add(this.accountOnlinePanel, "South");
      var1.add(var2, "Center");
      ProjectBattleAlpha.GearButton var3 = new ProjectBattleAlpha.GearButton();
      var3.setToolTipText(this.tr("Настройки лаунчера", "Launcher settings"));
      var3.setFont(new Font("SansSerif", 0, 18));
      var3.setPreferredSize(new Dimension(36, 36));
      var3.addActionListener(var1x -> this.openLauncherSettings());
      var1.add(var3, "East");
      this.updateAccountName();
      return var1;
   }

   private JPopupMenu accountMenu() {
      JPopupMenu var1 = new JPopupMenu();
      var1.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
      var1.setBackground(ProjectBattleAlpha.Colors.PANEL);
      var1.add(this.profileMenuItem("◉", this.tr("Аккаунт", "Account"), ProjectBattleAlpha.Colors.PLAY, this::openAccountDialog));
      var1.add(this.profileMenuItem("✉", this.tr("Поддержка", "Support"), ProjectBattleAlpha.Colors.PLAY, this::openSupport));
      if ("admin".equalsIgnoreCase(this.config.authRole)) {
         var1.add(this.profileMenuItem("⚒", this.tr("Панель администратора", "Administrator panel"), ProjectBattleAlpha.Colors.PLAY, this::openAdminPanel));
      }

      var1.add(this.darkSeparator());
      var1.add(this.profileMenuItem("⚙", this.tr("Настройки лаунчера", "Launcher settings"), ProjectBattleAlpha.Colors.PLAY, this::openLauncherSettings));
      var1.add(this.profileMenuItem("✖", this.tr("Закрыть лаунчер", "Close launcher"), ProjectBattleAlpha.Colors.ERROR, () -> this.closeLauncher()));
      var1.add(this.darkSeparator());
      JPanel var2 = new JPanel(new BorderLayout(18, 0));
      var2.setBackground(ProjectBattleAlpha.Colors.PANEL);
      var2.setBorder(new EmptyBorder(7, 8, 7, 8));
      JLabel var3 = new JLabel(this.tr("версия лаунчера", "launcher version"));
      var3.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var3.setFont(new Font("SansSerif", 0, 11));
      JLabel var4 = new JLabel("0.1.78.13");
      var4.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var4.setFont(new Font("SansSerif", 0, 11));
      var2.add(var3, "West");
      var2.add(var4, "East");
      var1.add(var2);
      return var1;
   }

   private JComponent darkSeparator() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.setOpaque(true);
      var1.setBackground(ProjectBattleAlpha.Colors.PANEL);
      var1.setBorder(new EmptyBorder(4, 0, 4, 0));
      JPanel var2 = new JPanel();
      var2.setPreferredSize(new Dimension(1, 1));
      var2.setBackground(new Color(64, 72, 70));
      var1.add(var2, "Center");
      return var1;
   }

   private JComponent profileMenuItem(String var1, String var2, Color var3, final Runnable var4) {
      final JPanel var5 = new JPanel(new BorderLayout(10, 0));
      var5.setOpaque(true);
      var5.setBackground(ProjectBattleAlpha.Colors.PANEL);
      var5.setBorder(new EmptyBorder(8, 10, 8, 24));
      var5.setCursor(launcherCursor(12));
      JLabel var6 = new JLabel(var1);
      var6.setForeground(var3);
      var6.setFont(new Font("SansSerif", 1, 14));
      var6.setPreferredSize(new Dimension(22, 18));
      final JLabel var7 = new JLabel(var2);
      var7.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var7.setFont(new Font("SansSerif", 0, 13));
      var5.add(var6, "West");
      var5.add(var7, "Center");
      var5.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1) {
            var5.setBackground(new Color(31, 38, 37));
            var7.setForeground(ProjectBattleAlpha.Colors.TEXT);
         }

         @Override
         public void mouseExited(MouseEvent var1) {
            var5.setBackground(ProjectBattleAlpha.Colors.PANEL);
            var7.setForeground(ProjectBattleAlpha.Colors.MUTED);
         }

         @Override
         public void mouseClicked(MouseEvent var1) {
            var4.run();
         }
      });
      return var5;
   }

   private void openNicknameDialog() {
      final JTextField var1 = this.input();
      var1.setText(this.config.playerName);
      var1.setPreferredSize(new Dimension(210, 34));
      JButton var2 = this.smallButton("✓");
      var2.setPreferredSize(new Dimension(42, 34));
      final JDialog var3 = new JDialog(this, this.tr("Сменить ник", "Change nickname"), true);
      JPanel var4 = this.panel();
      var4.setBorder(new EmptyBorder(14, 14, 14, 14));
      var4.setLayout(new BorderLayout(8, 8));
      JLabel var5 = new JLabel(this.tr("Ник", "Nickname"));
      var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var5.setFont(new Font("SansSerif", 0, 11));
      JPanel var6 = new JPanel(new BorderLayout(8, 0));
      var6.setOpaque(false);
      var6.add(var1, "Center");
      var6.add(var2, "East");
      var4.add(var5, "North");
      var4.add(var6, "Center");
      var3.setContentPane(var4);
      var3.pack();
      var3.setResizable(false);
      var3.setLocationRelativeTo(this);
      AbstractAction var7 = new AbstractAction() {
         @Override
         public void actionPerformed(ActionEvent var1x) {
            ProjectBattleAlpha.this.saveNickname(var1.getText());
            var3.dispose();
         }
      };
      var2.addActionListener(var7);
      var1.addActionListener(var7);
      var3.setVisible(true);
   }

   private void openAccountDialog() {
      if (this.config.authToken != null && !this.config.authToken.isBlank()) {
         this.openAccountDetailsDialog();
      } else {
         this.openLoginDialog();
      }
   }

   private void openLoginDialog() {
      this.discordPresence.setState("Выполняет вход");
      JDialog var1 = this.authDialog(this.tr("Вход в аккаунт", "Account login"), this.accountLoginImage, 620, 420);
      JPanel var2 = this.authContent();
      var2.add(this.authTitle(this.tr("Вход в аккаунт", "Account login")));
      var2.add(Box.createVerticalStrut(22));
      JTextField var3 = this.input();
      var3.setToolTipText(this.tr("Логин или email", "Login or email"));
      JPasswordField var4 = this.passwordInput();
      var2.add(this.authField(this.tr("Логин или email", "Login or email"), var3));
      var2.add(Box.createVerticalStrut(12));
      var2.add(this.authField(this.tr("Пароль", "Password"), var4));
      var2.add(Box.createVerticalStrut(18));
      JButton var5 = this.smallButton(this.tr("Войти", "Sign in"));
      JButton var6 = this.smallButton(this.tr("Создать аккаунт", "Create account"));
      JButton var7 = this.smallButton(this.tr("Забыли пароль", "Forgot password"));
      JPanel var8 = new JPanel(new BorderLayout(8, 0));
      var8.setOpaque(false);
      JPanel var9 = new JPanel(new FlowLayout(0, 0, 0));
      var9.setOpaque(false);
      var9.add(var6);
      JPanel var10 = new JPanel(new FlowLayout(1, 0, 0));
      var10.setOpaque(false);
      var10.add(var5);
      JPanel var11 = new JPanel(new FlowLayout(2, 0, 0));
      var11.setOpaque(false);
      var11.add(var7);
      var8.add(var9, "West");
      var8.add(var10, "Center");
      var8.add(var11, "East");
      var2.add(var8);
      var5.addActionListener(var5x -> {
         String var6x = var3.getText().trim();
         String var7x = new String(var4.getPassword());
         if (!var6x.isBlank() && !var7x.isBlank()) {
            var5.setEnabled(false);
            this.worker.submit(() -> {
               try {
                  this.authPost("/auth/login", this.json("login", var6x, "password", var7x), null);
                  SwingUtilities.invokeLater(() -> {
                     var1.dispose();
                     this.openLoginCodeDialog(var6x);
                  });
               } catch (Exception var6xx) {
                  this.error(this.tr("Ошибка входа", "Login error"), var6xx);
                  SwingUtilities.invokeLater(() -> var5.setEnabled(true));
               }
            });
         } else {
            JOptionPane.showMessageDialog(var1, this.tr("Введите логин/email и пароль.", "Enter login/email and password."), "Project Battle Alpha", 2);
         }
      });
      var6.addActionListener(var2x -> {
         var1.dispose();
         this.openRegisterDialog();
      });
      var7.addActionListener(var1x -> this.openForgotPasswordDialog());
      var1.setContentPane(this.authRoot(this.accountLoginImage, var2));
      var1.setLocationRelativeTo(this);
      var1.setVisible(true);
   }

   private void openLoginCodeDialog(String var1) {
      JDialog var2 = this.authDialog(this.tr("Код подтверждения", "Verification code"), this.accountLoginImage, 420, 260);
      JPanel var3 = this.authContent();
      var3.add(this.authTitle(this.tr("Код с почты", "Email code")));
      var3.add(Box.createVerticalStrut(12));
      JLabel var4 = new JLabel(this.tr("Мы отправили код на почту аккаунта.", "We sent a code to the account email."));
      var4.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var3.add(var4);
      var3.add(Box.createVerticalStrut(14));
      JTextField var5 = this.input();
      var3.add(this.authField(this.tr("Код", "Code"), var5));
      var3.add(Box.createVerticalStrut(14));
      JButton var6 = this.smallButton(this.tr("Подтвердить", "Confirm"));
      var3.add(var6);
      var6.addActionListener(var5x -> {
         var6.setEnabled(false);
         this.worker.submit(() -> {
            try {
               Map var5xx = this.authPost("/auth/verify-login", this.json("login", var1, "code", var5.getText().trim()), null);
               this.saveAuthFromResponse(var5xx);
               SwingUtilities.invokeLater(() -> {
                  var2.dispose();
                  JOptionPane.showMessageDialog(this, this.tr("Вход выполнен.", "Signed in."), "Project Battle Alpha", 1);
               });
            } catch (Exception var6x) {
               this.error(this.tr("Ошибка кода", "Code error"), var6x);
               SwingUtilities.invokeLater(() -> var6.setEnabled(true));
            }
         });
      });
      var2.setContentPane(this.authRoot(this.accountLoginImage, var3));
      var2.setLocationRelativeTo(this);
      var2.setVisible(true);
   }

   private void openRegisterDialog() {
      this.discordPresence.setState("Создаёт аккаунт");
      JDialog var1 = this.authDialog(this.tr("Создать аккаунт", "Create account"), this.accountRegisterImage, 900, 560);
      JPanel var2 = this.authContent();
      var2.add(this.authTitle(this.tr("Создать аккаунт", "Create account")));
      var2.add(Box.createVerticalStrut(18));
      JTextField var3 = this.input();
      JTextField var4 = this.input();
      JPasswordField var5 = this.passwordInput();
      JPasswordField var6 = this.passwordInput();
      var2.add(this.authField(this.tr("Логин", "Login"), var3));
      var2.add(Box.createVerticalStrut(10));
      var2.add(this.authField("Email", var4));
      var2.add(Box.createVerticalStrut(10));
      var2.add(this.authField(this.tr("Пароль", "Password"), var5));
      JLabel var7 = new JLabel(this.tr("Пароль: от 8 до 128 символов.", "Password must be 8-128 characters."));
      var7.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var7.setFont(new Font("SansSerif", 0, 11));
      var7.setAlignmentX(0.0F);
      var2.add(Box.createVerticalStrut(4));
      var2.add(var7);
      var2.add(Box.createVerticalStrut(10));
      var2.add(this.authField(this.tr("Повторите пароль", "Repeat password"), var6));
      var2.add(Box.createVerticalStrut(18));
      JButton var8 = this.smallButton(this.tr("Создать", "Create"));
      JButton var9 = this.smallButton(this.tr("Назад", "Back"));
      JPanel var10 = new JPanel(new FlowLayout(0, 8, 0));
      var10.setOpaque(false);
      var10.add(var8);
      var10.add(var9);
      var2.add(var10);
      var8.addActionListener(
         var7x -> {
            String var8x = var3.getText().trim();
            String var9x = var4.getText().trim();
            String var10x = new String(var5.getPassword());
            String var11 = new String(var6.getPassword());
            if (var8x.isBlank() || var9x.isBlank() || var10x.isBlank()) {
               JOptionPane.showMessageDialog(var1, this.tr("Заполните логин, email и пароль.", "Fill in login, email and password."), "Project Battle Alpha", 2);
            } else if (!var8x.matches("[A-Za-z0-9_]{3,16}")) {
               JOptionPane.showMessageDialog(
                  var1,
                  this.tr(
                     "Логин будет ником Minecraft: 3-16 символов, только A-Z, 0-9 и _.",
                     "Login will be your Minecraft nickname: 3-16 characters, only A-Z, 0-9 and _."
                  ),
                  "Project Battle Alpha",
                  2
               );
            } else if (var10x.length() < 8 || var10x.length() > 128) {
               JOptionPane.showMessageDialog(
                  var1, this.tr("Пароль должен быть от 8 до 128 символов.", "Password must be 8-128 characters."), "Project Battle Alpha", 2
               );
            } else if (!var10x.equals(var11)) {
               JOptionPane.showMessageDialog(var1, this.tr("Пароли не совпадают.", "Passwords do not match."), "Project Battle Alpha", 2);
            } else {
               var8.setEnabled(false);
               this.worker.submit(() -> {
                  try {
                     this.authPost("/auth/start-register", this.json("email", var9x), null);
                     SwingUtilities.invokeLater(() -> {
                        var1.dispose();
                        this.openRegisterCodeDialog(var9x, var8x, var10x);
                     });
                  } catch (Exception var7xx) {
                     this.error(this.tr("Ошибка регистрации", "Registration error"), var7xx);
                     SwingUtilities.invokeLater(() -> var8.setEnabled(true));
                  }
               });
            }
         }
      );
      var9.addActionListener(var2x -> {
         var1.dispose();
         this.openLoginDialog();
      });
      var1.setContentPane(this.authRoot(this.accountRegisterImage, var2));
      var1.setLocationRelativeTo(this);
      var1.setVisible(true);
   }

   private void openRegisterCodeDialog(String var1, String var2, String var3) {
      JDialog var4 = this.authDialog(this.tr("Код регистрации", "Registration code"), this.accountRegisterImage, 440, 280);
      JPanel var5 = this.authContent();
      var5.add(this.authTitle(this.tr("Подтвердите email", "Verify email")));
      var5.add(Box.createVerticalStrut(12));
      JTextField var6 = this.input();
      var5.add(this.authField(this.tr("Код", "Code"), var6));
      var5.add(Box.createVerticalStrut(14));
      JButton var7 = this.smallButton(this.tr("Подтвердить", "Confirm"));
      var5.add(var7);
      var7.addActionListener(var7x -> {
         var7.setEnabled(false);
         this.worker.submit(() -> {
            try {
               this.authPost("/auth/verify-email", this.json("email", var1, "code", var6.getText().trim()), null);
               Map var7xx = this.authPost("/auth/finish-register", this.json("email", var1, "nickname", var2, "password", var3), null);
               this.saveAuthFromResponse(var7xx);
               SwingUtilities.invokeLater(() -> {
                  var4.dispose();
                  JOptionPane.showMessageDialog(this, this.tr("Аккаунт создан.", "Account created."), "Project Battle Alpha", 1);
               });
            } catch (Exception var8) {
               this.error(this.tr("Ошибка кода", "Code error"), var8);
               SwingUtilities.invokeLater(() -> var7.setEnabled(true));
            }
         });
      });
      var4.setContentPane(this.authRoot(this.accountRegisterImage, var5));
      var4.setLocationRelativeTo(this);
      var4.setVisible(true);
   }

   private void openAccountDetailsDialog() {
      JDialog var1 = this.authDialog(this.tr("Аккаунт", "Account"), this.accountLoginImage, 620, 500);
      JPanel var2 = this.authContent();
      var2.add(this.authTitle(this.tr("Аккаунт Project Battle", "Project Battle Account")));
      var2.add(Box.createVerticalStrut(18));
      var2.add(
         this.authInfo(
            this.tr("Логин:", "Login:"),
            this.config.authNickname != null && !this.config.authNickname.isBlank() ? this.config.authNickname : this.config.playerName
         )
      );
      var2.add(Box.createVerticalStrut(8));
      var2.add(this.authInfo("Email:", this.maskEmail(this.config.authEmail)));
      var2.add(Box.createVerticalStrut(8));
      var2.add(this.authInfo(this.tr("Пароль:", "Password:"), "********"));
      var2.add(Box.createVerticalStrut(16));
      JPanel var3 = this.panel();
      var3.setLayout(new BoxLayout(var3, 1));
      var3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(12, 14, 12, 14)));
      JLabel var4 = new JLabel("Telegram");
      var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var4.setFont(new Font("SansSerif", 1, 14));
      var4.setAlignmentX(0.0F);
      JLabel var5 = new JLabel(this.tr("Проверяем статус...", "Checking status..."));
      var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var5.setFont(new Font("SansSerif", 0, 12));
      var5.setAlignmentX(0.0F);
      JButton var6 = this.smallButton(this.tr("Привязать Telegram", "Link Telegram"));
      var6.setAlignmentX(0.0F);
      var6.setEnabled(false);
      var6.addActionListener(var4x -> this.requestTelegramLink(var1, var6, var5));
      var3.add(var4);
      var3.add(Box.createVerticalStrut(6));
      var3.add(var5);
      var3.add(Box.createVerticalStrut(10));
      var3.add(var6);
      var3.setAlignmentX(0.0F);
      var2.add(var3);
      var2.add(Box.createVerticalStrut(16));
      this.refreshTelegramStatus(var5, var6);
      JButton var7 = this.smallButton(this.tr("Изменить пароль", "Change password"));
      JButton var8 = this.smallButton(this.tr("Выйти с аккаунта", "Sign out of account"));
      JPanel var9 = new JPanel(new BorderLayout(8, 0));
      var9.setOpaque(false);
      JPanel var10 = new JPanel(new FlowLayout(0, 0, 0));
      var10.setOpaque(false);
      var10.add(var8);
      JPanel var11 = new JPanel(new FlowLayout(1, 0, 0));
      var11.setOpaque(false);
      var11.add(var7);
      var9.add(var10, "West");
      var9.add(var11, "Center");
      var2.add(var9);
      var7.addActionListener(var1x -> this.openForgotPasswordDialog());
      var8.addActionListener(var2x -> {
         String var3x = this.config.authToken;
         this.clearAuth();
         var1.dispose();
         this.worker.submit(() -> {
            try {
               if (var3x != null && !var3x.isBlank()) {
                  this.authPost("/auth/logout", "{}", var3x);
               }
            } catch (Exception var3xx) {
            }
         });
      });
      var1.setContentPane(this.authRoot(this.accountLoginImage, var2));
      var1.setLocationRelativeTo(this);
      var1.setVisible(true);
   }

   private void openForgotPasswordDialog() {
      JDialog var1 = this.authDialog(this.tr("Восстановить пароль", "Reset password"), this.accountLoginImage, 460, 300);
      JPanel var2 = this.authContent();
      var2.add(this.authTitle(this.tr("Восстановить пароль", "Reset password")));
      var2.add(Box.createVerticalStrut(16));
      JLabel var3 = new JLabel(this.tr("Введите email аккаунта.", "Enter your account email."));
      var3.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var3.setAlignmentX(0.0F);
      var2.add(var3);
      var2.add(Box.createVerticalStrut(14));
      JTextField var4 = this.input();
      var2.add(this.authField("Email", var4));
      var2.add(Box.createVerticalStrut(16));
      JPanel var5 = new JPanel(new FlowLayout(0, 8, 0));
      var5.setOpaque(false);
      JButton var6 = this.smallButton(this.tr("Отправить код", "Send code"));
      JButton var7 = this.smallButton(this.tr("Отмена", "Cancel"));
      var5.add(var6);
      var5.add(var7);
      var2.add(var5);
      var7.addActionListener(var1x -> var1.dispose());
      var6.addActionListener(var4x -> {
         String var5x = var4.getText().trim();
         if (var5x.isBlank()) {
            JOptionPane.showMessageDialog(var1, this.tr("Введите email.", "Enter email."), "Project Battle Alpha", 2);
         } else {
            var6.setEnabled(false);
            this.worker.submit(() -> {
               try {
                  this.authPost("/auth/forgot-password", this.json("email", var5x), null);
                  SwingUtilities.invokeLater(() -> {
                     var1.dispose();
                     this.openResetPasswordCodeDialog(var5x);
                  });
               } catch (Exception var5xx) {
                  this.error(this.tr("Ошибка пароля", "Password error"), var5xx);
                  SwingUtilities.invokeLater(() -> var6.setEnabled(true));
               }
            });
         }
      });
      var1.setContentPane(this.authRoot(this.accountLoginImage, var2));
      var1.setLocationRelativeTo(this);
      var1.setVisible(true);
   }

   private void openResetPasswordCodeDialog(String var1) {
      JDialog var2 = this.authDialog(this.tr("Код и новый пароль", "Code and new password"), this.accountLoginImage, 480, 390);
      JPanel var3 = this.authContent();
      var3.add(this.authTitle(this.tr("Новый пароль", "New password")));
      var3.add(Box.createVerticalStrut(16));
      JTextField var4 = this.input();
      JPasswordField var5 = this.passwordInput();
      JPasswordField var6 = this.passwordInput();
      var3.add(this.authField(this.tr("Код", "Code"), var4));
      var3.add(Box.createVerticalStrut(10));
      var3.add(this.authField(this.tr("Новый пароль", "New password"), var5));
      var3.add(Box.createVerticalStrut(10));
      var3.add(this.authField(this.tr("Повторите пароль", "Repeat password"), var6));
      var3.add(Box.createVerticalStrut(16));
      JButton var7 = this.smallButton(this.tr("Сменить пароль", "Change password"));
      var3.add(var7);
      var7.addActionListener(var7x -> {
         String var8 = new String(var5.getPassword());
         if (!var4.getText().trim().isBlank() && !var8.isBlank()) {
            if (!var8.equals(new String(var6.getPassword()))) {
               JOptionPane.showMessageDialog(var2, this.tr("Пароли не совпадают.", "Passwords do not match."), "Project Battle Alpha", 2);
            } else {
               var7.setEnabled(false);
               this.worker.submit(() -> {
                  try {
                     this.authPost("/auth/reset-password", this.json("email", var1, "code", var4.getText().trim(), "password", var8), null);
                     SwingUtilities.invokeLater(() -> {
                        var2.dispose();
                        JOptionPane.showMessageDialog(this, this.tr("Пароль изменён.", "Password changed."), "Project Battle Alpha", 1);
                     });
                  } catch (Exception var7xx) {
                     this.error(this.tr("Ошибка пароля", "Password error"), var7xx);
                     SwingUtilities.invokeLater(() -> var7.setEnabled(true));
                  }
               });
            }
         } else {
            JOptionPane.showMessageDialog(var2, this.tr("Введите код и новый пароль.", "Enter code and new password."), "Project Battle Alpha", 2);
         }
      });
      var2.setContentPane(this.authRoot(this.accountLoginImage, var3));
      var2.setLocationRelativeTo(this);
      var2.setVisible(true);
   }

   private JDialog authDialog(String var1, BufferedImage var2, int var3, int var4) {
      JDialog var5 = new JDialog(this, var1, true);
      var5.setSize(var3, var4);
      var5.setResizable(false);
      if (this.iconImage != null) {
         var5.setIconImage(this.iconImage);
      }

      return var5;
   }

   private JPanel authRoot(BufferedImage var1, JPanel var2) {
      ProjectBattleAlpha.SettingsPanel var3 = new ProjectBattleAlpha.SettingsPanel(var1);
      var3.setLayout(new GridBagLayout());
      var3.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
      JPanel var4 = new JPanel(new BorderLayout());
      var4.setOpaque(false);
      var4.setBorder(new EmptyBorder(22, 28, 22, 28));
      var4.add(var2, "Center");
      var3.add(var4, new GridBagConstraints());
      return var3;
   }

   private JPanel authContent() {
      JPanel var1 = new JPanel();
      var1.setOpaque(false);
      var1.setLayout(new BoxLayout(var1, 1));
      return var1;
   }

   private JLabel authTitle(String var1) {
      JLabel var2 = new JLabel(var1);
      var2.setAlignmentX(0.0F);
      var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var2.setFont(new Font("SansSerif", 1, 24));
      return var2;
   }

   private JPanel authField(String var1, JComponent var2) {
      JPanel var3 = new JPanel(new BorderLayout(0, 5));
      var3.setOpaque(false);
      var3.setAlignmentX(0.0F);
      JLabel var4 = new JLabel(var1);
      var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var4.setFont(new Font("SansSerif", 1, 12));
      this.styleAuthInput(var2);
      var2.setPreferredSize(new Dimension(290, 34));
      var3.add(var4, "North");
      var3.add(var2, "Center");
      return var3;
   }

   private void styleAuthInput(JComponent var1) {
      if (var1 instanceof JTextField var2) {
         var2.setOpaque(false);
         var2.setBackground(new Color(0, 0, 0, 0));
         var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var2.setCaretColor(ProjectBattleAlpha.Colors.TEXT);
         var2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(6, 8, 6, 8)));
         var2.setFont(new Font("SansSerif", 1, 12));
      }
   }

   private JPanel authInfo(String var1, String var2) {
      JPanel var3 = new JPanel(new BorderLayout(12, 0));
      var3.setOpaque(false);
      var3.setAlignmentX(0.0F);
      JLabel var4 = new JLabel(var1);
      var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var4.setFont(new Font("SansSerif", 1, 13));
      JLabel var5 = new JLabel(var2 != null && !var2.isBlank() ? var2 : "-");
      var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var5.setFont(new Font("SansSerif", 0, 13));
      var3.add(var4, "West");
      var3.add(var5, "Center");
      return var3;
   }

   private JPasswordField passwordInput() {
      JPasswordField var1 = new JPasswordField();
      var1.setEchoChar('•');
      this.styleAuthInput(var1);
      return var1;
   }

   private Map<?, ?> authPost(String var1, String var2, String var3) throws IOException {
      String var4 = this.config.authApiUrl != null && !this.config.authApiUrl.isBlank()
         ? this.config.authApiUrl.trim()
         : "https://project-battle-auth-api-yp7n.onrender.com";

      while (var4.endsWith("/")) {
         var4 = var4.substring(0, var4.length() - 1);
      }

      HttpURLConnection var5 = (HttpURLConnection)URI.create(var4 + var1).toURL().openConnection();
      var5.setRequestMethod("POST");
      var5.setConnectTimeout(30000);
      var5.setReadTimeout(120000);
      var5.setDoOutput(true);
      var5.setRequestProperty("User-Agent", "Project Battle Alpha/0.1.78.13");
      var5.setRequestProperty("Accept", "application/json");
      var5.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      if (var3 != null && !var3.isBlank()) {
         var5.setRequestProperty("Authorization", "Bearer " + var3);
      }

      try (OutputStream var6 = var5.getOutputStream()) {
         var6.write(var2.getBytes(StandardCharsets.UTF_8));
      }

      int var14 = var5.getResponseCode();
      InputStream var7 = var14 >= 200 && var14 < 300 ? var5.getInputStream() : var5.getErrorStream();
      String var8 = var7 == null ? "{}" : new String(var7.readAllBytes(), StandardCharsets.UTF_8);
      if ((var8.isBlank() ? Map.of() : ProjectBattleAlpha.Json.parse(stripBom(var8))) instanceof Map var9) {
         if (var14 >= 200 && var14 < 300) {
            return var9;
         }

         String var15 = str(var9.get("error"));
         if (var15.isBlank()) {
            var15 = str(var9.get("message"));
         }

         if (var15.isBlank()) {
            var15 = "Auth server error " + var14;
            String var11 = var8 == null ? "" : var8.trim();
            if (!var11.isBlank() && var11.length() < 240) {
               var15 = var15 + ": " + var11;
            }
         }

         throw new IOException(var15);
      } else {
         throw new IOException("Auth server returned bad JSON.");
      }
   }

   private Map<?, ?> authGet(String var1, String var2) throws IOException {
      String var3 = this.config.authApiUrl != null && !this.config.authApiUrl.isBlank()
         ? this.config.authApiUrl.trim()
         : "https://project-battle-auth-api-yp7n.onrender.com";

      while (var3.endsWith("/")) {
         var3 = var3.substring(0, var3.length() - 1);
      }

      HttpURLConnection var4 = (HttpURLConnection)URI.create(var3 + var1).toURL().openConnection();
      var4.setRequestMethod("GET");
      var4.setConnectTimeout(30000);
      var4.setReadTimeout(120000);
      var4.setRequestProperty("User-Agent", "Project Battle Alpha/0.1.78.13");
      var4.setRequestProperty("Accept", "application/json");
      if (var2 != null && !var2.isBlank()) {
         var4.setRequestProperty("Authorization", "Bearer " + var2);
      }

      int var5 = var4.getResponseCode();
      InputStream var6 = var5 >= 200 && var5 < 300 ? var4.getInputStream() : var4.getErrorStream();
      String var7 = var6 == null ? "{}" : new String(var6.readAllBytes(), StandardCharsets.UTF_8);
      if ((var7.isBlank() ? Map.of() : ProjectBattleAlpha.Json.parse(stripBom(var7))) instanceof Map var9) {
         if (var5 >= 200 && var5 < 300) {
            return var9;
         }

         String var10 = str(var9.get("error"));
         if (var10.isBlank()) {
            var10 = str(var9.get("message"));
         }

         throw new IOException(var10.isBlank() ? "Auth server error " + var5 : var10);
      } else {
         throw new IOException("Auth server returned bad JSON.");
      }
   }

   private void refreshTelegramStatus(JLabel var1, JButton var2) {
      this.refreshTelegramStatus(var1, var2, null, null);
   }

   private void refreshTelegramStatus(JLabel var1, JButton var2, JButton unlink, JButton relink) {
      String var3 = this.config.authToken;
      if (var3 != null && !var3.isBlank()) {
         var1.setText(this.tr("Проверяем статус...", "Checking status..."));
         var2.setEnabled(false);
         this.profileWorker
            .submit(
               () -> {
                  try {
                     Map var4 = this.authGet("/auth/me", var3);
                     Object var5 = var4.get("user");
                     boolean var6 = var5 instanceof Map var7
                        && (Boolean.TRUE.equals(var7.get("telegramLinked")) || "true".equalsIgnoreCase(str(var7.get("telegramLinked"))));
                     if (var5 instanceof Map var12) {
                        String var8 = str(var12.get("role"));
                        this.config.authRole = var8.isBlank() ? "user" : var8;
                        String var9 = sanitizeName(str(var12.get("nickname")));
                        if (!var9.isBlank()) {
                           this.config.authNickname = var9;
                           this.config.playerName = var9;
                        }

                        String var10 = str(var12.get("email"));
                        if (!var10.isBlank()) {
                           this.config.authEmail = var10;
                        }

                        this.config.save();
                     }

                     SwingUtilities.invokeLater(() -> {
                        var1.setText(var6 ? this.tr("Telegram подключён", "Telegram connected") : this.tr("Telegram не подключён", "Telegram not connected"));
                        var1.setForeground(var6 ? ProjectBattleAlpha.Colors.PLAY : ProjectBattleAlpha.Colors.MUTED);
                        var2.setVisible(!var6);
                        var2.setEnabled(!var6);
                        if (unlink != null) { unlink.setVisible(var6); unlink.setEnabled(var6); }
                        if (relink != null) { relink.setVisible(var6); relink.setEnabled(var6); }
                        if (var1.getParent() != null) {
                           var1.getParent().revalidate();
                           var1.getParent().repaint();
                        }
                     });
                  } catch (Exception var11) {
                     SwingUtilities.invokeLater(() -> {
                        var1.setText(this.tr("Не удалось проверить статус Telegram.", "Could not check Telegram status."));
                        var1.setForeground(ProjectBattleAlpha.Colors.MUTED);
                        var2.setVisible(true);
                        var2.setEnabled(true);
                        if (unlink != null) unlink.setEnabled(false);
                        if (relink != null) relink.setEnabled(false);
                     });
                  }
               }
            );
      } else {
         var1.setText(this.tr("Войдите в аккаунт, чтобы привязать Telegram.", "Sign in to link Telegram."));
         var2.setVisible(false);
         if (unlink != null) unlink.setVisible(false);
         if (relink != null) relink.setVisible(false);
      }
   }

   private void unlinkTelegramLink(Component parent, JLabel status, JButton link, JButton unlink, JButton relink, boolean rebind) {
      JPasswordField password = new JPasswordField(24);
      int answer = JOptionPane.showConfirmDialog(parent, password,
            this.tr("Подтвердите пароль аккаунта для управления Telegram", "Confirm your account password to manage Telegram"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if (answer != JOptionPane.OK_OPTION) return;
      char[] secret = password.getPassword();
      String passwordText = new String(secret);
      Arrays.fill(secret, '\0');
      status.setText(this.tr("Проверяем владельца аккаунта...", "Verifying account owner..."));
      unlink.setEnabled(false);
      relink.setEnabled(false);
      this.profileWorker.submit(() -> {
         try {
            this.authPost("/auth/telegram/unlink", this.json("password", passwordText), this.config.authToken);
            SwingUtilities.invokeLater(() -> {
               status.setText(this.tr("Telegram отключён", "Telegram disconnected"));
               link.setVisible(true);
               link.setEnabled(true);
               unlink.setVisible(false);
               relink.setVisible(false);
               if (rebind) this.requestTelegramLink(parent, link, status);
            });
         } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
               status.setText(this.tr("Не удалось отключить Telegram: ", "Could not disconnect Telegram: ") + ex.getMessage());
               unlink.setEnabled(true);
               relink.setEnabled(true);
            });
         }
      });
   }

   private void refreshSessionProfileAsync() {
      String var1 = this.config.authToken;
      if (var1 != null && !var1.isBlank()) {
         this.profileWorker.submit(() -> {
            try {
               Map var2 = this.authGet("/auth/me", var1);
               if (var2.get("user") instanceof Map var4) {
                  String var5 = str(var4.get("role"));
                  this.config.authRole = var5.isBlank() ? "user" : var5;
                  String var6 = sanitizeName(str(var4.get("nickname")));
                  if (!var6.isBlank()) {
                     this.config.authNickname = var6;
                     this.config.playerName = var6;
                  }

                  String var7 = str(var4.get("email"));
                  if (!var7.isBlank()) {
                     this.config.authEmail = var7;
                  }

                  this.config.save();
                  SwingUtilities.invokeLater(this::updateAccountName);
               }
            } catch (Exception var8) {
               this.log("Account profile refresh skipped: " + var8.getMessage());
            }
         });
      }
   }

   private void requestTelegramLink(Component var1, JButton var2, JLabel var3) {
      String var4 = this.config.authToken;
      if (var4 != null && !var4.isBlank()) {
         var2.setEnabled(false);
         var3.setText(this.tr("Создаём ссылку...", "Creating link..."));
         this.profileWorker
            .submit(
               () -> {
                  try {
                     Map var5 = this.authPost("/auth/telegram/link-code", "{}", var4);
                     String var6 = str(var5.get("link"));
                     this.openTelegramLinkUrl(var6);
                     SwingUtilities.invokeLater(
                        () -> {
                           var3.setForeground(ProjectBattleAlpha.Colors.MUTED);
                           var3.setText(this.tr("Ожидается подтверждение в Telegram.", "Waiting for confirmation in Telegram."));
                           var2.setEnabled(true);
                           JOptionPane.showMessageDialog(
                              var1,
                              this.tr(
                                 "Откройте Telegram и нажмите Start, чтобы подтвердить привязку.\nСсылка действует 10 минут",
                                 "Open Telegram and press Start to confirm linking.\nThe link is valid for 10 minutes"
                              ),
                              "Telegram",
                              1
                           );
                        }
                     );
                  } catch (Exception var7) {
                     SwingUtilities.invokeLater(() -> {
                        var3.setText(this.tr("Telegram не подключён", "Telegram not connected"));
                        var2.setEnabled(true);
                        this.error(this.tr("Не удалось привязать Telegram", "Could not link Telegram"), var7);
                     });
                  }
               }
            );
      } else {
         JOptionPane.showMessageDialog(var1, this.tr("Сначала войдите в аккаунт.", "Please sign in first."), "Telegram", 2);
      }
   }

   private void openTelegramUrl(String var1) throws IOException {
      URI var2;
      try {
         var2 = URI.create(var1 == null ? "" : var1.trim());
      } catch (IllegalArgumentException var4) {
         throw new IOException("Telegram link is invalid.", var4);
      }

      if ("https".equalsIgnoreCase(var2.getScheme()) && "t.me".equalsIgnoreCase(var2.getHost())) {
         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            Desktop.getDesktop().browse(var2);
         } else {
            throw new IOException("System browser is not available.");
         }
      } else {
         throw new IOException("Telegram link is invalid.");
      }
   }

   private void openTelegramLinkUrl(String var1) throws IOException {
      URI var2;
      try {
         var2 = URI.create(var1 == null ? "" : var1.trim());
      } catch (IllegalArgumentException var5) {
         throw new IOException("Telegram account link is invalid.", var5);
      }

      String var3 = var2.getPath() == null ? "" : var2.getPath();
      String var4 = var2.getRawQuery() == null ? "" : var2.getRawQuery();
      if (!"https".equalsIgnoreCase(var2.getScheme())
         || !"t.me".equalsIgnoreCase(var2.getHost())
         || var3.length() <= 1
         || !var4.matches("start=link_[A-Z2-9]{8}")) {
         throw new IOException("Auth server did not return a one-time Telegram account link.");
      }

      if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
         Desktop.getDesktop().browse(var2);
      } else {
         throw new IOException("System browser is not available.");
      }
   }

   private void openTelegramSupport(Component var1) {
      try {
         this.openTelegramUrl("https://t.me/ProjectBattleSupport_bot");
      } catch (Exception var3) {
         this.error(this.tr("Не удалось открыть Telegram-поддержку", "Could not open Telegram support"), var3);
      }
   }

   private void openPortal(String var1, String var2) {
      try {
         URI var3 = URI.create(var1);
         if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.BROWSE)) {
            throw new IOException("System browser is not available.");
         }

         Desktop.getDesktop().browse(var3);
      } catch (Exception var4) {
         this.error(var2, var4);
      }
   }

   private String json(String... var1) {
      StringBuilder var2 = new StringBuilder("{");

      for (byte var3 = 0; var3 + 1 < var1.length; var3 += 2) {
         if (var3 > 0) {
            var2.append(',');
         }

         var2.append(this.jsonQuote(var1[var3])).append(':').append(this.jsonQuote(var1[var3 + 1]));
      }

      return var2.append('}').toString();
   }

   private String jsonQuote(String var1) {
      String var2 = var1 == null ? "" : var1;
      StringBuilder var3 = new StringBuilder("\"");

      for (int var4 = 0; var4 < var2.length(); var4++) {
         char var5 = var2.charAt(var4);
         switch (var5) {
            case '\b':
               var3.append("\\b");
               break;
            case '\t':
               var3.append("\\t");
               break;
            case '\n':
               var3.append("\\n");
               break;
            case '\f':
               var3.append("\\f");
               break;
            case '\r':
               var3.append("\\r");
               break;
            case '"':
               var3.append("\\\"");
               break;
            case '\\':
               var3.append("\\\\");
               break;
            default:
               if (var5 < ' ') {
                  var3.append(String.format("\\u%04x", Integer.valueOf(var5)));
               } else {
                  var3.append(var5);
               }
         }
      }

      return var3.append('"').toString();
   }

   private void saveAuthFromResponse(Map<?, ?> var1) throws IOException {
      Object var2 = var1.get("user");
      Object var3 = var1.get("session");
      if (var2 instanceof Map var4) {
         this.config.authEmail = str(var4.get("email"));
         this.config.authNickname = sanitizeName(str(var4.get("nickname")));
         this.config.authRole = str(var4.get("role"));
         if (this.config.authRole.isBlank()) {
            this.config.authRole = "user";
         }

         if (!this.config.authNickname.isBlank()) {
            this.config.playerName = this.config.authNickname;
         }
      }

      if (var3 instanceof Map var5) {
         this.config.authToken = str(var5.get("token"));
      }

      this.config.save();
      SwingUtilities.invokeLater(() -> {
         this.updateAccountName();
         this.loadConfig();
      });
   }

   private void clearAuth() {
      this.config.authToken = "";
      this.config.authEmail = "";
      this.config.authNickname = "";
      this.config.authRole = "user";

      try {
         this.config.save();
      } catch (IOException var2) {
      }

      SwingUtilities.invokeLater(this::updateAccountName);
   }

   private String maskEmail(String var1) {
      if (var1 != null && !var1.isBlank()) {
         int var2 = var1.indexOf(64);
         if (var2 <= 0) {
            return "*****";
         }

         String var3 = var1.substring(0, var2);
         String var4 = var1.substring(var2);
         String var5 = var3.length() <= 5 ? "" : var3.substring(5);
         return "*****" + var5 + var4;
      } else {
         return "-";
      }
   }

   private void openSupport() {
      JDialog var1 = new JDialog(this, "Project Battle Support", false);
      var1.setUndecorated(true);
      JPanel var2 = this.panel();
      var2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(14, 16, 16, 16)));
      var2.setLayout(new BorderLayout(0, 14));
      JLabel var3 = new JLabel("\ud83d\udd27  Project Battle Support");
      var3.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var3.setFont(new Font("SansSerif", 1, 18));
      JButton var4 = this.windowButton("×");
      var4.addActionListener(var1x -> var1.dispose());
      JPanel var5 = new JPanel(new BorderLayout());
      var5.setOpaque(false);
      var5.add(var3, "West");
      var5.add(var4, "East");
      JLabel var6 = new JLabel("ourffers@gmail.com");
      var6.setForeground(ProjectBattleAlpha.Colors.PLAY);
      var6.setFont(new Font("SansSerif", 1, 14));
      JButton var7 = this.smallButton(this.tr("Открыть Telegram-поддержку", "Open Telegram support"));
      var7.addActionListener(var2x -> this.openTelegramSupport(var1));
      JButton var8 = this.smallButton("⎘");
      var8.setPreferredSize(new Dimension(34, 30));
      var8.setToolTipText(this.tr("Скопировать почту", "Copy email"));
      var8.addActionListener(var2x -> {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("ourffers@gmail.com"), null);
         this.log("Support email copied.");
         var8.setText("✓");
         var8.setToolTipText(this.tr("Email скопирован", "Email copied"));
         Timer var3x = new Timer(1000, var2xx -> {
            var8.setText("⎘");
            var8.setToolTipText(this.tr("Скопировать почту", "Copy email"));
         });
         var3x.setRepeats(false);
         var3x.start();
      });
      JPanel var9 = new JPanel(new BorderLayout(8, 0));
      var9.setOpaque(false);
      var9.add(var6, "Center");
      var9.add(var8, "East");
      JPanel var10 = new JPanel();
      var10.setOpaque(false);
      var10.setLayout(new BoxLayout(var10, 1));
      var10.add(var9);
      var10.add(Box.createVerticalStrut(10));
      var10.add(var7);
      var2.add(var5, "North");
      var2.add(var10, "Center");
      var1.setContentPane(var2);
      var1.pack();
      var1.setResizable(false);
      var1.setLocationRelativeTo(this);
      var1.setVisible(true);
   }

   private void openAdminPanel() {
      this.openAdminPanel(this);
   }

   private void openAdminPanel(Window var1) {
      if (!"admin".equalsIgnoreCase(this.config.authRole)) {
         JOptionPane.showMessageDialog(this, this.tr("Доступ разрешён только администратору.", "Administrator access required."), "Project Battle Alpha", 2);
      } else {
         JDialog var2 = var1 instanceof JDialog
            ? new JDialog((JDialog)var1, this.tr("Панель администратора", "Administrator panel"), false)
            : new JDialog(this, this.tr("Панель администратора", "Administrator panel"), false);
         var2.setSize(780, 560);
         var2.setResizable(true);
         if (this.iconImage != null) {
            var2.setIconImage(this.iconImage);
         }

         ProjectBattleAlpha.SettingsPanel var3 = new ProjectBattleAlpha.SettingsPanel(this.settingsImage);
         var3.setLayout(new BorderLayout(0, 14));
         var3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(20, 22, 20, 22)));
         JLabel var4 = new JLabel(this.tr("Панель администратора  |  PB", "Administrator panel  |  PB"));
         var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var4.setFont(new Font("SansSerif", 1, 22));
         JTextArea var5 = new JTextArea(this.logArea.getDocument(), "", 0, 0);
         var5.setEditable(false);
         var5.setLineWrap(true);
         var5.setWrapStyleWord(true);
         var5.setBackground(ProjectBattleAlpha.Colors.PANEL);
         var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var5.setFont(new Font("Monospaced", 0, 12));
         var5.setBorder(new EmptyBorder(10, 12, 10, 12));
         JScrollPane var6 = new JScrollPane(var5);
         var6.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
         var6.getVerticalScrollBar().setUnitIncrement(16);
         JButton var7 = this.smallButton(this.tr("Скопировать логи", "Copy logs"));
         var7.addActionListener(var1x -> {
            var5.selectAll();
            var5.copy();
            var5.select(0, 0);
         });
         JButton var8 = this.smallButton(this.tr("Закрыть", "Close"));
         var8.addActionListener(var1x -> var2.dispose());
         JButton var9 = this.smallButton(this.tr("Открыть папку", "Open folder"));
         var9.addActionListener(var1x -> {
            try {
               Files.createDirectories(this.config.instanceDir);
               Desktop.getDesktop().open(this.config.instanceDir.toFile());
            } catch (Exception var3x) {
               this.error(this.tr("Не удалось открыть папку", "Could not open folder"), var3x);
            }
         });
         JPanel var10 = new JPanel(new BorderLayout());
         var10.setOpaque(false);
         JPanel var11 = new JPanel(new FlowLayout(0, 0, 0));
         var11.setOpaque(false);
         var11.add(var9);
         JPanel var12 = new JPanel(new FlowLayout(2, 10, 0));
         var12.setOpaque(false);
         var12.add(var7);
         var12.add(var8);
         var10.add(var11, "West");
         var10.add(var12, "East");
         var3.add(var4, "North");
         var3.add(var6, "Center");
         var3.add(var10, "South");
         var2.setContentPane(var3);
         var2.setLocationRelativeTo(this);
         this.fadeInDialog(var2);
         var2.setVisible(true);
      }
   }

   private void fadeInDialog(JDialog var1) {
      try {
         var1.setOpacity(0.0F);
         Timer var2 = new Timer(16, null);
         var2.addActionListener(var2x -> {
            float var3x = Math.min(1.0F, var1.getOpacity() + 0.12F);
            var1.setOpacity(var3x);
            if (var3x >= 1.0F) {
               var2.stop();
            }
         });
         var2.start();
      } catch (UnsupportedOperationException | IllegalComponentStateException var3) {
      }
   }

   private void openLauncherSettings() {
      this.openLauncherSettings("general");
   }

   private void openLauncherSettings(String var1) {
      this.discordPresence.setState(this.tr("Настраивает лаунчер", "Configuring launcher"));
      JDialog var2 = new JDialog(this, this.tr("Настройки лаунчера", "Launcher settings"), true);
      var2.setUndecorated(true);
      var2.setSize(Math.max(980, this.getWidth()), Math.max(640, this.getHeight()));
      var2.setContentPane(new ProjectBattleAlpha.SettingsWorkspace(var2, var1));
      var2.setLocationRelativeTo(this);
      var2.setVisible(true);
   }

   private JLabel settingsTitle(String var1) {
      JLabel var2 = new JLabel(var1);
      var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var2.setFont(new Font("SansSerif", 1, 18));
      var2.setAlignmentX(0.0F);
      return var2;
   }

   private JLabel settingLabel(String var1) {
      JLabel var2 = new JLabel(var1);
      var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var2.setFont(new Font("SansSerif", 1, 13));
      var2.setPreferredSize(new Dimension(190, 28));
      return var2;
   }

   private JPanel settingsRow(String var1, JComponent var2, int var3) {
      JPanel var4 = new JPanel(new BorderLayout(0, 6));
      var4.setOpaque(false);
      var4.setAlignmentX(0.0F);
      var4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
      JLabel var5 = this.settingLabel(var1);
      var5.setAlignmentX(0.0F);
      var4.add(var5, "North");
      Dimension var6 = new Dimension(var3, 38);
      var2.setMinimumSize(var6);
      var2.setPreferredSize(var6);
      var2.setMaximumSize(var6);
      var2.setAlignmentX(0.0F);
      JPanel var7 = new JPanel(new FlowLayout(0, 0, 0));
      var7.setOpaque(false);
      var7.setAlignmentX(0.0F);
      var7.add(var2);
      var4.add(var7, "Center");
      return var4;
   }

   private JComboBox<String> settingsCombo(String[] var1) {
      ProjectBattleAlpha.DarkComboBox var2 = new ProjectBattleAlpha.DarkComboBox(var1);
      var2.setFont(new Font("SansSerif", 1, 12));
      var2.setPreferredSize(new Dimension(360, 30));
      var2.setMaximumRowCount(6);
      return var2;
   }

   private boolean english() {
      return "en".equalsIgnoreCase(this.config.language);
   }

   private String tr(String var1, String var2) {
      return this.english() ? var2 : var1;
   }

   private void applyLanguage() {
      SwingUtilities.invokeLater(this::applyProjectModeControls);
      if (this.gameNav != null) {
         this.gameNav.setText(this.tr("ИГРА", "GAME"));
      }

      if (this.newsNav != null) {
         this.newsNav.setText(this.tr("НОВОСТИ ↗", "NEWS ↗"));
      }

      if (this.rulesNav != null) {
         this.rulesNav.setText(this.tr("ПРАВИЛА", "RULES"));
      }

      if (this.etsNav != null) {
         this.etsNav.setText("ETS");
      }

      if (this.faqNav != null) {
         this.faqNav.setText("FAQ");
      }

      if (this.forumNav != null) {
         this.forumNav.setText(this.tr("ФОРУМ ↗", "FORUM ↗"));
      }

      if (this.extensionsNav != null) {
         this.extensionsNav.setText(this.tr("РАСШИРЕНИЯ", "EXTENSIONS"));
      }

      if (this.updateButton != null) {
         this.updateButton.setText(this.tr("Загрузить", "Download"));
      }

      if (this.refreshButton != null) {
         this.refreshButton.setText(this.tr("Обновить", "Update"));
      }

      if (this.saveButton != null) {
         this.saveButton.setText(this.tr("Сохранить", "Save"));
      }

      this.updateAccountName();
      if (this.playButton != null) {
         this.playButton
            .setText(
               this.minecraftRunning
                  ? this.tr("Запущено", "Running")
                  : (this.updateAvailable ? this.tr("Скачать \ud83d\udce5", "Download \ud83d\udce5") : this.tr("Играть", "Play"))
            );
      }

      if (this.versionLabel != null) {
         this.versionLabel
            .setText(infoHtml(this.tr("Версия лаунчера:", "Launcher version:"), this.tr("0.1.78.13", "0.1.78.13"), ProjectBattleAlpha.Colors.MUTED));
      }

      if (this.editionLabel != null) {
         this.editionLabel.setText(infoHtml(this.tr("Издание:", "Edition:"), "Standard", ProjectBattleAlpha.Colors.MUTED));
      }

      if (this.serverStatusLabel != null) {
         this.updateServerStatus("ETS ▾", ProjectBattleAlpha.Colors.PLAY);
      }
   }

   private JPanel buildStage() {
      JPanel var1 = this.arenaMode ? new ProjectBattleAlpha.BackgroundPanel(null, this.activeStageBackground()) : new ProjectBattleAlpha.HeroPanel(null, null);
      JPanel var2 = this.buildConfigStrip();
      var2.setBounds(482, 516, 668, 104);
      var1.add(var2);
      this.updateNoticeLabel = new JLabel("");
      this.updateNoticeLabel.setForeground(ProjectBattleAlpha.Colors.ERROR);
      this.updateNoticeLabel.setFont(new Font("SansSerif", 1, 11));
      this.updateNoticeLabel.setBounds(494, 476, 645, 24);
      this.updateNoticeLabel.setVisible(false);
      var1.add(this.updateNoticeLabel);
      return var1;
   }

   private JPanel bsgPromoCard(BufferedImage var1, String var2, String var3, boolean var4) {
      ProjectBattleAlpha.BsgPromoCardPanel var5 = new ProjectBattleAlpha.BsgPromoCardPanel(var1, var4);
      var5.setLayout(new BorderLayout());
      var5.setBorder(new EmptyBorder(0, 0, 0, 0));
      if (!var2.isBlank() || !var3.isBlank()) {
         JPanel var6 = new JPanel();
         var6.setOpaque(false);
         var6.setLayout(new BoxLayout(var6, 1));
         var6.setBorder(new EmptyBorder(0, 14, 12, 14));
         JLabel var7 = new JLabel("<html>" + var2 + "</html>");
         var7.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var7.setFont(new Font("SansSerif", 1, var4 ? 25 : 18));
         JTextArea var8 = new JTextArea(var3);
         var8.setOpaque(false);
         var8.setEditable(false);
         var8.setLineWrap(true);
         var8.setWrapStyleWord(true);
         var8.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var8.setFont(new Font("SansSerif", 0, 13));
         var8.setMaximumSize(new Dimension(Integer.MAX_VALUE, var4 ? 42 : 54));
         var6.add(var7);
         if (!var3.isBlank()) {
            var6.add(Box.createVerticalStrut(8));
            var6.add(var8);
         }

         var5.add(var6, "South");
      }

      return var5;
   }

   private JPanel buildNewsStage() {
      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(26, 36, 22, 36));
      JPanel var2 = new JPanel();
      var2.setOpaque(false);
      var2.setLayout(new BoxLayout(var2, 1));
      var2.add(
         this.newsArticlePanel(
            LocalDate.of(2026, 7, 15),
            this.tr("Project Battle: обновления после первой тестовой игры", "Project Battle: updates after the first test game"),
            this.tr(
               "Доброго времени суток!\n\nПосле проведения первой тестовой игры мы выпустили ряд обновлений для Project Battle.\n\nОсновной упор был сделан на исправление проблем с лаунчером, повышение стабильности сборки и устранение ошибок, выявленных во время первого запуска проекта в реальных игровых условиях.\n\nНапоминаем, что Project Battle всё ещё находится на стадии альфа-тестирования. Некоторые технические проблемы могут сохраняться или возникать у отдельных игроков из-за различий в системе, настройках и оборудовании. Мы продолжаем собирать информацию об ошибках и постепенно их исправлять.\n\nТакже была проведена работа над оптимизацией проекта и совместимостью модификаций.\n\nОсновные изменения:\n\nулучшена стабильность лаунчера;\nпроведена дополнительная оптимизация сборки;\nисправлена работа отдельных модификаций;\nисправлена работа шейдеров;\nобновлены меню загрузки, паузы и элементы интерфейса;\nдобавлен компас с отображением азимута;\nвнесены изменения в кит-наборы и состав снаряжения;\nдобавлен мод, усложняющий разрушение блоков без подходящего инструмента;\nв кит-наборы добавлены лопаты и другие инструменты;\nдобавлены мешки с песком, доски и блоки для строительства укреплений.\n\nТеперь разрушать окружение без подходящего инструмента стало значительно сложнее. Это сделано, чтобы здания и позиции нельзя было слишком легко разобрать вручную, а строительство требовало подготовки и соответствующего снаряжения.\n\nМы продолжаем работать над найденными проблемами, игровыми механиками и общей стабильностью проекта.\n\nСпасибо всем, кто принял участие в первой игре и помог обнаружить ошибки.\n\nPB | Team\n",
               "Good day, everyone!\n\nAfter the first test game, we released a series of updates for Project Battle.\n\nOur main focus was fixing launcher issues, improving build stability, and resolving problems discovered during the first real-world project launch.\n\nProject Battle is still in alpha testing. Some technical issues may remain or affect individual players because of differences in systems, settings, and hardware. We continue collecting reports and fixing them step by step.\n\nWe also worked on project optimization and mod compatibility.\n\nMain changes:\n\nimproved launcher stability;\nadditional build optimization;\nfixes for several mods;\nimproved shader launching and operation;\nupdated loading, pause, and interface screens;\ncompass with azimuth display;\nupdated kits and equipment for all roles;\nslower manual block breaking without the right tool;\nshovels and other tools added to kits;\nsandbags, boards, and building blocks added for fortifications and positions.\n\nBreaking the environment without the right tool is now considerably harder. This keeps buildings and positions from being dismantled too easily and makes construction and fortification work require preparation and suitable equipment.\n\nWe continue working on reported issues, gameplay mechanics, and overall project stability.\n\nThank you to everyone who joined the first game and helped us find problems.\n\nPB | Team\n"
            )
         )
      );
      var2.add(Box.createVerticalStrut(18));
      var2.add(
         this.newsArticlePanel(
            LocalDate.of(2026, 6, 19),
            this.tr("Возвращение Project Battle: AAS уже в разработке", "Project Battle Returns: AAS is already in development"),
            this.newsText()
         )
      );
      var2.add(Box.createVerticalStrut(18));
      var2.add(
         this.newsArticlePanel(
            LocalDate.of(2026, 6, 22),
            this.tr("Работа над проектом продолжается", "Development of the project continues"),
            this.tr(
               "Приветствуем всех!\n\nРабота над нашим проектом продолжается полным ходом. Каждый день мы дорабатываем уже существующие системы и добавляем новый контент.\n\nНа данный момент была проведена работа по исправлению ряда ошибок и улучшению стабильности проекта. Также продолжается настройка игровых механик и подготовка контента к будущим тестам.\n\nНа скриншоте вы можете увидеть пример системы классов и кит-наборов, которые будут доступны игрокам после запуска обновления. Каждый класс получит своё уникальное вооружение, снаряжение и роль на поле боя, что позволит сделать игровой процесс более разнообразным и командным.\n\nУже совсем скоро мы сможем поделиться дополнительной информацией о первом тестовом запуске и пригласить игроков принять участие в проверке новых механик.\n\nPB | Team\n",
               "Welcome everyone!\n\nWork on our project is moving forward at full speed. Every day we improve existing systems and add new content.\n\nWe have fixed a number of issues and improved project stability. Gameplay mechanics are also being tuned and content is being prepared for future tests.\n\nThe screenshot shows an example of the class and kit system that will be available after the update. Each class will have unique weapons, equipment, and a role on the battlefield, making gameplay more varied and team-focused.\n\nSoon we will share more information about the first test launch and invite players to help test the new mechanics.\n\nPB | Team\n"
            )
         )
      );
      var2.add(Box.createVerticalStrut(18));
      var2.add(
         this.newsArticlePanel(
            LocalDate.of(2026, 7, 11),
            this.tr("Project Battle приближается к альфа-тесту", "Project Battle is approaching its alpha test"),
            this.tr(
               "Приветствуем всех!\n\nКак некоторые из вас уже могли заметить, работа над Project Battle продолжается полным ходом.\n\nОсновная работа над игровым сервером уже завершена. Сейчас мы вносим небольшие технические корректировки, исправляем оставшиеся ошибки и подготавливаем проект к стабильной работе во время будущих тестов.\n\nПараллельно продолжается активная разработка собственного лаунчера Project Battle. В данный момент мы работаем над системой аккаунтов, благодаря которой каждый игрок сможет создать собственный аккаунт проекта, скачать готовый архив с лаунчером и в дальнейшем обновлять моды и игровые файлы буквально нажатием одной кнопки.\n\nВ лаунчер уже добавлена интеграция с Discord. Во время нахождения в лаунчере или запуска игры в профиле пользователя будет отображаться активность на нашем проекте.\n\nЭто лишь небольшая часть того, что сейчас находится в разработке. Постепенно мы завершаем последние технические этапы и приближаемся к первому полноценному альфа-тесту проекта.\n\nPB | Team\n",
               "Welcome everyone!\n\nAs some of you may have noticed, work on Project Battle is moving forward at full speed.\n\nThe main work on the game server is complete. We are now making final technical adjustments, fixing remaining issues, and preparing the project for stable future tests.\n\nWe are also actively developing the Project Battle launcher. The account system will let every player create a project account, download the launcher archive, and update mods and game files with one click.\n\nDiscord integration has also been added to the launcher. Your profile will show activity while you are in the launcher or playing the game.\n\nThis is only a small part of what is currently in development. We are finishing the last technical stages and moving closer to the first full alpha test.\n\nPB | Team\n"
            )
         )
      );
      var2.add(Box.createVerticalGlue());
      Component[] var3 = var2.getComponents();
      if (var3.length >= 7) {
         var2.removeAll();
         var2.add(var3[0]);
         var2.add(Box.createVerticalStrut(18));
         var2.add(var3[6]);
         var2.add(Box.createVerticalStrut(18));
         var2.add(var3[4]);
         var2.add(Box.createVerticalStrut(18));
         var2.add(var3[2]);
         var2.add(Box.createVerticalGlue());
      }

      JScrollPane var4 = new JScrollPane(var2);
      var4.setOpaque(false);
      var4.getViewport().setOpaque(false);
      var4.setBorder(null);
      var4.getVerticalScrollBar().setUnitIncrement(16);
      var1.add(var4, "Center");
      return var1;
   }

   private JPanel buildRulesStage() {
      if (this.arenaMode) {
         return this.buildArenaUnavailableStage(
            this.tr("ПРАВИЛА PROJECT BATTLE ARENA", "PROJECT BATTLE ARENA RULES"),
            this.tr("Правила режима появятся ближе к открытию альфа-теста.", "Mode rules will be published closer to the alpha test.")
         );
      }

      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(34, 42, 34, 42));
      JPanel var2 = this.panel();
      var2.setLayout(new BorderLayout(0, 16));
      var2.setBorder(new EmptyBorder(22, 24, 22, 24));
      JLabel var3 = new JLabel(this.tr("Правила игры сервера Project Battle AAS", "Project Battle AAS Server Rules"));
      var3.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var3.setFont(new Font("SansSerif", 1, 24));
      JLabel var4 = new JLabel("PB | team");
      var4.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var4.setFont(new Font("SansSerif", 1, 13));
      JPanel var5 = new JPanel();
      var5.setOpaque(false);
      var5.setLayout(new BoxLayout(var5, 1));
      var5.add(var3);
      var5.add(Box.createVerticalStrut(5));
      var5.add(var4);
      JTextArea var6 = new JTextArea(this.rulesText());
      var6.setOpaque(false);
      var6.setEditable(false);
      var6.setLineWrap(true);
      var6.setWrapStyleWord(true);
      var6.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var6.setFont(new Font("SansSerif", 0, 14));
      var6.setBorder(new EmptyBorder(0, 2, 0, 12));
      JScrollPane var7 = new JScrollPane(var6);
      var7.setOpaque(false);
      var7.getViewport().setOpaque(false);
      var7.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ProjectBattleAlpha.Colors.BORDER));
      var7.getVerticalScrollBar().setUnitIncrement(16);
      var2.add(var5, "North");
      var2.add(var7, "Center");
      var1.add(var2, "Center");
      return var1;
   }

   private JPanel buildArenaUnavailableStage(String var1, String var2) {
      ProjectBattleAlpha.BackgroundPanel var3 = new ProjectBattleAlpha.BackgroundPanel(new GridBagLayout(), this.activeStageBackground());
      JPanel var4 = this.panel();
      var4.setPreferredSize(new Dimension(560, 190));
      var4.setLayout(new BoxLayout(var4, 1));
      var4.setBorder(new EmptyBorder(32, 38, 32, 38));
      JLabel var5 = new JLabel(var1);
      var5.setAlignmentX(0.5F);
      var5.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var5.setFont(new Font("SansSerif", 1, 26));
      JLabel var6 = new JLabel(var2);
      var6.setAlignmentX(0.5F);
      var6.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var6.setFont(new Font("SansSerif", 0, 15));
      var4.add(Box.createVerticalGlue());
      var4.add(var5);
      var4.add(Box.createVerticalStrut(16));
      var4.add(var6);
      var4.add(Box.createVerticalGlue());
      var3.add(var4);
      return var3;
   }

   private JPanel buildEtsStage() {
      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(34, 42, 34, 42));
      JPanel var2 = this.panel();
      var2.setLayout(new BorderLayout(0, 16));
      var2.setBorder(new EmptyBorder(22, 24, 22, 24));
      JLabel var3 = new JLabel("Early Test Server Project Battle");
      var3.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var3.setFont(new Font("SansSerif", 1, 24));
      JLabel var4 = new JLabel(this.tr("Альфа-тестирование", "Alpha testing"));
      var4.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var4.setFont(new Font("SansSerif", 1, 14));
      JPanel var5 = new JPanel();
      var5.setOpaque(false);
      var5.setLayout(new BoxLayout(var5, 1));
      var5.add(var3);
      var5.add(Box.createVerticalStrut(5));
      var5.add(var4);
      JTextArea var6 = new JTextArea(this.etsText());
      var6.setOpaque(false);
      var6.setEditable(false);
      var6.setLineWrap(true);
      var6.setWrapStyleWord(true);
      var6.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var6.setFont(new Font("SansSerif", 0, 14));
      var6.setBorder(new EmptyBorder(0, 2, 0, 12));
      JScrollPane var7 = new JScrollPane(var6);
      var7.setOpaque(false);
      var7.getViewport().setOpaque(false);
      var7.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ProjectBattleAlpha.Colors.BORDER));
      var7.getVerticalScrollBar().setUnitIncrement(16);
      var2.add(var5, "North");
      var2.add(var7, "Center");
      var1.add(var2, "Center");
      return var1;
   }

   private JPanel buildFaqStage() {
      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(0, 14), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(20, 38, 26, 38));
      JPanel var2 = new JPanel(new FlowLayout(1, 8, 0));
      var2.setOpaque(false);
      ProjectBattleAlpha.FaqTabButton var3 = new ProjectBattleAlpha.FaqTabButton("FAQ");
      ProjectBattleAlpha.FaqTabButton var4 = new ProjectBattleAlpha.FaqTabButton("INFO");
      var3.setActive(true);
      var4.setActive(false);
      var2.add(var3);
      var2.add(var4);
      CardLayout var5 = new CardLayout();
      JPanel var6 = new JPanel(var5);
      var6.setOpaque(false);
      JComponent var7 = this.buildFaqContent();
      JComponent var8 = this.buildInfoContent();
      ProjectBattleAlpha.RevealPanel var9 = new ProjectBattleAlpha.RevealPanel(var7);
      ProjectBattleAlpha.RevealPanel var10 = new ProjectBattleAlpha.RevealPanel(var8);
      var6.add(var9, "faq-guide");
      var6.add(var10, "project-info");
      var3.addActionListener(var7x -> {
         if (!var3.isActive()) {
            var3.setActive(true);
            var4.setActive(false);
            var5.show(var6, "faq-guide");
            this.resetFaqScroll(var7);
            var9.reveal();
         }
      });
      var4.addActionListener(var7x -> {
         if (!var4.isActive()) {
            var3.setActive(false);
            var4.setActive(true);
            var5.show(var6, "project-info");
            this.resetFaqScroll(var8);
            var10.reveal();
         }
      });
      var1.add(var2, "North");
      var1.add(var6, "Center");
      this.resetFaqScroll(var7);
      return var1;
   }

   private JComponent buildFaqContent() {
      JPanel var1 = this.faqContentPanel(
         "FAQ — PROJECT BATTLE", this.tr("Инструкция по лаунчеру и ответы на частые вопросы", "Launcher guide and frequently asked questions")
      );
      var1.add(
         this.faqSection(
            this.tr("1. Как начать играть", "1. How to start playing"),
            this.tr(
               "1) Скачайте с сайта Project.Battle.exe и запустите его. Лаунчер сам проверит и загрузит необходимые обновления.\n\n2) Откройте меню профиля в правом верхнем углу, выберите «Настройки лаунчера» и перейдите в раздел «Профиль». Создайте аккаунт и подтвердите почту кодом из письма.\n\n3) Вернитесь в раздел «Игра» и нажмите «Загрузить». Дождитесь, пока полоса достигнет 100%.\n\n4) После завершения загрузки нажмите «Играть». Лаунчер подготовит Minecraft и подключит вас к проекту.",
               "1) Download Project.Battle.exe from the website and run it. The launcher will check for and download required updates automatically.\n\n2) Open the profile menu in the upper-right corner, choose Launcher settings, then Profile. Create an account and confirm your email with the code from the message.\n\n3) Return to Game and click Download. Wait until the progress bar reaches 100%.\n\n4) Click Play when the download finishes. The launcher will prepare Minecraft and connect you to the project."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("2. Что делает кнопка «Загрузить»", "2. What the Download button does"),
            this.tr(
               "Кнопка подготавливает актуальную игровую сборку Project Battle. Лаунчер сравнивает файлы с манифестом по размеру и SHA-256 и скачивает только отсутствующие или повреждённые. Личные миры, скриншоты, настройки и моды, добавленные вручную, сохраняются. Ход загрузки и скорость видны в разделе «Файлы и обновления».",
               "The button prepares the current Project Battle game pack. The launcher checks file sizes and SHA-256 hashes against the manifest and downloads only missing or damaged files. Personal worlds, screenshots, settings, and manually added mods remain in place. Download progress and speed appear under Files and updates."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("3. Если игра не запускается или моды повреждены", "3. If the game does not start or mods are damaged"),
            this.tr(
               "Полностью закройте Minecraft и снова нажмите «Загрузить». В разделе «Файлы и обновления» проверьте файлы и нажмите «Восстановить»: будут скачаны только повреждённые или отсутствующие. Если ошибка повторяется, запустите «Диагностику» и подготовьте отчёт для поддержки.",
               "Close Minecraft completely and click Download again. Use Check files and Repair under Files and updates to download only missing or damaged files. If the error continues, run Diagnostics and create a support report."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("4. Аккаунт, вход и код с почты", "4. Account, sign-in, and email code"),
            this.tr(
               "Путь к аккаунту: меню профиля → «Настройки лаунчера» → «Профиль». При регистрации укажите действующую почту, ник и пароль. На почту придёт шестизначный код подтверждения, который нужно ввести в лаунчере.\n\nЕсли письмо не пришло, проверьте папки «Спам» и «Промоакции», затем запросите новый код. Никому не передавайте пароль и код подтверждения — команда Project Battle никогда их не запрашивает.",
               "Account path: profile menu → Launcher settings → Profile. Enter a valid email, nickname, and password. A six-digit confirmation code will arrive by email; enter it in the launcher.\n\nIf the message does not arrive, check Spam and Promotions, then request a new code. Never share your password or confirmation code — the Project Battle team will never ask for them."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("5. Разделы лаунчера и настройки", "5. Launcher sections and settings"),
            this.tr(
               "Игра — установка сборки, выбор проекта и запуск Minecraft.\nНовости — объявления команды и информация об обновлениях.\nПравила — действующие правила проекта.\nETS — информация о сервере раннего тестирования.\nFAQ — инструкция по лаунчеру; вкладка INFO рассказывает о самом проекте.\nФорум — переход на форум Project Battle.\nРасширения — дополнительные моды, ресурспаки и шейдеры.\n\nВ настройках доступны разделы «Общие», «Игра», «Профиль», «Безопасность», «Файлы и обновления», «Диагностика» и «Интеграции». Там можно изменить язык, поведение лаунчера, память для Minecraft, данные подключения и параметры аккаунта.",
               "Game — pack installation, project selection, and Minecraft launch.\nNews — team announcements and update information.\nRules — current project rules.\nETS — Early Test Server information.\nFAQ — launcher instructions; INFO explains the project itself.\nForum — opens the Project Battle forum.\nExtensions — optional mods, resource packs, and shaders.\n\nSettings includes General, Game, Profile, Security, Files and updates, Diagnostics, and Integrations. You can change language, launcher behavior, Minecraft memory, connection details, and account options there."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("6. Безопасность и поддержка", "6. Security and support"),
            this.tr(
               "Скачивайте лаунчер и обновления только из официальных источников Project Battle. Не передавайте другим людям пароль, код подтверждения или данные аккаунта.\n\nЕсли повторная загрузка не решила проблему, приложите описание ошибки и скриншот и напишите в поддержку: ourffers@gmail.com.",
               "Download the launcher and updates only from official Project Battle sources. Do not share your password, confirmation code, or account details.\n\nIf downloading again does not solve the problem, attach an error description and screenshot and contact support at ourffers@gmail.com."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("7. Project Battle Arena", "7. Project Battle Arena"),
            this.tr(
               "Project Battle Arena выбирается в левой панели.\n\nArena пока находится в разработке. В ней можно просматривать новости, правила, FAQ, форум, расширения и использовать общие настройки и аккаунт. Кнопки «Загрузить» и «Играть» отображаются для будущего запуска Arena, но сейчас недоступны.",
               "Select Project Battle Arena in the left sidebar.\n\nArena is currently in development. News, rules, FAQ, forum, extensions, shared settings, and the account remain available. Download and Play are shown for the future Arena release but are currently unavailable."
            )
         )
      );
      var1.add(Box.createVerticalGlue());
      return this.faqScrollPane(var1);
   }

   private JComponent buildInfoContent() {
      JPanel var1 = this.faqContentPanel(
         "INFO — PROJECT BATTLE", this.tr("О проекте, его мире и игровом процессе", "About the project, its world, and gameplay")
      );
      var1.add(
         this.faqSection(
            this.tr("Project Battle", "Project Battle"),
            this.tr(
               "Project Battle — командный военно-тактический проект в Minecraft, где результат боя зависит не только от меткости, но и от взаимодействия отряда. Игроки выполняют задачи, занимают позиции, поддерживают союзников и используют особенности своей роли на поле боя.",
               "Project Battle is a team-based military tactical Minecraft project where the outcome depends not only on aim, but also on squad coordination. Players complete objectives, hold positions, support allies, and use the strengths of their battlefield roles."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("Командный бой и режим AAS", "Team combat and AAS"),
            this.tr(
               "Одно из главных направлений проекта — режим AAS (Advance and Secure), вдохновлённый командными тактическими играми. Точки захватываются последовательно, поэтому командам приходится планировать наступление, удерживать важные участки и действовать сообща, а не разбегаться по карте.",
               "One of the project's main directions is AAS (Advance and Secure), inspired by team-based tactical games. Objectives are captured in sequence, so teams must plan attacks, hold important areas, and act together instead of scattering across the map."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("Классы, техника и снаряжение", "Classes, vehicles, and equipment"),
            this.tr(
               "Каждый класс получает собственную роль, вооружение, инструменты и комплект снаряжения. Состав классов и техника фракций подбираются так, чтобы разные бойцы дополняли друг друга, а подготовка отряда и правильное распределение задач действительно влияли на исход боя.",
               "Each class has its own role, weapons, tools, and equipment. Faction classes and vehicles are designed so different players complement each other, making squad preparation and task distribution meaningful to the outcome."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("Карты, укрепления и разрушения", "Maps, fortifications, and destruction"),
            this.tr(
               "Карты Project Battle создаются как полноценные игровые пространства с доступными зданиями и проработанными интерьерами. Окружение участвует в бою: игроки могут строить позиции и укрепления, использовать подходящие инструменты и менять тактику с учётом разрушений.",
               "Project Battle maps are built as complete gameplay spaces with accessible buildings and detailed interiors. The environment is part of combat: players can build positions and fortifications, use the right tools, and adapt tactics around destruction."
            )
         )
      );
      var1.add(Box.createVerticalStrut(14));
      var1.add(
         this.faqSection(
            this.tr("Разработка проекта", "Project development"),
            this.tr(
               "Project Battle развивается через игровые тесты и обратную связь участников. Команда постепенно улучшает стабильность, баланс классов, игровые механики, карты и визуальную часть. Проект находится на стадии альфа-тестирования, поэтому отдельные элементы продолжают дорабатываться и могут меняться перед следующими играми.",
               "Project Battle grows through playtests and participant feedback. The team continues improving stability, class balance, gameplay systems, maps, and visuals. The project is in alpha testing, so individual elements are still evolving and may change before future sessions."
            )
         )
      );
      var1.add(Box.createVerticalGlue());
      return this.faqScrollPane(var1);
   }

   private JPanel faqContentPanel(String var1, String var2) {
      JPanel var3 = new JPanel();
      var3.setOpaque(false);
      var3.setLayout(new BoxLayout(var3, 1));
      var3.setBorder(new EmptyBorder(4, 4, 18, 14));
      JLabel var4 = new JLabel(var1);
      var4.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var4.setFont(new Font("SansSerif", 1, 27));
      var4.setAlignmentX(0.0F);
      JLabel var5 = new JLabel(var2);
      var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var5.setFont(new Font("SansSerif", 0, 14));
      var5.setAlignmentX(0.0F);
      JPanel var6 = new JPanel(new BorderLayout(16, 0));
      var6.setOpaque(false);
      var6.setAlignmentX(0.0F);
      JPanel var7 = new JPanel();
      var7.setOpaque(false);
      var7.setLayout(new BoxLayout(var7, 1));
      var7.add(var4);
      var7.add(Box.createVerticalStrut(5));
      var7.add(var5);
      var6.add(var7, "West");
      if (var1.startsWith("FAQ")) {
         JButton var8 = this.smallButton(this.tr("Перейти в FAQ на сайте ↗", "Open FAQ on website ↗"));
         var8.setToolTipText(this.tr("Открыть FAQ Project Battle в браузере", "Open Project Battle FAQ in browser"));
         var8.addActionListener(
            var1x -> this.openPortal("https://project-battle-portal.vokisu9.workers.dev/#faq", this.tr("Не удалось открыть FAQ", "Could not open FAQ"))
         );
         var6.add(var8, "East");
      }

      var3.add(var6);
      var3.add(Box.createVerticalStrut(20));
      return var3;
   }

   private JScrollPane faqScrollPane(JPanel var1) {
      JScrollPane var2 = new JScrollPane(var1);
      var2.setOpaque(false);
      var2.getViewport().setOpaque(false);
      var2.setBorder(null);
      var2.getVerticalScrollBar().setUnitIncrement(18);
      var2.getVerticalScrollBar().setValue(0);
      return var2;
   }

   private void resetFaqScroll(JComponent var1) {
      if (var1 instanceof JScrollPane var2) {
         var2.getVerticalScrollBar().setValue(0);
         SwingUtilities.invokeLater(() -> var2.getVerticalScrollBar().setValue(0));
      }
   }

   private JPanel buildLegacyFaqStage() {
      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(28, 38, 26, 38));
      JPanel var2 = new JPanel();
      var2.setOpaque(false);
      var2.setLayout(new BoxLayout(var2, 1));
      var2.setBorder(new EmptyBorder(4, 4, 18, 14));
      JLabel var3 = new JLabel("FAQ — PROJECT BATTLE");
      var3.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var3.setFont(new Font("SansSerif", 1, 27));
      var3.setAlignmentX(0.0F);
      JLabel var4 = new JLabel(this.tr("Полная инструкция по лаунчеру", "Complete launcher guide"));
      var4.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var4.setFont(new Font("SansSerif", 0, 14));
      var4.setAlignmentX(0.0F);
      var2.add(var3);
      var2.add(Box.createVerticalStrut(5));
      var2.add(var4);
      var2.add(Box.createVerticalStrut(20));
      var2.add(
         this.faqSection(
            this.tr("1. Как начать играть", "1. How to start playing"),
            this.tr(
               "1) Создайте аккаунт Project Battle: откройте меню профиля в правом верхнем углу, нажмите «Настройки лаунчера» и перейдите в раздел «Профиль».\n\n2) Укажите электронную почту, ник и пароль. После регистрации введите код подтверждения, который придёт на почту. Затем войдите в аккаунт.\n\n3) Вернитесь в главное меню и нажмите «Загрузить». Дождитесь завершения проверки и загрузки файлов.\n\n4) Когда сборка будет готова, нажмите «Играть». Не запускайте игру несколько раз одновременно.",
               "1) Create a Project Battle account: open the profile menu in the upper-right corner, choose Launcher settings, then Profile.\n\n2) Enter your email, nickname, and password. Confirm registration with the code sent to your email, then sign in.\n\n3) Return to the main menu and click Download. Wait until file verification and download finish.\n\n4) When the pack is ready, click Play. Do not launch the game more than once."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("2. Что делает кнопка «Загрузить»", "2. What the Download button does"),
            this.tr(
               "Кнопка проверяет управляемые файлы по манифесту Project Battle и скачивает только отсутствующие или повреждённые. Личные миры, скриншоты, настройки и моды, добавленные вручную, не удаляются. Управлять загрузкой и смотреть скорость можно в разделе «Файлы и обновления».",
               "The button checks managed files against the Project Battle manifest and downloads only missing or damaged files. Personal worlds, screenshots, settings, and manually added mods remain in place. You can manage transfers and view speed under Files and updates."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("3. Если моды повреждены или игра не запускается", "3. If mods are damaged or the game does not start"),
            this.tr(
               "Полностью закройте Minecraft и снова нажмите «Загрузить». Откройте «Файлы и обновления», нажмите «Проверить файлы», затем «Восстановить». Лаунчер скачает только нужные файлы. Если проблема остаётся, запустите «Диагностику» и подготовьте отчёт для поддержки.",
               "Close Minecraft completely and click Download again. Open Files and updates, select Check files, then Repair. The launcher downloads only files that need repair. If the issue remains, run Diagnostics and create a support report."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("4. Аккаунт, вход и код с почты", "4. Account, sign-in, and email code"),
            this.tr(
               "Путь к аккаунту: меню профиля → «Настройки лаунчера» → «Профиль». При регистрации используйте действующую почту: на неё отправляется шестизначный код подтверждения, действующий 10 минут.\n\nНик аккаунта используется в игре. После успешного входа лаунчер сохраняет сессию, если в настройках включено запоминание аккаунта. Никому не передавайте пароль, код подтверждения или данные сессии. Если письмо не пришло, проверьте папки «Спам» и «Промоакции», затем запросите новый код.",
               "Account path: profile menu → Launcher settings → Profile. Use a valid email address during registration: a six-digit confirmation code valid for 10 minutes is sent there.\n\nYour account nickname is used in game. After successful sign-in, the launcher remembers the session when account remembering is enabled. Never share your password, confirmation code, or session data. If the email does not arrive, check Spam and Promotions, then request a new code."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("5. Разделы лаунчера", "5. Launcher sections"),
            this.tr(
               "Игра — загрузка сборки, выбор проекта и запуск Minecraft.\nНовости — объявления и ход разработки Project Battle.\nПравила — действующие правила проекта.\nETS — информация о сервере раннего тестирования.\nFAQ — эта инструкция и ответы на частые вопросы.\nФорум — ссылка на форум проекта; отдельные функции могут находиться в разработке.\nРасширения — дополнительные моды, ресурс-паки и шейдеры.\n\nВ настройках доступны разделы «Общие», «Игра», «Профиль», «Безопасность», «Файлы и обновления», «Диагностика» и «Интеграции». Они отвечают за язык, поведение лаунчера, память Minecraft, подключение, аккаунт, Discord-активность и журналы.",
               "Game — pack download, project selection, and Minecraft launch.\nNews — Project Battle announcements and development updates.\nRules — current project rules.\nETS — Early Test Server information.\nFAQ — this guide and frequently asked questions.\nForum — the project forum link; some functions may still be in development.\nExtensions — optional mods, resource packs, and shaders.\n\nSettings contains General, Game, Profile, Security, Files and updates, Diagnostics, and Integrations sections for language, launcher behavior, Minecraft memory, connection, account, Discord activity, and logs."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("6. Project Battle Arena", "6. Project Battle Arena"),
            this.tr(
               "Переключение между Project Battle и Project Battle Arena находится в левой панели. При переходе проигрывается отдельная четырёхсекундная заставка.\n\nArena сейчас находится в разработке: версия отображается как Alpha 0.1, сервер — Arena, а кнопки загрузки и запуска недоступны. Новости, FAQ, форум, расширения, настройки и система аккаунтов работают так же, как в основном Project Battle. Правила Arena будут опубликованы ближе к открытию альфа-теста.",
               "Switch between Project Battle and Project Battle Arena in the left sidebar. A dedicated four-second transition plays when changing projects.\n\nArena is currently in development: its version is Alpha 0.1, the server is Arena, and download/play actions are unavailable. News, FAQ, forum, extensions, settings, and accounts work the same as in the main Project Battle mode. Arena rules will be published closer to the alpha test."
            )
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.faqSection(
            this.tr("7. Безопасность и поддержка", "7. Security and support"),
            this.tr(
               "Скачивайте лаунчер и обновления только из официальных источников Project Battle. Команда проекта никогда не просит прислать пароль или код подтверждения.\n\nЕсли проблему не удалось решить полной загрузкой и проверкой журнала, приложите описание ошибки и скриншот и напишите в поддержку: ourffers@gmail.com.",
               "Download the launcher and updates only from official Project Battle sources. The project team will never ask for your password or confirmation code.\n\nIf a full download and log review do not solve the issue, attach an error description and screenshot and contact support at ourffers@gmail.com."
            )
         )
      );
      var2.add(Box.createVerticalGlue());
      JScrollPane var5 = new JScrollPane(var2);
      var5.setOpaque(false);
      var5.getViewport().setOpaque(false);
      var5.setBorder(null);
      var5.getVerticalScrollBar().setUnitIncrement(18);
      var1.add(var5, "Center");
      return var1;
   }

   private JPanel faqSection(String var1, String var2) {
      JPanel var3 = this.panel();
      var3.setLayout(new BorderLayout(0, 10));
      var3.setBorder(new EmptyBorder(18, 21, 18, 21));
      var3.setAlignmentX(0.0F);
      JLabel var4 = new JLabel(var1);
      var4.setForeground(ProjectBattleAlpha.Colors.PLAY_LIGHT);
      var4.setFont(new Font("SansSerif", 1, 18));
      JTextArea var5 = new JTextArea(var2);
      var5.setOpaque(false);
      var5.setEditable(false);
      var5.setFocusable(false);
      var5.setLineWrap(true);
      var5.setWrapStyleWord(true);
      var5.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var5.setFont(new Font("SansSerif", 0, 13));
      var5.setBorder(null);
      var5.setRows(Math.max(4, var2.length() / 105 + 2));
      var3.add(var4, "North");
      var3.add(var5, "Center");
      var3.setMaximumSize(new Dimension(Integer.MAX_VALUE, var5.getRows() * 22 + 62));
      return var3;
   }

   private JPanel buildExtensionsStage() {
      ProjectBattleAlpha.BackgroundPanel var1 = new ProjectBattleAlpha.BackgroundPanel(new BorderLayout(0, 18), this.activeStageBackground());
      var1.setBorder(new EmptyBorder(34, 42, 34, 42));
      CardLayout var2 = new CardLayout();
      JPanel var3 = new JPanel(var2);
      var3.setOpaque(false);
      var3.add(
         this.emptyExtensionPage(this.tr("Моды", "Mods"), this.tr("Дополнительные моды появятся здесь позже.", "Optional mods will appear here later.")),
         "mods"
      );
      var3.add(
         this.emptyExtensionPage(
            this.tr("Ресурс-паки", "Resource packs"),
            this.tr("Косметические паки можно будет ставить отсюда.", "Cosmetic packs will be installable from here.")
         ),
         "resourcepacks"
      );
      var3.add(this.shadersPage(), "shaders");
      JPanel var4 = new JPanel(new FlowLayout(1, 10, 0));
      var4.setOpaque(false);
      var4.add(this.extensionTab(this.tr("Моды", "Mods"), var2x -> var2.show(var3, "mods")));
      var4.add(this.extensionTab(this.tr("Ресурс-паки", "Resource packs"), var2x -> var2.show(var3, "resourcepacks")));
      var4.add(this.extensionTab(this.tr("Шейдеры", "Shaders"), var2x -> var2.show(var3, "shaders")));
      var1.add(var4, "North");
      var1.add(var3, "Center");
      var2.show(var3, "shaders");
      return var1;
   }

   private JButton extensionTab(String var1, ActionListener var2) {
      JButton var3 = this.smallButton(var1);
      var3.setPreferredSize(new Dimension(150, 34));
      var3.addActionListener(var2);
      return var3;
   }

   private JPanel emptyExtensionPage(String var1, String var2) {
      JPanel var3 = new JPanel(new GridBagLayout());
      var3.setOpaque(false);
      JPanel var4 = this.panel();
      var4.setPreferredSize(new Dimension(520, 170));
      var4.setBorder(new EmptyBorder(22, 24, 22, 24));
      var4.setLayout(new BorderLayout(0, 12));
      JLabel var5 = new JLabel(var1);
      var5.setForeground(ProjectBattleAlpha.Colors.PLAY);
      var5.setFont(new Font("SansSerif", 1, 20));
      JLabel var6 = new JLabel(var2);
      var6.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var6.setFont(new Font("SansSerif", 0, 13));
      var4.add(var5, "North");
      var4.add(var6, "Center");
      var3.add(var4);
      return var3;
   }

   private JPanel shadersPage() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.setOpaque(false);
      JPanel var2 = new JPanel();
      var2.setOpaque(false);
      var2.setLayout(new BoxLayout(var2, 1));
      var2.add(
         this.extensionCard(
            "BodyCam Shader",
            this.tr(
               "Body Camera Shader - данный набор шейдеров привносит в Minecraft уникальный визуальный эффект, имитирующий ракурс камеры тела.",
               "Body Camera Shader brings a unique visual effect to Minecraft, imitating the perspective of a body camera."
            ),
            this.bodycamShaderImage,
            false,
            () -> this.installBundledExtension(
               "/assets/extensions/body-camera-shader-v1.5.1.zip", "shaderpacks/Body Camera Shader - V1.5.1.zip", "BodyCam Shader"
            ),
            () -> this.removeBundledExtension("shaderpacks/Body Camera Shader - V1.5.1.zip", "BodyCam Shader")
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.extensionCard(
            "ComplementaryReimagined_r5.4",
            "Сохраняя элементы Minecraft с исключительным качеством, детализацией и производительностью.\n\nComplementary Shaders - это шейдер-пак для Minecraft Java Edition с исключительным вниманием к деталям, высокими стандартами качества и первоклассной оптимизацией. Он предлагает множество качественных эффектов, два стандартных визуальных стиля, кастомные эффекты почти для каждого блока в игре, профили от Potato до Ultra, и он разработан так, чтобы никогда не мешать во время игры. Игра началась как редактирование BSL Shaders в 2018 году и получила множество крупных обновлений, включая полную переработку для соответствия стандартам сообщества. Complementary Shaders ориентирован на формирование отзывов пользователей больше, чем на всё остальное, и до сих пор регулярно обновляется.",
            this.complementaryShaderImage,
            true,
            () -> this.installBundledExtension(
               "/assets/extensions/complementary-reimagined-r5.4.zip", "shaderpacks/ComplementaryReimagined_r5.4.zip", "ComplementaryReimagined_r5.4"
            ),
            () -> this.removeBundledExtension("shaderpacks/ComplementaryReimagined_r5.4.zip", "ComplementaryReimagined_r5.4")
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.extensionCard(
            "Iteration T3.2.0",
            "Этот шейдерпак кардинально преображает игру, создавая реалистичную атмосферу с потрясающими визуальными эффектами. Мир станет более живым и захватывающим, с улучшенными тенями, освещением и отражениями. Для полного погружения потребуется мощный компьютер, но результат того стоит.",
            this.iterationShaderImage,
            false,
            () -> this.installBundledExtension("/assets/extensions/iterationT3.2.0.zip", "shaderpacks/iterationT3.2.0.zip", "Iteration T3.2.0"),
            () -> this.removeBundledExtension("shaderpacks/iterationT3.2.0.zip", "Iteration T3.2.0")
         )
      );
      var2.add(Box.createVerticalStrut(14));
      var2.add(
         this.extensionCard(
            "Insanity Shader 1.650",
            "Тематический хоррор-шейдер, основанный на BSL и нацеленный на придание устрашающей атмосферы игре. Мир вокруг погрузится во тьму и серость, вся погода будет переработана, а удобное меню позволит настроить шейдер по своему вкусу.",
            this.insanityShaderImage,
            false,
            () -> this.installBundledExtension(
               "/assets/extensions/Insanity-Shader-Universal-v1.650.zip", "shaderpacks/Insanity-Shader-Universal-v1.650.zip", "Insanity Shader 1.650"
            ),
            () -> this.removeBundledExtension("shaderpacks/Insanity-Shader-Universal-v1.650.zip", "Insanity Shader 1.650")
         )
      );
      var2.setBorder(new EmptyBorder(0, 0, 16, 0));
      JScrollPane var3 = new JScrollPane(var2);
      var3.setBorder(null);
      var3.setOpaque(false);
      var3.getViewport().setOpaque(false);
      var3.getVerticalScrollBar().setUnitIncrement(24);
      var3.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
      var1.add(var3, "Center");
      return var1;
   }

   private JPanel extensionCard(String var1, String var2, BufferedImage var3, boolean var4, Runnable var5, Runnable var6) {
      JPanel var7 = this.panel();
      var7.setLayout(new BorderLayout(16, 0));
      var7.setBorder(new EmptyBorder(16, 16, 16, 16));
      var7.setMaximumSize(new Dimension(Integer.MAX_VALUE, var4 ? 180 : 150));
      ProjectBattleAlpha.ImagePanel var8 = new ProjectBattleAlpha.ImagePanel(var3, false, 0.92F, true);
      var8.setPreferredSize(new Dimension(86, 86));
      var8.setMaximumSize(new Dimension(86, 86));
      var8.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
      var7.add(var8, "West");
      JPanel var9 = new JPanel(new BorderLayout(0, 8));
      var9.setOpaque(false);
      JLabel var10 = new JLabel(var1);
      var10.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var10.setFont(new Font("SansSerif", 1, 20));
      JTextArea var11 = new JTextArea(this.localizedExtensionDescription(var1, var2));
      var11.setOpaque(false);
      var11.setEditable(false);
      var11.setLineWrap(true);
      var11.setWrapStyleWord(true);
      var11.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var11.setFont(new Font("SansSerif", 0, 13));
      JScrollPane var12 = new JScrollPane(var11);
      var12.setOpaque(false);
      var12.getViewport().setOpaque(false);
      var12.setBorder(null);
      var12.setPreferredSize(new Dimension(540, var4 ? 54 : 70));
      var12.getVerticalScrollBar().setUnitIncrement(12);
      var9.add(var10, "North");
      var9.add(var12, "Center");
      if (var4) {
         JButton var13 = this.smallButton(this.tr("Развернуть", "Expand"));
         var13.setPreferredSize(new Dimension(130, 28));
         var13.addActionListener(var4x -> {
            boolean var5x = var12.getPreferredSize().height < 190;
            var12.setPreferredSize(new Dimension(540, var5x ? 210 : 54));
            var13.setText(var5x ? this.tr("Свернуть", "Collapse") : this.tr("Развернуть", "Expand"));
            var7.setMaximumSize(new Dimension(Integer.MAX_VALUE, var5x ? 330 : 180));
            var7.revalidate();
         });
         var9.add(var13, "South");
      }

      var7.add(var9, "Center");
      JPanel var16 = new JPanel();
      var16.setOpaque(false);
      var16.setLayout(new BoxLayout(var16, 1));
      var16.setBorder(new EmptyBorder(14, 4, 14, 4));
      JButton var14 = this.extensionActionButton("\ud83d\udd27  " + this.tr("Скачать", "Download"), ProjectBattleAlpha.Colors.PLAY);
      var14.addActionListener(var1x -> var5.run());
      JButton var15 = this.extensionActionButton("\ud83d\uddd1  " + this.tr("Удалить", "Remove"), ProjectBattleAlpha.Colors.ERROR);
      var15.addActionListener(var1x -> var6.run());
      var16.add(var14);
      var16.add(Box.createVerticalStrut(10));
      var16.add(var15);
      var16.add(Box.createVerticalGlue());
      var7.add(var16, "East");
      return var7;
   }

   private JButton extensionActionButton(String var1, Color var2) {
      JButton var3 = new JButton(var1);
      var3.setOpaque(false);
      var3.setContentAreaFilled(false);
      var3.setFocusPainted(false);
      var3.setBorder(new EmptyBorder(8, 10, 8, 10));
      var3.setForeground(var2);
      var3.setFont(new Font("SansSerif", 1, 14));
      var3.setCursor(launcherCursor(12));
      var3.setAlignmentX(1.0F);
      return var3;
   }

   private String localizedExtensionDescription(String var1, String var2) {
      if (!this.english()) {
         return var2;
      } else if (var1.startsWith("BodyCam")) {
         return "Body Camera Shader brings a unique visual effect to Minecraft, imitating the perspective of a body camera.";
      } else if (var1.startsWith("Complementary")) {
         return "Preserves the look of Minecraft with exceptional quality, detail, and performance.\n\nComplementary Shaders is a shader pack for Minecraft Java Edition with strong attention to detail, high quality standards, and excellent optimization. It offers many high-quality effects, two default visual styles, custom effects for almost every block, profiles from Potato to Ultra, and is designed to stay out of the way during gameplay. It began as a BSL Shaders edit in 2018 and has received many major updates, including a full rework to match community standards. Complementary Shaders is shaped heavily by user feedback and is still updated regularly.";
      } else if (var1.startsWith("Iteration")) {
         return "This shader pack dramatically transforms the game, creating a realistic atmosphere with stunning visual effects. Your world becomes more vivid and immersive with improved shadows, lighting, and reflections. A powerful computer is recommended for the full experience, but the result is worth it.";
      } else {
         return var1.startsWith("Insanity")
            ? "A horror-themed shader based on BSL, designed to give the game a frightening atmosphere. The world becomes dark and desaturated, the weather is reworked, and a convenient menu lets you tune the shaders to your preference."
            : var2;
      }
   }

   private JPanel newsArticlePanel(LocalDate var1, String var2, String var3) {
      JPanel var4 = new JPanel() {
         @Override
         protected void paintComponent(Graphics var1) {
            Graphics2D var2x = (Graphics2D)var1.create();
            var2x.setColor(new Color(12, 17, 16, 205));
            var2x.fillRect(0, 0, this.getWidth(), this.getHeight());
            var2x.dispose();
            super.paintComponent(var1);
         }
      };
      var4.setOpaque(false);
      var4.setLayout(new BorderLayout(18, 0));
      var4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(16, 16, 16, 16)));
      var4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
      JPanel var5 = new JPanel(new BorderLayout(0, 8));
      var5.setOpaque(false);
      String var6 = this.english()
         ? var1.getMonthValue() + "/" + var1.getDayOfMonth() + "/" + var1.getYear()
         : var1.getDayOfMonth() + " " + this.russianMonth(var1.getMonthValue()) + " " + var1.getYear();
      JLabel var7 = new JLabel(var6 + "   " + this.daysAgoText(var1));
      var7.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var7.setFont(new Font("SansSerif", 1, 12));
      JLabel var8 = new JLabel(var2);
      var8.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var8.setFont(new Font("SansSerif", 1, 18));
      JTextArea var9 = new JTextArea(var3);
      var9.setOpaque(false);
      var9.setEditable(false);
      var9.setLineWrap(true);
      var9.setWrapStyleWord(true);
      var9.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var9.setFont(new Font("SansSerif", 0, 13));
      JScrollPane var10 = new JScrollPane(var9);
      var10.setOpaque(false);
      var10.getViewport().setOpaque(false);
      var10.setBorder(null);
      var10.setPreferredSize(new Dimension(540, 86));
      var10.getVerticalScrollBar().setUnitIncrement(12);
      JButton var11 = this.smallButton(this.tr("Развернуть", "Expand"));
      var11.setPreferredSize(new Dimension(130, 30));
      var11.addActionListener(var4x -> {
         boolean var5x = var10.getPreferredSize().height < 220;
         var10.setPreferredSize(new Dimension(540, var5x ? 230 : 86));
         var11.setText(var5x ? this.tr("Свернуть", "Collapse") : this.tr("Развернуть", "Expand"));
         var4.setMaximumSize(new Dimension(Integer.MAX_VALUE, var5x ? 470 : 320));
         var4.revalidate();
      });
      JPanel var12 = new JPanel();
      var12.setOpaque(false);
      var12.setLayout(new BoxLayout(var12, 1));
      var12.add(var7);
      var12.add(Box.createVerticalStrut(5));
      var12.add(var8);
      var5.add(var12, "North");
      var5.add(var10, "Center");
      var5.add(var11, "South");
      var4.add(var5, "Center");
      return var4;
   }

   private JPanel buildConfigStrip() {
      JPanel var1 = new JPanel(new BorderLayout(18, 0));
      var1.setOpaque(false);
      var1.setBorder(new EmptyBorder(0, 0, 0, 0));
      JPanel var2 = this.panel();
      var2.setLayout(new BorderLayout(20, 0));
      var2.setBorder(new EmptyBorder(14, 18, 14, 18));
      var2.setPreferredSize(new Dimension(668, 104));
      JPanel var3 = new JPanel();
      var3.setOpaque(false);
      var3.setPreferredSize(new Dimension(320, 90));
      var3.setLayout(new BoxLayout(var3, 1));
      this.versionLabel = this.infoLine(this.tr("Версия лаунчера:", "Launcher version:"), this.tr("0.1.78.13", "0.1.78.13"), ProjectBattleAlpha.Colors.MUTED);
      var3.add(this.versionLabel);
      var3.add(Box.createVerticalStrut(5));
      this.editionLabel = this.infoLine(this.tr("Издание:", "Edition:"), "Standard", ProjectBattleAlpha.Colors.MUTED);
      var3.add(this.editionLabel);
      var3.add(Box.createVerticalStrut(5));
      this.serverStatusLabel = this.infoLine(this.tr("Сервер:", "Server:"), "ETS ▾", ProjectBattleAlpha.Colors.PLAY);
      this.serverStatusLabel.setCursor(launcherCursor(12));
      this.serverStatusLabel.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            ProjectBattleAlpha.this.showServerMenu(ProjectBattleAlpha.this.serverStatusLabel);
         }
      });
      var3.add(this.serverStatusLabel);
      JPanel var4 = new JPanel(new GridLayout(2, 2, 8, 8));
      var4.setOpaque(false);
      this.ramSelector = new ProjectBattleAlpha.RamSelector();
      this.forgeField = this.input();
      this.serverField = this.input();
      this.manifestField = this.input();
      this.cleanBox = this.check("Чистить");
      this.joinBox = this.check("Авто-вход");
      JPanel var5 = new JPanel(new GridLayout(2, 1));
      var5.setOpaque(false);
      var5.add(this.cleanBox);
      var5.add(this.joinBox);
      var4.add(this.labeled("RAM", this.ramSelector));
      var4.add(this.labeled("Forge", this.forgeField));
      var4.add(var5);
      JPanel var6 = new JPanel(new GridLayout(1, 2, 8, 8));
      var6.setOpaque(false);
      this.refreshButton = this.actionButton(this.tr("Обновить", "Update"));
      this.refreshButton.setPreferredSize(new Dimension(92, 48));
      this.refreshButton.addActionListener(this::onUpdate);
      this.saveButton = this.actionButton(this.tr("Сохранить", "Save"));
      this.saveButton.setPreferredSize(new Dimension(92, 48));
      this.saveButton.addActionListener(this::onSave);
      var6.add(this.refreshButton);
      var6.add(this.saveButton);
      var4.setVisible(false);
      var6.setVisible(false);
      var2.add(var3, "West");
      var2.add(Box.createHorizontalGlue(), "Center");
      this.playButton = new ProjectBattleAlpha.PlayButton(this.tr("Играть", "Play"), this.playButtonImage);
      this.playButton.setPreferredSize(new Dimension(292, 66));
      this.playButton.addActionListener(this::onPlay);
      var2.add(this.playButton, "East");
      var1.add(var2, "Center");
      this.logArea = new JTextArea();
      this.logArea.setEditable(false);
      this.logArea.setLineWrap(true);
      this.logArea.setWrapStyleWord(true);
      this.logArea.setBackground(ProjectBattleAlpha.Colors.PANEL);
      this.logArea.setForeground(ProjectBattleAlpha.Colors.MUTED);
      this.logArea.setFont(new Font("Monospaced", 0, 11));
      this.logArea.setOpaque(true);
      this.logArea.setSelectionColor(ProjectBattleAlpha.Colors.PANEL_LIGHT);
      this.logArea.setSelectedTextColor(ProjectBattleAlpha.Colors.TEXT);
      return var1;
   }

   private JPanel buildBottomBar() {
      JPanel var1 = new JPanel(new BorderLayout(18, 0));
      var1.setOpaque(false);
      var1.setPreferredSize(new Dimension(1, 0));
      return var1;
   }

   private JPanel sidebarItem(String var1, String var2, boolean var3) {
      return new ProjectBattleAlpha.SidebarProjectItem(var1, var2, "Project Battle: Arena".equals(var2), var3);
   }

   private void installSidebarAction(JPanel var1, final Runnable var2) {
      var1.setCursor(launcherCursor(12));
      var1.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            if (SwingUtilities.isLeftMouseButton(var1)) {
               var2.run();
            }
         }
      });
   }

   private void installSidebarTooltip(final JPanel var1, final String var2) {
      MouseAdapter var3 = new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1x) {
            ProjectBattleAlpha.this.scheduleSidebarTooltip(var1, var2);
         }

         @Override
         public void mouseExited(MouseEvent var1x) {
            ProjectBattleAlpha.this.hideNavTooltip();
         }
      };
      var1.addMouseListener(var3);

      for (Component var7 : var1.getComponents()) {
         var7.addMouseListener(var3);
      }
   }

   private void scheduleSidebarTooltip(Component var1, String var2) {
      this.hideNavTooltip();
      this.navTooltipTimer = new Timer(320, var3 -> this.showComponentTooltip(var1, var2));
      this.navTooltipTimer.setRepeats(false);
      this.navTooltipTimer.start();
   }

   private JLabel navLabel(String var1, boolean var2) {
      final ProjectBattleAlpha.NavItemLabel var3 = new ProjectBattleAlpha.NavItemLabel(var1);
      var3.setActive(var2);
      var3.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1) {
            ProjectBattleAlpha.this.scheduleNavTooltip(var3);
         }

         @Override
         public void mouseExited(MouseEvent var1) {
            ProjectBattleAlpha.this.hideNavTooltip();
         }
      });
      return var3;
   }

   private void scheduleNavTooltip(JLabel var1) {
      this.hideNavTooltip();
      this.navTooltipTimer = new Timer(320, var2 -> this.showNavTooltip(var1, this.navTooltipText(var1)));
      this.navTooltipTimer.setRepeats(false);
      this.navTooltipTimer.start();
   }

   private String navTooltipText(JLabel var1) {
      if (var1 == this.gameNav) {
         return this.tr("Главное меню лаунчера", "Launcher main menu");
      } else if (var1 == this.newsNav) {
         return this.tr("Открыть новости Project Battle на сайте", "Open Project Battle news on the website");
      } else if (var1 == this.rulesNav) {
         return this.tr("Правила игры на Project Battle", "Project Battle game rules");
      } else if (var1 == this.etsNav) {
         return this.tr("Early Test Server - сервер раннего тестирования", "Early Test Server");
      } else if (var1 == this.faqNav) {
         return this.tr("Инструкция по лаунчеру и ответы на частые вопросы", "Launcher guide and frequently asked questions");
      } else if (var1 == this.forumNav) {
         return this.tr("Открыть форум Project Battle на сайте", "Open the Project Battle forum on the website");
      } else {
         return var1 == this.extensionsNav ? this.tr("Дополнительные расширения для сборки", "Additional extensions for the modpack") : "";
      }
   }

   private void showNavTooltip(JLabel var1, String var2) {
      this.showComponentTooltip(var1, var2);
   }

   private void showComponentTooltip(Component var1, String var2) {
      if (var2 != null && !var2.isBlank() && var1.isShowing()) {
         this.hideNavTooltip();
         JWindow var3 = new JWindow(this);
         var3.setFocusableWindowState(false);
         var3.setAlwaysOnTop(false);
         JPanel var4 = new JPanel(new BorderLayout());
         var4.setBackground(new Color(38, 44, 43));
         var4.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(105, 116, 112)), new EmptyBorder(8, 11, 8, 11)));
         JLabel var5 = new JLabel("<html><div style='width:220px'>" + var2 + "</div></html>");
         var5.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var5.setFont(new Font("SansSerif", 0, 12));
         var4.add(var5, "Center");
         var3.setContentPane(var4);
         var3.pack();
         Point var6 = var1.getLocationOnScreen();
         int var7 = var6.x;
         int var8 = var6.y + var1.getHeight() + 7;
         Rectangle var9 = var1.getGraphicsConfiguration() == null
            ? new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
            : var1.getGraphicsConfiguration().getBounds();
         if (var7 + var3.getWidth() > var9.x + var9.width) {
            var7 = var9.x + var9.width - var3.getWidth() - 8;
         }

         if (var8 + var3.getHeight() > var9.y + var9.height) {
            var8 = var6.y - var3.getHeight() - 7;
         }

         var3.setLocation(Math.max(var9.x + 8, var7), Math.max(var9.y + 8, var8));
         this.navTooltip = var3;
         var3.setVisible(true);
      }
   }

   private void hideNavTooltip() {
      if (this.navTooltipTimer != null) {
         this.navTooltipTimer.stop();
         this.navTooltipTimer = null;
      }

      if (this.navTooltip != null) {
         this.navTooltip.setVisible(false);
         this.navTooltip.dispose();
         this.navTooltip = null;
      }
   }

   private JPanel newsCard(String var1, String var2, String var3, boolean var4) {
      ProjectBattleAlpha.NewsCardPanel var5 = new ProjectBattleAlpha.NewsCardPanel(var4);
      var5.setLayout(new BorderLayout());
      var5.setBorder(new EmptyBorder(18, 18, 16, 18));
      JLabel var6 = new JLabel(var1);
      var6.setForeground(ProjectBattleAlpha.Colors.PLAY);
      var6.setFont(new Font("SansSerif", 1, var4 ? 18 : 13));
      JLabel var7 = new JLabel("<html>" + var2 + "</html>");
      var7.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var7.setFont(new Font("SansSerif", 1, var4 ? 34 : 18));
      JTextArea var8 = new JTextArea(var3);
      var8.setOpaque(false);
      var8.setEditable(false);
      var8.setLineWrap(true);
      var8.setWrapStyleWord(true);
      var8.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var8.setFont(new Font("SansSerif", 0, var4 ? 15 : 12));
      var5.add(var6, "North");
      var5.add(var7, "Center");
      var5.add(var8, "South");
      return var5;
   }

   private String daysAgoText(LocalDate var1) {
      long var2 = Math.max(0L, ChronoUnit.DAYS.between(var1, LocalDate.now()));
      if (this.english()) {
         return var2 == 1L ? "1 day ago" : var2 + " days ago";
      } else if (var2 % 10L == 1L && var2 % 100L != 11L) {
         return var2 + " день назад";
      } else {
         return var2 % 10L < 2L || var2 % 10L > 4L || var2 % 100L >= 10L && var2 % 100L < 20L ? var2 + " дней назад" : var2 + " дня назад";
      }
   }

   private String russianMonth(int var1) {
      return switch (var1) {
         case 1 -> "января";
         case 2 -> "февраля";
         case 3 -> "марта";
         case 4 -> "апреля";
         case 5 -> "мая";
         case 6 -> "июня";
         case 7 -> "июля";
         case 8 -> "августа";
         case 9 -> "сентября";
         case 10 -> "октября";
         case 11 -> "ноября";
         default -> "декабря";
      };
   }

   private String etsText() {
      return this.english()
         ? "ETS - Early Test Server, the main Project Battle testing environment at the current stage.\n\nThe project is currently in alpha testing, so all sessions take place on ETS. Players can be the first to try Project Battle mechanics, team battles, AAS, classes, weapons, vehicles, and other systems.\n\nDuring testing, some features may be unstable, change, or be temporarily disabled. Errors, technical issues, balance changes, mod updates, and partial data resets are a normal part of early development.\n\nThe main goal of ETS is to test the project in real conditions, find problems, collect feedback, and prepare Project Battle for the next stages.\n\nYou can report bugs and suggestions through the official Discord server or Project Battle Support in the launcher: open your profile and select Support.\n\nETS is currently the only available Project Battle server. Join the Project Battle alpha test!"
         : "ETS - Early Test Server, или сервер раннего тестирования, - основная игровая среда Project Battle на текущем этапе разработки.\n\nСейчас проект находится на стадии альфа-тестирования, поэтому все игровые сессии проходят именно на ETS. Здесь игроки смогут первыми знакомиться с механиками Project Battle, участвовать в командных боях, проверять режим AAS, классы, вооружение, технику и другие системы проекта.\n\nВо время тестирования некоторые функции могут работать нестабильно, изменяться или временно отключаться. Возможны ошибки, технические неполадки, изменения баланса, обновления состава модификаций и сброс отдельных данных. Это нормальная часть раннего этапа разработки.\n\nГлавная задача ETS - проверить проект в реальных игровых условиях, выявить проблемы, собрать отзывы участников и подготовить Project Battle к дальнейшим этапам развития. Каждое обновление будет содержать исправления, технические улучшения и доработки на основе результатов тестов.\n\nОб обо всех найденных ошибках и предложениях можно сообщать через официальный Discord-сервер или поддержку Project Battle. Поддержка находится в лаунчере: откройте профиль и выберите пункт «Поддержка».\n\nНа текущем этапе ETS является единственным доступным сервером Project Battle. Присоединяйтесь к альфа-тестированию Project Battle!";
   }

   private String newsText() {
      return this.english()
         ? "Good day!\n\nAfter a long break, development of Project Battle is officially resuming!\n\nWe are now working on an update so large that it can be treated as a new version of the project. We have tried to fix past mistakes, clean up weak spots, and rework many gameplay systems.\n\nNew map\nThe new map will be Airport, a full-scale location based on a large airport.\n\nDuring the first tests the map will remain civilian, without signs of combat. Later we will run a poll and decide whether to keep it that way or rework it into a more military format.\n\nThe map has a high level of detail: every building can be entered, and most objects have detailed interiors.\n\nVehicles and classes\nThe vehicle pool for all factions has been fully updated and adapted to the current faction roster.\n\nThe class system has also been reworked: there are more classes now, and the equipment has been revised and improved.\n\nNew gameplay - AAS\nThe main change will be the AAS (Advance and Secure) system, inspired by Squad.\n\nIt adds full point capture with sequential objectives and other mechanics that make battles more organized and team-based.\n\nThe map also supports full object destruction, which will seriously affect tactics and the flow of combat.\n\n---\n\nThis is only part of the changes. Follow the news - there is much more ahead!\n\nProject Battle AAS\nPB | Team"
         : "Доброго времени суток!\n\nПосле длительного перерыва разработка Project Battle официально возобновляется!\n\nСейчас мы работаем над настолько масштабным обновлением, что его можно считать новой версией проекта. Мы постарались исправить прошлые ошибки, недочеты и переработать многие игровые механики.\n\nНовая карта\nНовой картой станет Аэропорт - полноценная локация большого аэропорта.\n\nНа первых тестах карта останется в гражданском стиле, без следов боевых действий. Позже мы проведем опрос и решим, оставить ее такой или переработать в более военный формат.\n\nКарта получила высокую детализацию: во все здания можно попасть, а большинство объектов имеют проработанные интерьеры.\n\nТехника и классы\nАвтопарк всех фракций был полностью обновлен и адаптирован под текущий состав сторон.\n\nТакже была переработана система классов: их стало больше, а экипировка была пересмотрена и улучшена.\n\nНовый геймплей - AAS\nГлавным изменением станет система AAS (Advance and Secure), вдохновленная механикой игры Squad.\n\nОна добавит полноценный захват точек с последовательными задачами и другими механиками, делающими бои более организованными и командными.\n\nТакже на карте теперь доступны полные разрушения объектов, что серьезно повлияет на тактику и ход сражений.\n\n---\n\nЭто лишь часть изменений. Следите за новостями - впереди еще много интересного!\n\nProject Battle AAS\nPB | Team";
   }

   private String rulesText() {
      return this.english()
         ? "1. Cheats and modifications\nAny third-party programs, mods, or resource packs that provide an advantage are strictly forbidden. Examples include X-Ray, ESP/WH, hitbox enlargement, autoclickers, and similar tools.\nOnly cosmetic mods that do not affect gameplay are allowed.\n\n2. Movement\nUsing jumps to speed up movement is forbidden. Players must move by running or walking as intended by the mechanics.\n\n3. Subordination\nPlayers must follow the orders of their squad commander.\n\n4. SpawnKill\nKilling players at spawn points and staying near the enemy base for a long time is forbidden.\n\n5. Power Gaming\nAny interaction while knocked out is forbidden. You may not shoot, throw grenades, place vehicles, or use items.\n\n6. Play area\nPlayers must stay inside the play area.\n\n7. Bug abuse\nUsing bugs, map flaws, game issues, or mod issues is forbidden.\n\n8. Meta Gaming\nSharing gameplay information through Discord, Telegram, or other communication tools outside the game is forbidden.\n\n9. Provocation\nAny intentional action aimed at provoking other players into breaking server rules is treated as a rule violation.\n"
         : "1. Читы и модификации\nЛюбые сторонние программы, моды или ресурспаки, дающие преимущество, строго запрещены. Примеры: X-Ray, ESP/WH, увеличение хитбоксов, автокликеры и подобное.\nРазрешены только косметические моды, не влияющие на игровой процесс.\n\n2. Передвижение\nЗапрещено использовать прыжки для ускоренного перемещения. Игроки обязаны передвигаться бегом или шагом.\n\n3. Субординация\nИгроки обязаны выполнять указания своего командира отряда.\n\n4. SpawnKill\nЗапрещено убивать игроков на точке возрождения и длительно находиться возле базы противника.\n\n5. Power Gaming\nЗапрещено любое взаимодействие в стадии нокаута. Нельзя стрелять, метать гранаты, ставить транспорт или использовать предметы.\n\n6. Игровая зона\nИгроки обязаны находиться только в пределах игровой зоны.\n\n7. Багоюз\nЗапрещено использовать баги, недоработки карты, игры или модов.\n\n8. Meta Gaming\nЗапрещено передавать игровую информацию через Discord, Telegram и другие средства связи вне игры.\n\n9. Провокация на нарушение\nЛюбое намеренное действие, направленное на провокацию других игроков, приравнивается к нарушению.\n";
   }

   private JPanel panel() {
      ProjectBattleAlpha.SoftPanel var1 = new ProjectBattleAlpha.SoftPanel();
      var1.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
      return var1;
   }

   private JTextField input() {
      JTextField var1 = new JTextField();
      var1.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
      var1.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var1.setCaretColor(ProjectBattleAlpha.Colors.TEXT);
      var1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(6, 8, 6, 8)));
      var1.setFont(new Font("SansSerif", 0, 12));
      return var1;
   }

   private JPanel labeled(String var1, JComponent var2) {
      JPanel var3 = new JPanel(new BorderLayout(0, 3));
      var3.setOpaque(false);
      JLabel var4 = new JLabel(var1);
      var4.setForeground(ProjectBattleAlpha.Colors.MUTED);
      var4.setFont(new Font("SansSerif", 0, 10));
      var3.add(var4, "North");
      var3.add(var2, "Center");
      return var3;
   }

   private JLabel infoLine(String var1, String var2, Color var3) {
      JLabel var4 = new JLabel(infoHtml(var1, var2, var3));
      var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var4.setFont(new Font("SansSerif", 1, 13));
      var4.setAlignmentX(0.0F);
      return var4;
   }

   private static String infoHtml(String var0, String var1, Color var2) {
      return "<html><span style='color:"
         + colorHex(ProjectBattleAlpha.Colors.TEXT)
         + "'>"
         + var0
         + "</span> <span style='color:"
         + colorHex(var2)
         + "'>"
         + var1
         + "</span></html>";
   }

   private static String colorHex(Color var0) {
      return String.format("#%02x%02x%02x", var0.getRed(), var0.getGreen(), var0.getBlue());
   }

   private JCheckBox check(String var1) {
      JCheckBox var2 = new JCheckBox(var1);
      var2.setOpaque(false);
      var2.setForeground(ProjectBattleAlpha.Colors.TEXT);
      var2.setFont(new Font("SansSerif", 0, 11));
      return var2;
   }

   private JButton smallButton(String var1) {
      ProjectBattleAlpha.DarkButton var2 = new ProjectBattleAlpha.DarkButton(var1);
      var2.setFont(new Font("SansSerif", 1, 12));
      return var2;
   }

   private JButton actionButton(String var1) {
      ProjectBattleAlpha.SoftActionButton var2 = new ProjectBattleAlpha.SoftActionButton(var1);
      var2.setFont(new Font("SansSerif", 1, 12));
      return var2;
   }

   private void loadConfig() {
      this.nameField.setText(this.config.playerName);
      this.updateAccountName();
      this.ramSelector.setValue(normalizeRamMb(this.config.ramMb));
      this.forgeField.setText(this.config.forgeVersionId);
      this.serverField.setText(this.config.server);
      this.manifestField.setText(this.config.manifestLocation);
      this.cleanBox.setSelected(this.config.cleanManagedDirs);
      this.joinBox.setSelected(this.config.autoJoinServer);
      this.applyLanguage();
      this.applyProjectModeControls();
   }

   private void saveConfigFromUi() {
      if (this.config.authToken != null && !this.config.authToken.isBlank() && this.config.authNickname != null && !this.config.authNickname.isBlank()) {
         this.config.playerName = sanitizeName(this.config.authNickname);
         this.nameField.setText(this.config.playerName);
      } else {
         this.config.playerName = sanitizeName(this.nameField.getText());
      }

      this.config.ramMb = normalizeRamMb(this.ramSelector.getValue());
      this.config.forgeVersionId = normalizeForgeVersionId(this.forgeField.getText());
      this.forgeField.setText(this.config.forgeVersionId);
      this.config.server = normalizeServer(this.serverField.getText().trim());
      this.serverField.setText(this.config.server);
      this.config.manifestLocation = this.manifestField.getText().trim();
      this.config.cleanManagedDirs = this.cleanBox.isSelected();
      this.config.autoJoinServer = this.joinBox.isSelected();
   }

   private void onSave(ActionEvent var1) {
      this.saveConfigFromUi();

      try {
         this.config.save();
         this.checkServerStatus();
         this.log("Settings saved.");
      } catch (IOException var3) {
         this.error(this.tr("Не удалось сохранить", "Could not save"), var3);
      }
   }

   private void startServerStatusMonitor() {
      this.updateServerStatus("ETS ▾", ProjectBattleAlpha.Colors.PLAY);
   }

   private void checkServerStatus() {
      this.updateServerStatus("ETS ▾", ProjectBattleAlpha.Colors.PLAY);
   }

   private void updateServerStatus(String var1, Color var2) {
      if (this.arenaMode) {
         SwingUtilities.invokeLater(() -> {
            if (this.serverStatusLabel != null) {
               this.serverStatusLabel.setText(infoHtml(this.tr("Сервер:", "Server:"), "Arena", ProjectBattleAlpha.Colors.MUTED));
            }
         });
      } else {
         SwingUtilities.invokeLater(() -> {
            if (this.serverStatusLabel != null) {
               this.serverStatusLabel.setText(infoHtml(this.tr("Сервер:", "Server:"), var1, var2));
            }
         });
      }
   }

   private void showServerMenu(Component var1) {
      if (!this.arenaMode) {
         JPopupMenu var2 = new JPopupMenu();
         var2.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
         var2.setBackground(ProjectBattleAlpha.Colors.PANEL);
         var2.add(this.serverMenuItem("ETS", ProjectBattleAlpha.Colors.PLAY, () -> this.updateServerStatus("ETS ▾", ProjectBattleAlpha.Colors.PLAY)));
         var2.add(this.serverMenuItem(this.tr("Live (недоступен)", "Live (unavailable)"), ProjectBattleAlpha.Colors.MUTED, () -> {}));
         var2.show(var1, 0, var1.getHeight() + 3);
      }
   }

   private JComponent serverMenuItem(String var1, Color var2, final Runnable var3) {
      final JPanel var4 = new JPanel(new BorderLayout());
      var4.setOpaque(true);
      var4.setBackground(ProjectBattleAlpha.Colors.PANEL);
      var4.setBorder(new EmptyBorder(9, 12, 9, 50));
      var4.setCursor(launcherCursor(12));
      JLabel var5 = new JLabel(var1);
      var5.setForeground(var2);
      var5.setFont(new Font("SansSerif", 1, 13));
      var4.add(var5, "Center");
      var4.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent var1) {
            var4.setBackground(new Color(31, 38, 37));
         }

         @Override
         public void mouseExited(MouseEvent var1) {
            var4.setBackground(ProjectBattleAlpha.Colors.PANEL);
         }

         @Override
         public void mouseClicked(MouseEvent var1) {
            var3.run();
         }
      });
      return var4;
   }

   private void onNickSave(ActionEvent var1) {
      this.saveNickname(this.nameField.getText());
   }

   private void saveNickname(String var1) {
      this.config.playerName = sanitizeName(var1);
      this.nameField.setText(this.config.playerName);
      this.updateAccountName();

      try {
         this.config.save();
         this.log("Nickname saved.");
      } catch (IOException var3) {
         this.error(this.tr("Не удалось сохранить ник", "Could not save nickname"), var3);
      }
   }

   private void updateAccountName() {
      boolean var1 = this.config.authToken != null && !this.config.authToken.isBlank();
      if (this.accountNameLabel != null) {
         this.accountNameLabel.setText(var1 ? this.config.playerName : "Unlogged");
         this.accountNameLabel.setVisible(true);
      }

      if (this.accountAvatarLabel != null) {
         this.accountAvatarLabel.setVisible(true);
      }

      if (this.accountOnlinePanel != null) {
         this.accountOnlinePanel.setVisible(true);
      }

      if (this.accountOnlineDot != null) {
         this.accountOnlineDot.setForeground(var1 ? ProjectBattleAlpha.Colors.ONLINE : ProjectBattleAlpha.Colors.ERROR);
      }

      if (this.accountStatusLabel != null) {
         this.accountStatusLabel.setText(var1 ? "Live" : this.tr("Не в сети", "Offline"));
      }
   }

   private void onUpdate(ActionEvent var1) {
      if (this.maintenanceRunning.get()) {
         this.log("Finish file maintenance before updating.");
         return;
      }
      if (!this.arenaMode && !this.updateRunning) {
         this.updateRunning = true;
         this.discordPresence.setState("Проверяет файлы");
         this.saveConfigFromUi();
         this.setBusy(true, this.tr("Проверка...", "Checking..."));
         this.worker
            .submit(
               () -> {
                  try {
                     this.config.manifestLocation = normalizeManifestLocation(this.config.manifestLocation);
                     this.manifestField.setText(this.config.manifestLocation);
                     this.log("Checking update source: " + this.config.manifestLocation);
                     ProjectBattleAlpha.Manifest var1x = ProjectBattleAlpha.Manifest.load(this.config);
                     this.sync(var1x, false);
                     this.discordPresence.setState("Загружает обновление");
                     this.removeBundledAuthMods();
                     this.ensureBundledSettingsMod();
                     this.ensureShaderpackDirectory();
                     this.ensureVanillaMinecraftVersion();
                     this.versionLabel
                        .setText(
                           infoHtml(
                              this.tr("Версия лаунчера:", "Launcher version:"),
                              APP_VERSION,
                              ProjectBattleAlpha.Colors.MUTED
                           )
                        );
                     this.setUpdateAvailable(false, var1x.version);
                     this.config.save();
                     this.log("Pack is ready.");
                  } catch (FileNotFoundException var6) {
                     this.log("Manifest is not configured. Skipping update.");
                  } catch (Exception var7) {
                     this.setProgress(0, this.tr("Обновление не завершено", "Update not completed"));
                     this.log("Update failed after network/file checks: " + var7.getClass().getSimpleName() + ": " + var7.getMessage());
                     this.error(this.tr("Ошибка обновления", "Update error"), var7);
                  } finally {
                     this.updateRunning = false;
                     this.setBusy(false, this.tr("Готово", "Ready"));
                  }
               }
            );
      }
   }

   private void onPlay(ActionEvent var1) {
      if (this.maintenanceRunning.get()) {
         this.log("Finish file maintenance before launching.");
         return;
      }
      if (!this.arenaMode) {
         if (this.updateAvailable) {
            this.onUpdate(var1);
         } else if (this.minecraftRunning) {
            this.log("Minecraft is already running.");
         } else if (this.config.authToken != null && !this.config.authToken.isBlank()) {
            this.saveConfigFromUi();
            if (this.config.authNickname != null && !this.config.authNickname.isBlank()) {
               this.config.playerName = sanitizeName(this.config.authNickname);
               this.nameField.setText(this.config.playerName);
               this.config.ramMb = normalizeRamMb(this.ramSelector.getValue());

               try {
                  this.config.save();
                  this.log("RAM selected for launch: " + this.config.ramMb + " MB");
               } catch (IOException var3) {
                  this.log("Could not save RAM before launch: " + var3.getMessage());
               }

               this.updateAccountName();
               this.setBusy(true, this.tr("Запуск...", "Launching..."));
               this.discordPresence.setState("Подготавливает игру");
               this.worker.submit(() -> {
                  try {
                     try {
                        ProjectBattleAlpha.Manifest var1x = ProjectBattleAlpha.Manifest.load(this.config);
                        this.sync(var1x);
                        this.setUpdateAvailable(false, var1x.version);
                     } catch (FileNotFoundException var7) {
                        this.log("No manifest configured, launching current instance.");
                     }

                     this.removeBundledAuthMods();
                     this.ensureBundledSettingsMod();
                     this.ensureShaderpackDirectory();
                     this.ensureForgeInstalled();
                     this.ensureVanillaMinecraftVersion();
                     this.ensurePreferredMinecraftLanguageOnce();
                     this.ensureRequiredResourcePackEnabled();
                     if (!this.checkAutoJoinServer()) return;
                     Map var10 = this.authPost("/auth/minecraft/ticket", "{}", this.config.authToken);
                     this.config.gameTicket = str(var10.get("ticket"));
                     if (this.config.gameTicket.isBlank()) {
                        throw new IOException("Auth server did not return a Minecraft game ticket.");
                     }

                     Process var2 = new ProjectBattleAlpha.Runner(this.config, this::log).launch();
                     SwingUtilities.invokeLater(() -> this.forgeField.setText(this.config.forgeVersionId));
                     this.log("Minecraft started. PID " + var2.pid());
                     this.setMinecraftRunning(true);
                     this.discordPresence.setActivity("Играет в Project Battle", "Project Battle");
                     if (!"exit".equals(this.config.launchAction)) {
                        this.stream(var2);
                        return;
                     }

                     SwingUtilities.invokeLater(() -> this.closeLauncher());
                  } catch (Exception var8) {
                     this.showLaunchFailure(var8);
                     return;
                  } finally {
                     this.setBusy(false, this.tr("Готово", "Ready"));
                  }
               });
            } else {
               JOptionPane.showMessageDialog(
                  this,
                  this.tr(
                     "Не удалось получить ник аккаунта. Выйдите и войдите в аккаунт заново.",
                     "Could not read the account nickname. Sign out and sign in again."
                  ),
                  this.tr("Нужен ник аккаунта", "Account nickname required"),
                  2
               );
            }
         } else {
            JOptionPane.showMessageDialog(
               this,
               this.tr(
                  "Сначала войдите в аккаунт.\n\nДля этого нажмите стрелку возле профиля -> Аккаунт -> Войти в аккаунт.",
                  "Please sign in first.\n\nClick the arrow near your profile -> Account -> Sign in."
               ),
               this.tr("Нужен аккаунт", "Account required"),
               2
            );
            this.openLoginDialog();
         }
      }
   }

   private void stream(Process var1) {
      this.worker.submit(() -> {
         try (BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream(), StandardCharsets.UTF_8))) {
            while (true) {
               String var3;
               if ((var3 = var2.readLine()) == null) {
                  int exitCode = var1.waitFor();
                  this.log("Minecraft exited with code " + exitCode + ".");
                  if (exitCode != 0) this.showLaunchFailure(new IOException("Minecraft exited with code " + exitCode));
                  break;
               }

               this.log("[MC] " + var3);
            }
         } catch (Exception var12) {
            this.log("Minecraft log stopped: " + var12.getMessage());
         } finally {
            this.setMinecraftRunning(false);
            this.discordPresence.setActivity("Находится в лаунчере", "Главное меню");
         }
      });
   }

   private void setMinecraftRunning(boolean var1) {
      this.minecraftRunning = var1;
      SwingUtilities.invokeLater(
         () -> {
            if (this.playButton != null) {
               this.playButton
                  .setText(
                     var1
                        ? this.tr("Запущено", "Running")
                        : (this.updateAvailable ? this.tr("Скачать \ud83d\udce5", "Download \ud83d\udce5") : this.tr("Играть", "Play"))
                  );
               this.playButton.setEnabled(!var1);
            }

            if (this.arenaMode) {
               this.applyProjectModeControls();
            }
         }
      );
   }

   private void refreshManifestStatusAsync() {
      this.worker
         .submit(
            () -> {
               try {
                  ProjectBattleAlpha.Manifest var1 = ProjectBattleAlpha.Manifest.load(this.config);
                  boolean var2 = this.manifestNeedsUpdate(var1);
                  this.setUpdateAvailable(var2, var1.version);
                  this.log(
                     "Manifest status: "
                        + (var2 ? "update available" : "up to date")
                        + (var1.version != null && !var1.version.isBlank() ? " (" + var1.version + ")" : "")
                  );
               } catch (Exception var3) {
                  this.log("Manifest status check skipped: " + var3.getClass().getSimpleName() + ": " + var3.getMessage());
               }
            }
         );
   }

   private boolean manifestNeedsUpdate(ProjectBattleAlpha.Manifest var1) throws IOException {
      if (var1.files.isEmpty()) {
         return false;
      }

      for (ProjectBattleAlpha.ManifestFile var3 : var1.files) {
         if (var3.required) {
            Path var4 = safeResolve(this.config.instanceDir, var3.path);
            if (!Files.isRegularFile(var4)) {
               return true;
            }

            if (var3.size >= 0L && Files.size(var4) != var3.size) {
               return true;
            }

            if (var3.sha256 != null && !var3.sha256.isBlank() && !var3.sha256.equalsIgnoreCase(sha256(var4))) {
               return true;
            }
         }
      }

      return false;
   }

   private void setUpdateAvailable(boolean var1, String var2) {
      this.updateAvailable = var1;
      this.detectedManifestVersion = var2 == null ? "" : var2;
      SwingUtilities.invokeLater(
         () -> {
            if (this.arenaMode) {
               this.applyProjectModeControls();
            } else {
               if (this.updateNoticeLabel != null) {
                  this.updateNoticeLabel
                     .setText(var1 ? this.tr("Версия игры устарела. Доступно обновление.", "Game version is outdated. An update is available.") : "");
                  this.updateNoticeLabel.setVisible(var1);
               }

               if (this.playButton != null && !this.minecraftRunning) {
                  this.playButton.setText(var1 ? this.tr("Скачать \ud83d\udce5", "Download \ud83d\udce5") : this.tr("Играть", "Play"));
               }

               if (this.versionLabel != null && var1 && !this.detectedManifestVersion.isBlank()) {
                  this.versionLabel.setToolTipText(this.tr("Досту#fна версия: ", "Available version: ") + this.detectedManifestVersion);
               }

               this.revalidate();
               this.repaint();
            }
         }
      );
   }

   private void installBundledExtension(String var1, String var2, String var3) {
      if (!this.extensionInstalls.add(var2)) {
         this.log(var3 + " installation is already in progress.");
      } else {
         this.setBusy(true, this.tr("Установка...", "Installing..."));
         this.worker
            .submit(
               () -> {
                  try (InputStream var4 = ProjectBattleAlpha.class.getResourceAsStream(var1)) {
                     if (var4 == null) {
                        throw new IOException("Extension resource not found: " + var1);
                     }

                     Path var5 = safeResolve(this.config.instanceDir, var2);
                     Files.createDirectories(var5.getParent());
                     if (!Files.isRegularFile(var5)) {
                        Files.copy(var4, var5, StandardCopyOption.REPLACE_EXISTING);
                        hidePath(var5.getParent());
                        this.log(var3 + " installed to " + var5.toAbsolutePath() + ".");
                        SwingUtilities.invokeLater(
                           () -> JOptionPane.showMessageDialog(
                              this, var3 + this.tr(" установлен в сборку.", " installed into the pack."), "Project Battle Alpha", 1
                           )
                        );
                        return;
                     }

                     this.log(var3 + " is already installed at " + var5.toAbsolutePath() + ".");
                     SwingUtilities.invokeLater(
                        () -> JOptionPane.showMessageDialog(
                           this,
                           var3
                              + this.tr(
                                 " уже установлен. Если Minecraft был открыт, перезапустите игру.", " is already installed. Restart Minecraft if it was open."
                              ),
                           "Project Battle Alpha",
                           1
                        )
                     );
                  } catch (Exception var14) {
                     this.error(this.tr("Ошибка расширения", "Extension error"), var14);
                     return;
                  } finally {
                     this.extensionInstalls.remove(var2);
                     this.setBusy(false, null);
                  }
               }
            );
      }
   }

   private void removeBundledAuthMods() throws IOException {
      Path var1 = this.config.instanceDir.resolve("mods");
      Files.createDirectories(var1);
      int var2 = 0;

      try (DirectoryStream<Path> var3 = Files.newDirectoryStream(var1, "projectbattle-auth-*.jar")) {
         for (Path var5 : var3) {
            if (Files.deleteIfExists(var5)) {
               var2++;
            }
         }
      }

      if (var2 > 0) {
         this.log("Removed obsolete Project Battle authentication module.");
      }
   }

   private void ensureBundledSettingsMod() throws IOException {
      Path var1 = this.config.instanceDir.resolve("mods");
      Files.createDirectories(var1);
      Path var2 = var1.resolve("projectbattle-settings-test-0.3.5-test.jar");

      try (DirectoryStream<Path> var3 = Files.newDirectoryStream(var1, "projectbattle-settings-test-*.jar")) {
         for (Path var5 : var3) {
            if (!var5.getFileName().toString().equals("projectbattle-settings-test-0.3.5-test.jar")) {
               Files.deleteIfExists(var5);
            }
         }
      }

      try (InputStream var10 = ProjectBattleAlpha.class.getResourceAsStream("/assets/extensions/projectbattle-settings-test-0.3.5-test.jar")) {
         if (var10 == null) {
            throw new FileNotFoundException("Bundled Project Battle interface module is missing.");
         }

         Files.copy(var10, var2, StandardCopyOption.REPLACE_EXISTING);
      }

      this.log("Project Battle interface module is ready: " + var2.toAbsolutePath());
   }

   private void ensureShaderpackDirectory() throws IOException {
      Path var1 = this.config.instanceDir.resolve("shaderpacks");
      Files.createDirectories(var1);
      this.log("Shaderpacks directory: " + var1.toAbsolutePath());
   }

   private void removeBundledExtension(String var1, String var2) {
      this.setBusy(true, this.tr("Удаление...", "Removing..."));
      this.worker
         .submit(
            () -> {
               try {
                  Path var3 = safeResolve(this.config.instanceDir, var1);
                  if (Files.deleteIfExists(var3)) {
                     this.log(var2 + " removed.");
                     SwingUtilities.invokeLater(
                        () -> JOptionPane.showMessageDialog(this, var2 + this.tr(" удалён из сборки.", " removed from the pack."), "Project Battle Alpha", 1)
                     );
                  } else {
                     SwingUtilities.invokeLater(
                        () -> JOptionPane.showMessageDialog(this, var2 + this.tr(" ещё не установлен.", " is not installed yet."), "Project Battle Alpha", 1)
                     );
                  }
               } catch (Exception var7) {
                  this.error(this.tr("Ошибка удаления", "Remove error"), var7);
               } finally {
                  this.setBusy(false, null);
               }
            }
         );
   }

   private void setBusy(boolean var1, String var2) {
      this.busy = var1;
      SwingUtilities.invokeLater(() -> {
         this.playButton.setEnabled(!var1 && !this.minecraftRunning);
         this.updateButton.setEnabled(!var1);
         this.refreshButton.setEnabled(!var1);
         this.saveButton.setEnabled(!var1);
         if (this.nickSaveButton != null) {
            this.nickSaveButton.setEnabled(!var1);
         }

         this.progress.setIndeterminate(var1);
         if (var1) {
            this.progress.setString(var2);
         } else {
            this.progress.setValue(this.lastProgressValue);
            this.progress.setString(this.lastProgressText);
         }

         if (this.arenaMode) {
            this.applyProjectModeControls();
         }
      });
   }

   private void setProgress(int var1, String var2) {
      int var3 = Math.max(0, Math.min(100, var1));
      this.lastProgressValue = var3;
      this.lastProgressText = var3 + "%";
      SwingUtilities.invokeLater(() -> {
         this.progress.setIndeterminate(false);
         this.progress.setValue(var3);
         this.progress.setString(this.lastProgressText);
      });
   }

   private void log(String var1) {
      SwingUtilities.invokeLater(() -> {
         this.logArea.append("[" + LocalDateTime.now().format(TIME) + "] " + var1 + System.lineSeparator());
         this.logArea.setCaretPosition(this.logArea.getDocument().getLength());
         this.logArea.revalidate();
         this.logArea.repaint();
      });
   }

   private void error(String var1, Exception var2) {
      this.log(var1 + ": " + var2.getMessage());
      SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, var1 + "\n\n" + var2.getMessage(), "Project Battle Alpha", 0));
   }

   private void showLaunchFailure(Exception failure) {
      String reason = failure.getMessage() == null ? failure.getClass().getSimpleName() : failure.getMessage();
      String lower = reason.toLowerCase(Locale.ROOT);
      String fix;
      if (lower.contains("forge") || lower.contains("installer"))
         fix = this.tr("Проверьте Java 17 и профиль Forge в «Диагностике». Затем повторите запуск.",
               "Check Java 17 and the Forge profile in Diagnostics, then retry.");
      else if (lower.contains("library") || lower.contains("lwjgl") || lower.contains("native"))
         fix = this.tr("В «Диагностике» проверьте библиотеки; повторный запуск загрузит недостающие.",
               "Check libraries in Diagnostics; retrying will download missing files.");
      else if (lower.contains("manifest") || lower.contains("hash") || lower.contains("download"))
         fix = this.tr("Откройте «Файлы и обновления» и нажмите «Восстановить».",
               "Open Files and updates and click Repair.");
      else if (lower.contains("auth") || lower.contains("ticket") || lower.contains("token"))
         fix = this.tr("Проверьте API авторизации и повторно войдите в аккаунт.",
               "Check the auth API and sign in again.");
      else fix = this.tr("Запустите «Диагностику» и подготовьте отчёт для поддержки.",
               "Run Diagnostics and create a support report.");
      this.log("Launch failed: " + reason);
      SwingUtilities.invokeLater(() -> {
         Object[] options = {this.tr("Повторить", "Retry"), this.tr("Диагностика", "Diagnostics"), this.tr("Закрыть", "Close")};
         int choice = JOptionPane.showOptionDialog(this,
               this.tr("Не удалось запустить Minecraft.\n\nПричина: ", "Could not start Minecraft.\n\nReason: ")
                     + redactSupportText(reason) + "\n\n" + fix,
               "Project Battle Alpha", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
         if (choice == 0) this.onPlay(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "retry"));
         if (choice == 1) this.openLauncherSettings("diagnostics");
      });
   }

   private boolean checkAutoJoinServer() throws Exception {
      if (!this.config.autoJoinServer || this.config.server == null || this.config.server.isBlank()) return true;
      ProjectBattleAlpha.Address address = ProjectBattleAlpha.Address.parse(normalizeServer(this.config.server));
      while (true) {
         try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(address.host(), address.port()), 4000);
            this.log("Autoconnect preflight reachable: " + address.host() + ":" + address.port());
            return true;
         } catch (IOException failure) {
            this.log("Autoconnect preflight unavailable: " + failure.getMessage());
            final int[] selected = new int[1];
            SwingUtilities.invokeAndWait(() -> {
               Object[] choices = {this.tr("Повторить", "Retry"), this.tr("Диагностика", "Diagnostics"),
                     this.tr("Запустить всё равно", "Launch anyway")};
               selected[0] = JOptionPane.showOptionDialog(this,
                     this.tr("Сервер сейчас недоступен: ", "Server is currently unavailable: ") + this.config.server
                           + "\n" + redactSupportText(failure.getMessage()) + "\n\n"
                           + this.tr("Повторите проверку или откройте диагностику. Minecraft можно запустить и без доступного сервера.",
                                 "Retry the check or open Diagnostics. Minecraft can still launch while the server is unavailable."),
                     "Project Battle Alpha", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                     null, choices, choices[0]);
               if (selected[0] == 1) this.openLauncherSettings("diagnostics");
            });
            if (selected[0] == 0) continue;
            return selected[0] == 2;
         }
      }
   }

   private void closeLauncher() {
      try {
         this.saveConfigFromUi();
         this.config.save();
      } catch (IOException var2) {
         this.log("Could not save launcher settings before exit: " + var2.getMessage());
      }

      this.discordPresence.shutdown();
      this.worker.shutdownNow();
      this.profileWorker.shutdownNow();
      this.dispose();
      System.exit(0);
   }

   private void sync(ProjectBattleAlpha.Manifest var1) throws IOException {
      this.sync(var1, false);
   }

   private String diagnosticText() {
      StringBuilder result = new StringBuilder("Project Battle Alpha 0.1.78.13\n");
      try {
         Path javaPath = ProjectBattleAlpha.Runner.findJava(this.config.minecraftDir);
         Process process = new ProcessBuilder(javaPath.toString(), "-version").redirectErrorStream(true).start();
         if (!process.waitFor(6, java.util.concurrent.TimeUnit.SECONDS)) {
            process.destroyForcibly();
            result.append("Java: timeout\n");
         } else {
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).lines().findFirst().orElse("unknown");
            result.append("Java: ").append(output).append("\n");
            if (!output.contains("\"17.") && !output.contains("\"17\""))
               result.append(this.tr("  Рекомендация: для Forge 1.20.1 используйте Java 17.\n", "  Recommendation: use Java 17 for Forge 1.20.1.\n"));
         }
      } catch (Exception ex) { result.append("Java: ").append(ex.getMessage()).append("\n"); }

      try {
         String forge = ProjectBattleAlpha.Runner.resolveForgeVersionId(this.config.minecraftDir, this.config.forgeVersionId);
         boolean usable = ProjectBattleAlpha.Runner.isUsableForgeProfile(this.config.minecraftDir, forge);
         result.append("Forge: ").append(forge).append(usable ? " OK" : " INVALID").append("\n");
         ProjectBattleAlpha.Version version = ProjectBattleAlpha.Version.load(this.config.minecraftDir, forge);
         int missing = 0;
         int natives = 0;
         for (ProjectBattleAlpha.Library library : version.libraries) {
            if (!library.allowedWindows()) continue;
            if (library.artifact != null && library.required
                  && !Files.isRegularFile(this.config.minecraftDir.resolve("libraries").resolve(library.artifact))) missing++;
            if (library.nativeArtifact != null && library.required) {
               natives++;
               if (!Files.isRegularFile(this.config.minecraftDir.resolve("libraries").resolve(library.nativeArtifact))) missing++;
            }
         }
         result.append(this.tr("Библиотеки: отсутствуют ", "Libraries: missing ")).append(missing)
               .append(this.tr(", нативных записей ", ", native entries ")).append(natives).append("\n");
         if (missing > 0) result.append(this.tr("  Решение: нажмите «Играть» для загрузки библиотек либо восстановите сборку.\n",
               "  Fix: use Play to download libraries or repair the pack.\n"));
      } catch (Exception ex) { result.append("Forge / libraries: ").append(ex.getMessage()).append("\n"); }

      long repairBytes = 0;
      try {
         ProjectBattleAlpha.Manifest manifest = ProjectBattleAlpha.Manifest.load(this.config);
         List<PackMaintenance.FileSpec> files = new ArrayList<>();
         for (ProjectBattleAlpha.ManifestFile file : manifest.files)
            files.add(new PackMaintenance.FileSpec(file.path, file.url, file.sha256, file.size, file.required));
         List<PackMaintenance.FileIssue> issues = PackMaintenance.inspect(this.config.instanceDir, files);
         for (PackMaintenance.FileIssue issue : issues) repairBytes += Math.max(0, issue.file().size());
         result.append(this.tr("Сборка: файлов для восстановления ", "Pack: files needing repair ")).append(issues.size()).append("/").append(files.size()).append("\n");
         if (!issues.isEmpty()) result.append(this.tr("  Решение: «Файлы и обновления» → «Восстановить».\n", "  Fix: Files and updates → Repair.\n"));
      } catch (Exception ex) { result.append(this.tr("Сборка: ошибка манифеста: ", "Pack: manifest error: ")).append(ex.getMessage()).append("\n"); }

      try {
         URL url = new URL(this.config.authApiUrl.replaceAll("/+$", "") + "/health");
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setConnectTimeout(4000);
         connection.setReadTimeout(4000);
         int status = connection.getResponseCode();
         result.append(this.tr("API авторизации: HTTP ", "Auth API: HTTP ")).append(status).append("\n");
         connection.disconnect();
      } catch (Exception ex) { result.append(this.tr("API авторизации: недоступен: ", "Auth API: unavailable: ")).append(ex.getMessage()).append("\n"); }

      try {
         String server = normalizeServer(this.config.server);
         int delimiter = server.lastIndexOf(':');
         String host = delimiter < 0 ? server : server.substring(0, delimiter);
         int port = delimiter < 0 ? 25565 : Integer.parseInt(server.substring(delimiter + 1));
         long started = System.nanoTime();
         try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress(host, port), 4000); }
         result.append(this.tr("Сервер: TCP доступен, ", "Server: TCP reachable, "))
               .append((System.nanoTime() - started) / 1000000).append(" ms\n");
      } catch (Exception ex) { result.append(this.tr("Сервер: соединение недоступно: ", "Server: connection unavailable: ")).append(ex.getMessage()).append("\n"); }

      try {
         Path location = Files.exists(this.config.instanceDir) ? this.config.instanceDir : this.config.instanceDir.getParent();
         long available = Files.getFileStore(location).getUsableSpace();
         result.append(this.tr("Свободно на диске: ", "Disk free: ")).append(String.format(Locale.ROOT, "%.1f GB", available / 1073741824.0)).append("\n");
         if (available < repairBytes + 1073741824L)
            result.append(this.tr("  Недостаточно места для восстановления с запасом 1 GB.\n", "  Not enough space for repair plus 1 GB reserve.\n"));
      } catch (Exception ex) { result.append(this.tr("Диск: ", "Disk: ")).append(ex.getMessage()).append("\n"); }
      return redactSupportText(result.toString());
   }

   private String serverStatusText() {
      try {
         URL url = new URL(this.config.authApiUrl.replaceAll("/+$", "") + "/server/status");
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(10000);
         int code = connection.getResponseCode();
         if (code != 200) {
            connection.disconnect();
            return this.tr("Статус exaroton недоступен (HTTP ", "Exaroton status unavailable (HTTP ") + code + ").\n"
                  + this.tr("Проверьте настройку backend или повторите позже.", "Check backend configuration or retry later.");
         }
         String body;
         try (InputStream input = connection.getInputStream()) { body = new String(input.readAllBytes(), StandardCharsets.UTF_8); }
         connection.disconnect();
         if (!(ProjectBattleAlpha.Json.parse(body) instanceof Map<?, ?> data)) throw new IOException("Invalid status response");
         String status = str(data.get("status"));
         StringBuilder result = new StringBuilder(this.tr("Статус: ", "Status: ")).append(status).append("\n");
         if (data.get("players") instanceof Map<?, ?> players) {
            result.append(this.tr("Игроков: ", "Players: ")).append(str(players.get("count"))).append("/").append(str(players.get("max"))).append("\n");
         }
         result.append(this.tr("Адрес: ", "Address: ")).append(this.config.server).append("\n");
         if ("ONLINE".equals(status)) {
            String server = normalizeServer(this.config.server);
            int delimiter = server.lastIndexOf(':');
            String host = delimiter < 0 ? server : server.substring(0, delimiter);
            int port = delimiter < 0 ? 25565 : Integer.parseInt(server.substring(delimiter + 1));
            try {
               long started = System.nanoTime();
               try (Socket socket = new Socket()) { socket.connect(new InetSocketAddress(host, port), 4000); }
               result.append(this.tr("Задержка TCP: ", "TCP latency: ")).append((System.nanoTime() - started) / 1000000).append(" ms");
            } catch (IOException ex) {
               result.append(this.tr("Подключение к игровому адресу не удалось: ", "Game address connection failed: ")).append(ex.getMessage());
            }
         }
         return result.toString();
      } catch (Exception ex) {
         return this.tr("Не удалось получить статус сервера: ", "Could not get server status: ") + ex.getMessage();
      }
   }

   private String redactSupportText(String text) {
      String value = text;
      for (String secret : Arrays.asList(this.config.authToken, this.config.gameTicket, this.config.authEmail, this.config.authNickname,
            System.getProperty("user.home", ""))) {
         if (secret != null && !secret.isBlank()) value = value.replace(secret, "<redacted>");
      }
      value = value.replaceAll("(?i)bearer\\s+[^\\s\\\"']+", "Bearer <redacted>");
      value = value.replaceAll("(?i)(password|token|ticket|secret|authorization)\\s*[:=]\\s*[^\\s,;\\\"']+", "$1=<redacted>");
      value = value.replaceAll("(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}", "<email>");
      value = value.replaceAll("(?i)(https?://[^\\s?]+)\\?[^\\s]+", "$1?<redacted>");
      value = value.replaceAll("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b", "<ip>");
      value = value.replaceAll("(?i)eyJ[a-z0-9_-]+\\.eyJ[a-z0-9_-]+\\.[a-z0-9_-]+", "<jwt>");
      value = value.replaceAll("(?i)[a-z]:\\\\users\\\\[^\\\\\\s]+", "<user-path>");
      return value;
   }

   private Path writeSupportReport(String diagnostics) throws IOException {
      Files.createDirectories(this.config.configDir);
      Path output = this.config.configDir.resolve("support-report-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt");
      StringBuilder report = new StringBuilder("Project Battle support report\n\n").append(diagnostics).append("\nLauncher log (last 200 lines):\n");
      String[] captured = new String[1];
      if (SwingUtilities.isEventDispatchThread()) captured[0] = this.logArea == null ? "" : this.logArea.getText();
      else {
         try { SwingUtilities.invokeAndWait(() -> captured[0] = this.logArea == null ? "" : this.logArea.getText()); }
         catch (Exception ex) { throw new IOException("Could not capture launcher log", ex); }
      }
      String[] launcherLines = captured[0].split("\\R");
      for (int i = Math.max(0, launcherLines.length - 200); i < launcherLines.length; i++) report.append(launcherLines[i]).append('\n');
      Path minecraftLog = this.config.instanceDir.resolve("logs").resolve("latest.log");
      if (Files.isRegularFile(minecraftLog)) {
         report.append("\nMinecraft latest.log (last 200 lines):\n");
         java.util.ArrayDeque<String> tail = new java.util.ArrayDeque<>(200);
         try (Stream<String> lines = Files.lines(minecraftLog, StandardCharsets.UTF_8)) {
            lines.forEach(line -> { if (tail.size() == 200) tail.removeFirst(); tail.addLast(line); });
         }
         for (String line : tail) report.append(line).append('\n');
      }
      Files.writeString(output, redactSupportText(report.toString()), StandardCharsets.UTF_8);
      return output;
   }

   private void sync(ProjectBattleAlpha.Manifest var1, boolean var2) throws IOException {
      if (this.minecraftRunning || this.isProjectBattleMinecraftProcessRunning())
         throw new IOException(this.tr("Сначала закройте Minecraft.", "Close Minecraft before updating files."));
      Files.createDirectories(this.config.instanceDir);
      this.packDownloads.begin(var1.files.stream().map(file -> file.path).toList());
      try {
      int var4 = Math.max(1, var1.files.size());
      int var5 = 0;

      for (ProjectBattleAlpha.ManifestFile var7 : var1.files) {
         var5++;
         this.setProgress((var5 - 1) * 100 / var4, var7.path);
         Path var8 = safeResolve(this.config.instanceDir, var7.path);
         boolean var9 = Files.exists(var8);
         if (var9 && var7.size >= 0L) {
            var9 = Files.size(var8) == var7.size;
         }

         if (var9 && var7.sha256 != null && !var7.sha256.isBlank()) {
            var9 = var7.sha256.equalsIgnoreCase(sha256(var8));
         }

         if (var9) {
            this.log("File ready " + var5 + "/" + var4 + ": " + var7.path);
            this.packDownloads.mark(var7.path, "Ready");
         } else if (var7.url != null && !var7.url.isBlank()) {
            Files.createDirectories(var8.getParent());
            this.log("Downloading file " + var5 + "/" + var4 + ": " + var7.path);
            this.packDownloads.download(var7.path, new URL(var7.url), var8, var7.size, var7.sha256);
         } else if (var7.required) {
            throw new IOException("Missing required pack file.");
         }
      }

      hidePath(this.config.instanceDir);
      hidePath(this.config.instanceDir.resolve("mods"));
      this.setProgress(100, "Сборка готова");
      this.packDownloads.finish();
      } catch (IOException | RuntimeException ex) {
         this.packDownloads.fail(ex.getMessage());
         throw ex;
      }
   }

   private void ensurePackCanBeUpdated(ProjectBattleAlpha.Manifest var1) throws IOException {
      if (this.isProjectBattleMinecraftProcessRunning()) {
         throw lockedPackFileException(this.config.instanceDir, new IOException("Project Battle Minecraft process is still running."));
      }

      for (ProjectBattleAlpha.ManifestFile var3 : var1.files) {
         String var4 = var3.path == null ? "" : var3.path.replace('\\', '/');
         if (!var4.startsWith("mods/")) {
            Path var5 = safeResolve(this.config.instanceDir, var3.path);
            if (Files.isRegularFile(var5)) {
               probeManagedFileReplacement(var5);
            }
         }
      }
   }

   private static void probeManagedFileReplacement(Path var0) throws IOException {
      Path var1 = var0.resolveSibling(var0.getFileName() + ".projectbattle-update-check-" + UUID.randomUUID());

      try {
         moveManagedFile(var0, var1);
      } catch (IOException var4) {
         throw lockedPackFileException(var0, var4);
      }

      try {
         moveManagedFile(var1, var0);
      } catch (IOException var3) {
         throw new IOException("Could not restore managed file after the update lock check: " + var1.toAbsolutePath(), var3);
      }
   }

   private static void moveManagedFile(Path var0, Path var1) throws IOException {
      try {
         Files.move(var0, var1, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException var3) {
         Files.move(var0, var1);
      }
   }

   private boolean isProjectBattleMinecraftProcessRunning() {
      return this.minecraftRunning;
   }

   private static void deletePackFileWithRetry(Path var0) throws IOException {
      IOException var1 = null;

      for (int var2 = 1; var2 <= 10; var2++) {
         try {
            Files.deleteIfExists(var0);
            return;
         } catch (IOException var4) {
            var1 = var4;
            if (var2 < 10) {
               pauseForFileUnlock(var2);
            }
         }
      }

      throw lockedPackFileException(var0, var1);
   }

   private static void replaceDownloadedFileWithRetry(Path var0, Path var1) throws IOException {
      IOException var2 = null;

      for (int var3 = 1; var3 <= 10; var3++) {
         try {
            try {
               Files.move(var0, var1, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException var5) {
               Files.move(var0, var1, StandardCopyOption.REPLACE_EXISTING);
            }

            return;
         } catch (IOException var6) {
            var2 = var6;
            if (var3 < 10) {
               pauseForFileUnlock(var3);
            }
         }
      }

      throw lockedPackFileException(var1, var2);
   }

   private static void pauseForFileUnlock(int var0) throws IOException {
      try {
         Thread.sleep(Math.min(1000L, 150L * var0));
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
         throw new IOException("Update interrupted while waiting for a managed file.", var2);
      }
   }

   private static IOException lockedPackFileException(Path var0, IOException var1) {
      return new IOException(
         "Закройте Minecraft и другие лаунчеры, затем нажмите «Загрузить» снова. Файл сборки сейчас используется другим процессом:\n" + var0.toAbsolutePath(),
         var1
      );
   }

   private void clearModsDirectory() throws IOException {
      Path var1 = this.config.instanceDir.resolve("mods");
      Files.createDirectories(var1);
      int var2 = 0;

      try (Stream<Path> var3 = Files.walk(var1)) {
         for (Path var5 : var3.filter(var1x -> !var1x.equals(var1)).sorted(Comparator.reverseOrder()).toList()) {
            Files.deleteIfExists(var5);
            var2++;
         }
      }

      this.log("Cleared mods directory before full download: " + var2 + " item(s).");
   }

   private void removeLegacyMenuMods(ProjectBattleAlpha.Manifest var1) throws IOException {
      Path var2 = this.config.instanceDir.resolve("mods");
      if (Files.isDirectory(var2)) {
         Set var3 = var1.files
            .stream()
            .map(var0 -> var0.path == null ? "" : var0.path.replace('\\', '/'))
            .filter(var0 -> var0.startsWith("mods/"))
            .map(var0 -> var0.substring(var0.lastIndexOf(47) + 1))
            .filter(var0 -> var0.startsWith("projectbattle-menu-") && var0.endsWith(".jar"))
            .collect(Collectors.toSet());

         try (Stream<Path> var4 = Files.list(var2)) {
            for (Path var6 : var4.filter(var0 -> Files.isRegularFile(var0)).toList()) {
               String var7 = var6.getFileName().toString();
               if (var7.startsWith("projectbattle-menu-") && var7.endsWith(".jar") && !var3.contains(var7)) {
                  Files.deleteIfExists(var6);
                  this.log("Removed legacy menu mod: " + var7);
               }
            }
         }
      }
   }

   private void ensureForgeInstalled() throws IOException, InterruptedException {
      if (!"1.20.1-forge-47.4.28".equals(this.config.forgeVersionId)) {
         this.log("Replacing legacy Forge profile " + this.config.forgeVersionId + " with 1.20.1-forge-47.4.28.");
         this.config.forgeVersionId = "1.20.1-forge-47.4.28";
         this.config.save();
      }

      try {
         this.config.forgeVersionId = ProjectBattleAlpha.Runner.resolveForgeVersionId(this.config.minecraftDir, this.config.forgeVersionId);
         this.config.save();
         this.log("Forge ready: " + this.config.forgeVersionId);
      } catch (FileNotFoundException var13) {
         this.log("Forge 1.20.1 is missing. Installing automatically...");
         Path var2 = this.config.configDir.resolve("cache");
         Files.createDirectories(var2);
         Path var3 = var2.resolve("forge-1.20.1-47.4.28-installer.jar");
         if (Files.exists(var3) && Files.size(var3) >= 1048576L) {
            this.log("Forge installer already cached.");
         } else {
            this.setProgress(5, "Скачивание Forge");
            this.log("Downloading Forge installer...");
            download(new URL("https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.4.28/forge-1.20.1-47.4.28-installer.jar"), var3);
         }

         this.setProgress(25, "Установка Forge");
         Files.createDirectories(this.config.minecraftDir);
         this.ensureMinecraftLauncherProfile();
         Path var4 = ProjectBattleAlpha.Runner.findJava(this.config.minecraftDir);
         this.log("Installing Forge with Java: " + var4);
         ProcessBuilder var5 = new ProcessBuilder(var4.toString(), "-jar", var3.toString(), "--installClient", this.config.minecraftDir.toString());
         var5.directory(this.config.minecraftDir.toFile());
         var5.redirectErrorStream(true);
         Process var6 = var5.start();
         BufferedReader var7 = new BufferedReader(new InputStreamReader(var6.getInputStream(), StandardCharsets.UTF_8));

         String var8;
         try {
            while ((var8 = var7.readLine()) != null) {
               if (!var8.isBlank()) {
                  this.log("[Forge] " + var8);
               }
            }
         } catch (Throwable var12) {
            try {
               var7.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }

            throw var12;
         }

         var7.close();
         int var9 = var6.waitFor();
         if (var9 != 0) {
            throw new IOException("Forge installer exited with code " + var9 + ". Check the launcher log above for [Forge] lines.");
         }

         this.config.forgeVersionId = ProjectBattleAlpha.Runner.resolveForgeVersionId(this.config.minecraftDir, this.config.forgeVersionId);
         this.config.save();
         SwingUtilities.invokeLater(() -> this.forgeField.setText(this.config.forgeVersionId));
         this.setProgress(100, "Forge ready");
         this.log("Forge installed: " + this.config.forgeVersionId);
      }
   }

   private void ensureVanillaMinecraftVersion() throws IOException {
      Path var1 = this.config.minecraftDir.resolve("versions").resolve("1.20.1");
      Path var2 = var1.resolve("1.20.1.json");
      Path var3 = var1.resolve("1.20.1.jar");
      boolean var4 = !Files.exists(var2) || Files.size(var2) < 1024L;
      boolean var5 = !Files.exists(var3) || Files.size(var3) < 1048576L;
      Files.createDirectories(var1);
      String var6;
      if (var4) {
         this.log("Minecraft 1.20.1 metadata is missing. Downloading from Mojang...");
         String var7 = readText(new URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"));
         if (!(ProjectBattleAlpha.Json.parse(stripBom(var7)) instanceof Map var8)) {
            throw new IOException("Bad Mojang version manifest.");
         }

         String var18 = null;
         Object var10 = var8.get("versions");
         if (var10 instanceof List) {
            for (Object var12 : (List)var10) {
               if (var12 instanceof Map var13 && "1.20.1".equals(str(var13.get("id")))) {
                  var18 = str(var13.get("url"));
                  break;
               }
            }
         }

         if (var18 == null || var18.isBlank()) {
            throw new IOException("Could not find Minecraft 1.20.1 in Mojang manifest.");
         }

         this.log("Downloading Minecraft 1.20.1 json.");
         var6 = readText(new URL(var18));
         Files.writeString(var2, stripBom(var6), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      } else {
         var6 = Files.readString(var2, StandardCharsets.UTF_8);
      }

      if (!(ProjectBattleAlpha.Json.parse(stripBom(var6)) instanceof Map var14)) {
         throw new IOException("Bad Minecraft 1.20.1 json.");
      } else {
         if (var5) {
            String var16 = null;
            if (var14.get("downloads") instanceof Map var19 && var19.get("client") instanceof Map var22) {
               var16 = str(var22.get("url"));
            }

            if (var16 == null || var16.isBlank()) {
               throw new IOException("Could not find Minecraft 1.20.1 client download.");
            }

            this.log("Downloading Minecraft 1.20.1 client jar.");
            download(new URL(var16), var3);
         }

         if (var14.get("assetIndex") instanceof Map var17) {
            String var21 = str(var17.get("id"));
            String var23 = str(var17.get("url"));
            if (var21 != null && var23 != null) {
               Path var26 = this.config.minecraftDir.resolve("assets").resolve("indexes").resolve(var21 + ".json");
               if (!Files.exists(var26) || Files.size(var26) < 1024L) {
                  Files.createDirectories(var26.getParent());
                  this.log("Downloading Minecraft asset index " + var21 + ".");
                  download(new URL(var23), var26);
               }

               this.ensureLanguageAssets(var26);
            }
         }
      }
   }

   private void ensureLanguageAssets(Path var1) throws IOException {
      if (Files.exists(var1)
         && ProjectBattleAlpha.Json.parse(stripBom(Files.readString(var1, StandardCharsets.UTF_8))) instanceof Map var2
         && var2.get("objects") instanceof Map var3) {
         int var16 = 0;

         for (Object entryObject : var3.entrySet()) {
            Entry var6 = (Entry)entryObject;
            String var7 = str(var6.getKey());
            if (var7 != null && var7.startsWith("minecraft/lang/") && var7.endsWith(".json") && var6.getValue() instanceof Map var8) {
               String var17 = str(var8.get("hash"));
               if (var17 != null && var17.length() >= 3) {
                  Path var10 = this.config.minecraftDir.resolve("assets").resolve("objects").resolve(var17.substring(0, 2)).resolve(var17);
                  long var11 = var8.get("size") instanceof Number var13 ? var13.longValue() : -1L;
                  boolean var18 = Files.exists(var10);
                  if (var18 && var11 >= 0L) {
                     var18 = Files.size(var10) == var11;
                  }

                  if (var18) {
                     var18 = var17.equalsIgnoreCase(sha1(var10));
                  }

                  if (!var18) {
                     Files.createDirectories(var10.getParent());
                     download(new URL("https://resources.download.minecraft.net/" + var17.substring(0, 2) + "/" + var17), var10);
                     var16++;
                  }
               }
            }
         }

         if (var16 > 0) {
            this.log("Downloaded Minecraft language assets: " + var16);
         }
      }
   }

   private void ensurePreferredMinecraftLanguageOnce() throws IOException {
      Files.createDirectories(this.config.instanceDir);
      Path var1 = this.config.instanceDir.resolve(".projectbattle-language-initialized");
      if (!Files.exists(var1)) {
         Path var2 = this.config.instanceDir.resolve("options.txt");
         ArrayList var3 = Files.exists(var2) ? new ArrayList<>(Files.readAllLines(var2, StandardCharsets.UTF_8)) : new ArrayList();
         String var4 = "en".equalsIgnoreCase(this.config.language) ? "en_us" : "ru_ru";
         boolean var5 = false;
         boolean var6 = false;

         for (int var7 = 0; var7 < var3.size(); var7++) {
            String var8 = (String)var3.get(var7);
            if (var8.startsWith("lang:")) {
               var5 = true;
               String var9 = var8.substring("lang:".length()).trim();
               if ("en_us".equalsIgnoreCase(var9) || var9.isBlank()) {
                  var3.set(var7, "lang:" + var4);
                  var6 = true;
               }
               break;
            }
         }

         if (!var5) {
            var3.add("lang:" + var4);
            var6 = true;
         }

         if (var6) {
            Files.write(var2, var3, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            this.log("Minecraft language initialized: " + var4 + ".");
         } else {
            this.log("Minecraft language preference preserved.");
         }

         Files.writeString(var1, "0.1.78.13" + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
      }
   }

   private void ensureRequiredResourcePackEnabled() throws IOException {
      String var1 = "file/ProjectBattle-Server-Resources.zip";
      Path var2 = this.config.instanceDir.resolve("resourcepacks").resolve("ProjectBattle-Server-Resources.zip");
      if (!Files.isRegularFile(var2)) {
         this.log("Required Project Battle resource pack is not installed yet.");
      } else {
         Path var3 = this.config.instanceDir.resolve("options.txt");
         ArrayList var4 = Files.exists(var3) ? new ArrayList<>(Files.readAllLines(var3, StandardCharsets.UTF_8)) : new ArrayList();
         boolean var5 = false;
         boolean var6 = false;

         for (int var7 = 0; var7 < var4.size(); var7++) {
            String var8 = (String)var4.get(var7);
            if (var8.startsWith("resourcePacks:")) {
               var5 = true;
               if (!var8.contains("\"" + var1 + "\"")) {
                  int var9 = var8.lastIndexOf(93);
                  String var10 = var9 >= 0 ? var8.substring(0, var9) : "resourcePacks:[\"vanilla\"";
                  if (!var10.endsWith("[")) {
                     var10 = var10 + ",";
                  }

                  var4.set(var7, var10 + "\"" + var1 + "\"]");
                  var6 = true;
               }
               break;
            }
         }

         if (!var5) {
            var4.add("resourcePacks:[\"vanilla\",\"" + var1 + "\"]");
            var6 = true;
         }

         if (var6) {
            Files.write(var3, var4, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            this.log("Project Battle resource pack enabled automatically.");
         }
      }
   }

   private void ensureMinecraftLauncherProfile() throws IOException {
      Path var1 = this.config.minecraftDir.resolve("launcher_profiles.json");
      if (Files.exists(var1) && Files.size(var1) > 8L) {
         String var2 = Files.readString(var1, StandardCharsets.UTF_8).stripLeading();
         if (var2.startsWith("{") && var2.contains("\"profiles\"")) {
            return;
         }

         Path var3 = this.config.minecraftDir.resolve("launcher_profiles.projectbattle.backup.json");
         Files.copy(var1, var3, StandardCopyOption.REPLACE_EXISTING);
         this.log("Backed up invalid launcher profile.");
      }

      String var4 = "{\n  \"profiles\": {},\n  \"selectedProfile\": \"\"\n}\n";
      Files.writeString(var1, var4, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      this.log("Created Minecraft launcher profile for Forge installer.");
   }

   private static List<String> replace(List<String> var0, Map<String, String> var1) {
      ArrayList var2 = new ArrayList();

      for (String var4 : var0) {
         String var5 = var4;

         for (Entry var7 : var1.entrySet()) {
            var5 = var5.replace("${" + (String)var7.getKey() + "}", (CharSequence)var7.getValue());
         }

         if (!var5.isBlank() && !var5.contains("${") && !var5.equals("-XstartOnFirstThread")) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private static List<String> sanitizeGameArgs(List<String> var0) {
      Set var1 = Set.of(
         "--username",
         "--version",
         "--gameDir",
         "--assetsDir",
         "--assetIndex",
         "--uuid",
         "--accessToken",
         "--clientId",
         "--xuid",
         "--userType",
         "--versionType",
         "--width",
         "--height",
         "--quickPlayPath",
         "--quickPlaySingleplayer",
         "--quickPlayMultiplayer",
         "--quickPlayRealms",
         "--server",
         "--port"
      );
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var0.size(); var3++) {
         String var4 = (String)var0.get(var3);
         if (!var1.contains(var4)) {
            var2.add(var4);
         } else if (var3 + 1 < var0.size()) {
            String var5 = (String)var0.get(var3 + 1);
            if (var5 != null && !var5.isBlank() && !var5.startsWith("--")) {
               var2.add(var4);
               var2.add(var5);
               var3++;
            }
         }
      }

      return var2;
   }

   private static String join(List<Path> var0) {
      StringJoiner var1 = new StringJoiner(File.pathSeparator);

      for (Path var3 : var0) {
         var1.add(var3.toString());
      }

      return var1.toString();
   }

   private static Path prepareNativeDirectory(Path var0, String var1) throws IOException {
      Path var2 = var0.resolve("natives").resolve(var1).resolve("windows").resolve("x64");
      Files.createDirectories(var2);
      Path var3 = var2.resolve("run-" + Long.toUnsignedString(System.nanoTime()));
      Files.createDirectories(var3);
      return var3;
   }

   private static void extractNatives(List<Path> var0, Path var1) throws IOException {
      Files.createDirectories(var1);

      for (Path var3 : var0) {
         ZipEntry var4;
         try (ZipInputStream var5 = new ZipInputStream(Files.newInputStream(var3))) {
            while ((var4 = var5.getNextEntry()) != null) {
               String var6 = var4.getName().replace('\\', '/');
               if (!var4.isDirectory() && !var6.startsWith("META-INF/")) {
                  Path var7;
                  if (var6.toLowerCase(Locale.ROOT).endsWith(".dll")) {
                     var7 = var1.resolve(Paths.get(var6).getFileName().toString());
                  } else {
                     var7 = safeResolve(var1, var6);
                  }

                  Files.createDirectories(var7.getParent());
                  Files.copy(var5, var7, StandardCopyOption.REPLACE_EXISTING);
               }
            }
         }
      }
   }

   private static String normalizeManifestLocation(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (var1.isEmpty()) {
         return DEFAULT_MANIFEST_URL;
      }

      if (isRemoteUrl(var1)) {
         return var1;
      }

      if (!isRemoteUrl(var1)) {
         try {
            if (!Files.exists(Paths.get(var1))) {
               return DEFAULT_MANIFEST_URL;
            }
         } catch (RuntimeException var3) {
            return DEFAULT_MANIFEST_URL;
         }
      }

      return var1;
   }

   private static boolean isRemoteUrl(String var0) {
      return var0 != null && (var0.startsWith("http://") || var0.startsWith("https://"));
   }

   private static String readManifestLocation(String var0) throws IOException {
      return isRemoteUrl(var0) ? readText(new URL(var0)) : Files.readString(Paths.get(var0), StandardCharsets.UTF_8);
   }

   private static String resolveReleaseManifestUrl() throws IOException {
      if (!(
         ProjectBattleAlpha.Json.parse(stripBom(readText(new URL("https://api.github.com/repos/vok1si/project-battle-pack-NOW/releases/tags/v0.1.0")))) instanceof Map var0
      )) {
         throw new IOException("Bad GitHub release response.");
      } else {
         Object var9 = var0.get("assets");
         String var2 = "";
         if (var9 instanceof List) {
            for (Object var4 : (List)var9) {
               if (var4 instanceof Map var5) {
                  String var6 = str(var5.get("name")).trim();
                  String var7 = str(var5.get("browser_download_url")).trim();
                  if (!var6.isEmpty() && !var7.isEmpty()) {
                     String var8 = var6.toLowerCase(Locale.ROOT);
                     if (var8.startsWith("launcher-manifest.project-battle.generated") && var8.endsWith(".json")) {
                        if ("launcher-manifest.project-battle.generated.json".equals(var6)) {
                           return var7;
                        }

                        if (var2.isEmpty()) {
                           var2 = var7;
                        }
                     }
                  }
               }
            }
         }

         if (!var2.isEmpty()) {
            return var2;
         } else {
            throw new FileNotFoundException("GitHub release does not contain launcher-manifest.project-battle.generated*.json");
         }
      }
   }

   private static String normalizeServer(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      return !var1.isEmpty() && !"127.0.0.1:25565".equals(var1) && !"localhost:25565".equalsIgnoreCase(var1) ? var1 : "ProjectBattle.exaroton.me:33061";
   }

   private static String normalizeForgeVersionId(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (var1.isEmpty()) {
         return "1.20.1-forge-47.4.28";
      }

      String var2 = var1.toLowerCase(Locale.ROOT);
      return !"1.20.1".equals(var1) && !"forge".equals(var2) ? var1 : "1.20.1-forge-47.4.28";
   }

   private static boolean minecraftStatusPing(String var0, int var1, int var2) {
      try (Socket var4 = new Socket()) {
         var4.connect(new InetSocketAddress(var0, var1), var2);
         var4.setSoTimeout(var2);
         OutputStream var5 = var4.getOutputStream();
         InputStream var6 = var4.getInputStream();
         ByteArrayOutputStream var7 = new ByteArrayOutputStream();
         writeVarInt(var7, 0);
         writeVarInt(var7, 763);
         writeString(var7, var0);
         var7.write(var1 >>> 8 & 0xFF);
         var7.write(var1 & 0xFF);
         writeVarInt(var7, 1);
         writeVarInt(var5, var7.size());
         var5.write(var7.toByteArray());
         ByteArrayOutputStream var8 = new ByteArrayOutputStream();
         writeVarInt(var8, 0);
         writeVarInt(var5, var8.size());
         var5.write(var8.toByteArray());
         var5.flush();
         int var9 = readVarInt(var6);
         if (var9 <= 0) {
            return false;
         }

         int var10 = readVarInt(var6);
         int var11 = readVarInt(var6);
         if (var10 != 0 || var11 <= 0 || var11 > 1048576) {
            return false;
         }

         byte[] var12 = var6.readNBytes(var11);
         if (var12.length != var11) {
            return false;
         }

         String var13 = new String(var12, StandardCharsets.UTF_8);
         return var13.contains("\"version\"") || var13.contains("\"players\"");
      } catch (IOException | RuntimeException var16) {
         return false;
      }
   }

   private static void writeString(OutputStream var0, String var1) throws IOException {
      byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
      writeVarInt(var0, var2.length);
      var0.write(var2);
   }

   private static void writeVarInt(OutputStream var0, int var1) throws IOException {
      while ((var1 & -128) != 0) {
         var0.write(var1 & 127 | 128);
         var1 >>>= 7;
      }

      var0.write(var1);
   }

   private static int readVarInt(InputStream var0) throws IOException {
      int var1 = 0;
      int var2 = 0;

      int var3;
      do {
         var3 = var0.read();
         if (var3 == -1) {
            throw new EOFException("Unexpected end of Minecraft status response.");
         }

         var2 |= (var3 & 127) << 7 * var1;
         if (++var1 > 5) {
            throw new IOException("Minecraft status VarInt is too big.");
         }
      } while ((var3 & 128) != 0);

      return var2;
   }

   private static String stripBom(String var0) {
      return var0 != null && !var0.isEmpty() && var0.charAt(0) == '\ufeff' ? var0.substring(1) : var0;
   }

   private static String readText(URL var0) throws IOException {
      IOException var1 = null;

      for (int var2 = 1; var2 <= 3; var2++) {
         HttpURLConnection var3 = null;

         try {
            label149: {
               var3 = (HttpURLConnection)var0.openConnection();
               var3.setInstanceFollowRedirects(true);
               var3.setRequestProperty("User-Agent", "Project Battle Alpha/0.1.78.13");
               var3.setConnectTimeout(45000);
               var3.setReadTimeout(180000);
               int var5 = var3.getResponseCode();
               if (var5 < 200 || var5 >= 300) {
                  IOException var18 = new IOException("HTTP " + var5 + " while loading manifest: " + var0);
                  if (isTransientNetworkFailure(var18) && var2 != 3) {
                     var1 = var18;
                     break label149;
                  }

                  throw var18;
               }

               try (InputStream var6 = var3.getInputStream()) {
                  return new String(var6.readAllBytes(), StandardCharsets.UTF_8);
               }
            }
         } catch (IOException var16) {
            var1 = var16;
            if (var2 == 3 || !isTransientNetworkFailure(var16)) {
               throw new IOException("Could not load " + var0 + " after " + var2 + " attempt(s): " + var16.getMessage(), var16);
            }
         } finally {
            if (var3 != null) {
               var3.disconnect();
            }
         }

         pauseBeforeRetry(var2, var1);
      }

      throw new IOException("Could not load " + var0 + " after 3 attempts.", var1);
   }

   private static void download(URL var0, Path var1) throws IOException {
      Path var2 = var1.resolveSibling(var1.getFileName() + ".part");
      IOException var3 = null;

      for (int var4 = 1; var4 <= 3; var4++) {
         HttpURLConnection var5 = null;

         label136: {
            try {
               Files.deleteIfExists(var2);
               var5 = (HttpURLConnection)var0.openConnection();
               var5.setInstanceFollowRedirects(true);
               var5.setRequestProperty("User-Agent", "Project Battle Alpha/0.1.78.13");
               var5.setConnectTimeout(45000);
               var5.setReadTimeout(180000);
               int var6 = var5.getResponseCode();
               if (var6 < 200 || var6 >= 300) {
                  IOException var19 = new IOException("HTTP " + var6 + " while downloading pack file: " + fileNameFromUrl(var0));
                  if (isTransientNetworkFailure(var19) && var4 != 3) {
                     var3 = var19;
                     break label136;
                  }

                  throw var19;
               }

               try (InputStream var7 = var5.getInputStream()) {
                  Files.copy(var7, var2, StandardCopyOption.REPLACE_EXISTING);
               }

               replaceDownloadedFileWithRetry(var2, var1);
            } catch (IOException var17) {
               var3 = var17;
               Files.deleteIfExists(var2);
               if (var4 != 3 && isTransientNetworkFailure(var17)) {
                  break label136;
               }

               throw new IOException("Could not download " + fileNameFromUrl(var0) + " after " + var4 + " attempt(s): " + var17.getMessage(), var17);
            } finally {
               if (var5 != null) {
                  var5.disconnect();
               }
            }

            return;
         }

         pauseBeforeRetry(var4, var3);
      }

      throw new IOException("Could not download " + fileNameFromUrl(var0) + " after 3 attempts.", var3);
   }

   private static boolean isTransientNetworkFailure(IOException var0) {
      if (!(var0 instanceof ConnectException) && !(var0 instanceof SocketTimeoutException) && !(var0 instanceof UnknownHostException)) {
         String var1 = var0.getMessage() == null ? "" : var0.getMessage().toLowerCase(Locale.ROOT);
         return var1.contains("timed out")
            || var1.contains("connection reset")
            || var1.contains("connection refused")
            || var1.contains("http 408")
            || var1.contains("http 429")
            || var1.contains("http 500")
            || var1.contains("http 502")
            || var1.contains("http 503")
            || var1.contains("http 504");
      } else {
         return true;
      }
   }

   private static void pauseBeforeRetry(int var0, IOException var1) throws IOException {
      try {
         Thread.sleep(1500L * var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw new IOException("Network retry interrupted.", var3);
      }
   }

   private static String fileNameFromUrl(URL var0) {
      String var1 = var0.getPath();
      int var2 = var1 == null ? -1 : var1.lastIndexOf(47);
      return var2 >= 0 && var2 + 1 < var1.length() ? var1.substring(var2 + 1) : var0.toString();
   }

   private static String sha256(Path var0) throws IOException {
      return hash(var0, "SHA-256");
   }

   private static String sha1(Path var0) throws IOException {
      return hash(var0, "SHA-1");
   }

   private static String hash(Path var0, String var1) throws IOException {
      try {
         MessageDigest var2 = MessageDigest.getInstance(var1);

         try (InputStream var3 = Files.newInputStream(var0)) {
            byte[] var4 = new byte[8192];

            int var5;
            while ((var5 = var3.read(var4)) >= 0) {
               var2.update(var4, 0, var5);
            }
         }

         StringBuilder var11 = new StringBuilder();

         for (byte var7 : var2.digest()) {
            var11.append(String.format("%02x", var7));
         }

         return var11.toString();
      } catch (Exception var10) {
         throw new IOException("Could not hash " + var0, var10);
      }
   }

   private static Path safeResolve(Path var0, String var1) throws IOException {
      Path var2 = var0.toAbsolutePath().normalize();
      Path var3 = var2.resolve(var1).normalize().toAbsolutePath();
      if (!var3.startsWith(var2)) {
         throw new IOException("Unsafe path: " + var1);
      } else {
         Path cursor = var2;
         for (Path component : var2.relativize(var3)) {
            cursor = cursor.resolve(component);
            if (Files.isSymbolicLink(cursor)) throw new IOException("Symbolic link in managed path: " + var1);
         }
         return var3;
      }
   }

   private static void hidePath(Path var0) {
      try {
         if (Files.exists(var0)) {
            Files.setAttribute(var0, "dos:hidden", true);
         }
      } catch (Exception var2) {
      }
   }

   private static Path appDir() {
      try {
         Path var0 = Paths.get(ProjectBattleAlpha.class.getProtectionDomain().getCodeSource().getLocation().toURI());
         return Files.isRegularFile(var0) ? var0.getParent() : var0;
      } catch (Exception var1) {
         return Paths.get(".").toAbsolutePath().normalize();
      }
   }

   private static BufferedImage loadImage(String var0) {
      try (InputStream var2 = ProjectBattleAlpha.class.getResourceAsStream(var0)) {
         return var2 == null ? null : ImageIO.read(var2);
      } catch (IOException var7) {
         return null;
      }
   }

   private static BufferedImage makeAvatarImage(BufferedImage var0, int var1) {
      BufferedImage var2 = new BufferedImage(var1, var1, 2);
      Graphics2D var3 = var2.createGraphics();

      try {
         var3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         if (var0 != null) {
            double var4 = Math.max((double)var1 / var0.getWidth(), (double)var1 / var0.getHeight());
            int var6 = Math.max(1, (int)Math.round(var0.getWidth() * var4));
            int var7 = Math.max(1, (int)Math.round(var0.getHeight() * var4));
            int var8 = (var1 - var6) / 2;
            int var9 = (var1 - var7) / 2;
            var3.drawImage(var0, var8, var9, var6, var7, null);
            var3.setComposite(AlphaComposite.SrcOver.derive(0.7F));
            var3.setColor(new Color(15, 18, 18));
            var3.fillRect(0, 0, var1, var1);
            var3.setComposite(AlphaComposite.SrcOver);
         }
      } finally {
         var3.dispose();
      }

      return var2;
   }

   private static String normalPath(String var0) {
      return var0 == null ? null : var0.replace('\\', '/').replaceAll("^/+", "");
   }

   private static String sanitizeName(String var0) {
      String var1 = var0 == null ? "" : var0.trim().replaceAll("[^A-Za-z0-9_]", "_");
      if (var1.length() < 3) {
         var1 = "Player";
      }

      return var1.length() > 16 ? var1.substring(0, 16) : var1;
   }

   private static int parseInt(String var0, int var1) {
      try {
         return Integer.parseInt(var0.trim());
      } catch (Exception var3) {
         return var1;
      }
   }

   private static int normalizeRamMb(int var0) {
      if (var0 <= 0) {
         return 8192;
      }

      int var1 = Math.max(2048, Math.min(MAX_RAM_MB, var0));
      return Math.round(var1 / 1024.0F) * 1024;
   }

   private static int detectMaxRamMb() {
      try {
         if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean var0) {
            long var6 = var0.getTotalMemorySize() / 1048576L;
            long var3 = Math.max(2048L, var6 / 1024L * 1024L);
            return (int)Math.min(var3, 262144L);
         }
      } catch (RuntimeException var5) {
      }

      return 16384;
   }

   private static String formatRamGiB(int var0) {
      return Math.max(2, Math.round(var0 / 1024.0F)) + " ГиБ";
   }

   private static long asLong(Object var0, long var1) {
      if (var0 instanceof Number var3) {
         return var3.longValue();
      } else {
         try {
            return var0 == null ? var1 : Long.parseLong(var0.toString());
         } catch (Exception var5) {
            return var1;
         }
      }
   }

   private static boolean asBool(Object var0, boolean var1) {
      if (var0 instanceof Boolean var2) {
         return var2;
      } else {
         return var0 == null ? var1 : Boolean.parseBoolean(var0.toString());
      }
   }

   private static String str(Object var0) {
      return var0 == null ? "" : String.valueOf(var0);
   }

   private record Address(String host, int port) {
      static ProjectBattleAlpha.Address parse(String var0) {
         String[] var1 = var0.trim().split(":", 2);
         return new ProjectBattleAlpha.Address(var1[0].isBlank() ? "127.0.0.1" : var1[0], var1.length > 1 ? ProjectBattleAlpha.parseInt(var1[1], 25565) : 25565);
      }
   }

   private static final class ArenaTransitionOverlay extends JComponent {
      private final BufferedImage image;
      private final boolean enteringArena;
      private final Timer timer;
      private long startedAt;
      private float opacity;
      private boolean midpointCalled;
      private Runnable midpoint;
      private Runnable finished;

      ArenaTransitionOverlay(BufferedImage var1, boolean var2) {
         this.image = var1;
         this.enteringArena = var2;
         this.setOpaque(false);
         this.setCursor(ProjectBattleAlpha.launcherCursor(3));
         MouseAdapter var3 = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent var1) {
               var1.consume();
            }

            @Override
            public void mouseReleased(MouseEvent var1) {
               var1.consume();
            }

            @Override
            public void mouseClicked(MouseEvent var1) {
               var1.consume();
            }
         };
         this.addMouseListener(var3);
         this.timer = new Timer(16, var1x -> this.advance());
      }

      void start(Runnable var1, Runnable var2) {
         this.midpoint = var1;
         this.finished = var2;
         this.startedAt = System.nanoTime();
         this.opacity = 0.0F;
         this.midpointCalled = false;
         this.setVisible(true);
         this.timer.start();
      }

      private void advance() {
         long var1 = (System.nanoTime() - this.startedAt) / 1000000L;
         if (var1 < 450L) {
            this.opacity = (float)var1 / 450.0F;
         } else if (var1 < 3500L) {
            this.opacity = 1.0F;
         } else {
            this.opacity = Math.max(0.0F, (float)(4000L - var1) / 500.0F);
         }

         if (!this.midpointCalled && var1 >= 3500L) {
            this.midpointCalled = true;
            if (this.midpoint != null) {
               this.midpoint.run();
            }
         }

         if (var1 >= 4000L) {
            this.timer.stop();
            this.opacity = 0.0F;
            if (this.finished != null) {
               this.finished.run();
            }
         }

         this.repaint();
      }

      @Override
      protected void paintComponent(Graphics var1) {
         if (!(this.opacity <= 0.0F)) {
            Graphics2D var2 = (Graphics2D)var1.create();
            var2.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
            int var3 = this.getWidth();
            int var4 = this.getHeight();
            var2.setColor(new Color(8, 11, 12));
            var2.fillRect(0, 0, var3, var4);
            if (this.image != null) {
               var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
               double var5 = Math.max((double)var3 / this.image.getWidth(), (double)var4 / this.image.getHeight());
               int var7 = (int)Math.round(this.image.getWidth() * var5);
               int var8 = (int)Math.round(this.image.getHeight() * var5);
               int var9 = (var3 - var7) / 2;
               int var10 = (var4 - var8) / 2;
               var2.drawImage(this.image, var9, var10, var7, var8, null);
               long var11 = (System.nanoTime() - this.startedAt) / 1000000L;
               int var13 = (int)(var11 / 390L % 3L);
               float var14 = (float)(var11 % 390L) / 390.0F;
               float var15 = (float)Math.sin(Math.PI * var14);
               double[] var16 = this.enteringArena ? new double[]{0.4994, 0.451, 0.5478} : new double[]{0.505, 0.47, 0.541};
               double[] var17 = this.enteringArena ? new double[]{0.409, 0.553, 0.553} : new double[]{0.423, 0.541, 0.541};
               int var18 = var9 + (int)Math.round(this.image.getWidth() * var16[var13] * var5);
               int var19 = var10 + (int)Math.round(this.image.getHeight() * var17[var13] * var5);
               int var20 = Math.max(8, (int)Math.round(13.0 * var5));
               int var21 = Math.max(4, (int)Math.round(6.0 * var5));
               var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               var2.setColor(new Color(226, 209, 151, Math.round(38.0F + 45.0F * var15)));
               var2.fillOval(var18 - var20, var19 - var20, var20 * 2, var20 * 2);
               var2.setColor(new Color(239, 222, 164, Math.round(105.0F + 90.0F * var15)));
               var2.fillOval(var18 - var21, var19 - var21, var21 * 2, var21 * 2);
            } else {
               var2.setColor(new Color(255, 255, 255, 9));

               for (byte var22 = 0; var22 < var4; var22 += 3) {
                  var2.drawLine(0, var22, var3, var22);
               }
            }

            var2.dispose();
         }
      }
   }

   private static final class BackgroundPanel extends JPanel {
      private final BufferedImage background;

      BackgroundPanel(LayoutManager var1, BufferedImage var2) {
         super(var1);
         this.background = var2;
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         Graphics2D var2 = (Graphics2D)var1.create();
         paintBackground(var2, this.getWidth(), this.getHeight(), this.background);
         var2.dispose();
      }

      private static void paintBackground(Graphics2D var0, int var1, int var2, BufferedImage var3) {
         var0.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         if (var3 != null) {
            double var4 = Math.max((double)var1 / var3.getWidth(), (double)var2 / var3.getHeight());
            int var6 = (int)Math.round(var3.getWidth() * var4);
            int var7 = (int)Math.round(var3.getHeight() * var4);
            var0.drawImage(var3, (var1 - var6) / 2, (var2 - var7) / 2, var6, var7, null);
         } else {
            var0.setPaint(new GradientPaint(0.0F, 0.0F, new Color(11, 14, 13), var1, var2, new Color(42, 28, 22)));
            var0.fillRect(0, 0, var1, var2);
         }

         var0.setPaint(new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 205), var1, var2, new Color(0, 0, 0, 120)));
         var0.fillRect(0, 0, var1, var2);
      }
   }

   private static final class BsgLogoPanel extends JPanel {
      private final BufferedImage image;

      BsgLogoPanel(BufferedImage var1) {
         this.image = var1;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         int var3 = this.getWidth();
         int var4 = this.getHeight();
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(9, 13, 13, 150), var3, var4, new Color(42, 30, 22, 92)));
         var2.fillRect(0, 0, var3, var4);
         if (this.image != null) {
            int var5 = (int)Math.round(this.image.getWidth() * 0.82);
            double var6 = Math.min((double)var3 / var5, (double)var4 / this.image.getHeight());
            int var8 = Math.max(1, (int)Math.round(var5 * var6));
            int var9 = Math.max(1, (int)Math.round(this.image.getHeight() * var6));
            int var10 = (var3 - var8) / 2;
            int var11 = (var4 - var9) / 2;
            var2.setComposite(AlphaComposite.SrcOver.derive(0.84F));
            var2.drawImage(this.image, var10, var11, var10 + var8, var11 + var9, 0, 0, var5, this.image.getHeight(), null);
            var2.setComposite(AlphaComposite.SrcOver);
         }

         var2.setColor(new Color(0, 0, 0, 82));
         var2.fillRect(0, 0, 26, var4);
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 120), var3, 0.0F, new Color(0, 0, 0, 0)));
         var2.fillRect(0, 0, var3, var4);
         var2.setPaint(new GradientPaint(var3 - 120, 0.0F, new Color(190, 116, 54, 0), var3, 0.0F, new Color(190, 116, 54, 24)));
         var2.fillRect(0, 0, var3, var4);
         var2.setColor(new Color(215, 210, 190, 30));

         for (byte var12 = 0; var12 < var4; var12 += 3) {
            var2.drawLine(0, var12, var3, var12);
         }

         var2.setColor(new Color(0, 0, 0, 70));

         for (byte var13 = 1; var13 < var4; var13 += 3) {
            var2.drawLine(0, var13, var3, var13);
         }

         var2.dispose();
      }
   }

   private static final class BsgPromoCardPanel extends JPanel {
      private final BufferedImage image;
      private final boolean large;

      BsgPromoCardPanel(BufferedImage var1, boolean var2) {
         this.image = var1;
         this.large = var2;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         int var3 = this.getWidth();
         int var4 = this.getHeight();
         var2.setColor(this.image == null ? new Color(8, 12, 12, 54) : new Color(12, 17, 18));
         var2.fillRect(0, 0, var3, var4);
         if (this.image != null) {
            double var5 = Math.max((double)var3 / this.image.getWidth(), (double)var4 / this.image.getHeight());
            int var7 = Math.max(1, (int)Math.round(this.image.getWidth() * var5));
            int var8 = Math.max(1, (int)Math.round(this.image.getHeight() * var5));
            int var9 = (var3 - var7) / 2;
            int var10 = (var4 - var8) / 2;
            var2.setComposite(AlphaComposite.SrcOver.derive(this.large ? 0.78F : 0.68F));
            var2.drawImage(this.image, var9, var10, var7, var8, null);
            var2.setComposite(AlphaComposite.SrcOver);
         }

         var2.setPaint(
            new GradientPaint(
               0.0F,
               0.0F,
               this.image == null ? new Color(0, 0, 0, 8) : new Color(0, 0, 0, 36),
               0.0F,
               var4,
               this.image == null ? new Color(0, 0, 0, 92) : new Color(0, 0, 0, 202)
            )
         );
         var2.fillRect(0, 0, var3, var4);
         var2.setPaint(
            new GradientPaint(
               0.0F, var4 - 94, new Color(13, 17, 17, this.image == null ? 8 : 30), 0.0F, var4, new Color(13, 17, 17, this.image == null ? 105 : 236)
            )
         );
         var2.fillRect(0, Math.max(0, var4 - 94), var3, Math.min(94, var4));
         if (this.image != null) {
            var2.setColor(new Color(218, 212, 190, 28));

            for (byte var11 = 0; var11 < var4; var11 += 3) {
               var2.drawLine(0, var11, var3, var11);
            }

            var2.setColor(new Color(0, 0, 0, 65));

            for (byte var12 = 1; var12 < var4; var12 += 3) {
               var2.drawLine(0, var12, var3, var12);
            }
         }

         var2.setColor(new Color(98, 111, 108));
         var2.drawRect(0, 0, var3 - 1, var4 - 1);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class Colors {
      static final Color SIDEBAR = new Color(14, 20, 20);
      static final Color TOP = new Color(21, 20, 19);
      static final Color PANEL = new Color(16, 22, 23);
      static final Color STAGE_CLEAR = new Color(11, 14, 14);
      static final Color PANEL_LIGHT = new Color(38, 47, 45);
      static final Color BUTTON = new Color(30, 38, 38);
      static final Color BORDER = new Color(82, 91, 86);
      static final Color TEXT = new Color(232, 232, 222);
      static final Color MUTED = new Color(142, 148, 140);
      static final Color LOGO = new Color(218, 222, 204);
      static final Color PLAY = new Color(214, 198, 151);
      static final Color PLAY_LIGHT = new Color(238, 232, 196);
      static final Color PLAY_TEXT = new Color(70, 52, 38);
      static final Color ONLINE = new Color(116, 220, 35);
      static final Color ERROR = new Color(255, 105, 105);
   }

   private static final class Config {
      final Path configDir;
      final Path configFile;
      Path minecraftDir;
      Path instanceDir;
      String playerName;
      String forgeVersionId = "1.20.1-forge-47.4.28";
      String server = "ProjectBattle.exaroton.me:33061";
      String manifestLocation = ProjectBattleAlpha.DEFAULT_MANIFEST_URL;
      String authApiUrl = "https://project-battle-auth-api-yp7n.onrender.com";
      String authToken = "";
      String authEmail = "";
      String authNickname = "";
      String authRole = "user";
      String gameTicket = "";
      String language = "ru";
      String closeAction = "exit";
      String launchAction = "keep";
      int ramMb = 8192;
      boolean cleanManagedDirs = true;
      boolean autoJoinServer = true;
      boolean rememberNick = true;
      boolean rememberAccount = true;
      boolean discordPresenceEnabled = true;

      private Config(Path var1) {
         this.configDir = var1;
         this.configFile = var1.resolve("launcher.properties");
         Path var2 = Paths.get(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home")));
         this.minecraftDir = var2.resolve(".minecraft");
         this.instanceDir = var2.resolve(".projectbattle");
         this.playerName = ProjectBattleAlpha.sanitizeName(System.getProperty("user.name", "Player"));
      }

      static ProjectBattleAlpha.Config load() {
         Path var0 = Paths.get(System.getenv().getOrDefault("APPDATA", System.getProperty("user.home")));
         ProjectBattleAlpha.Config var1 = new ProjectBattleAlpha.Config(var0.resolve("ProjectBattleAlpha"));
         if (!Files.exists(var1.configFile)) {
            if (!ProjectBattleAlpha.MANIFEST_OVERRIDE_URL.isEmpty()) {
               var1.manifestLocation = ProjectBattleAlpha.normalizeManifestLocation(ProjectBattleAlpha.MANIFEST_OVERRIDE_URL);
            }

            return var1;
         } else {
            Properties var2 = new Properties();

            try (BufferedReader var3 = Files.newBufferedReader(var1.configFile, StandardCharsets.UTF_8)) {
               var2.load(var3);
               var1.minecraftDir = Paths.get(var2.getProperty("minecraftDir", var1.minecraftDir.toString()));
               var1.instanceDir = Paths.get(var2.getProperty("instanceDir", var1.instanceDir.toString()));
               var1.playerName = ProjectBattleAlpha.sanitizeName(var2.getProperty("playerName", var1.playerName));
               var1.forgeVersionId = ProjectBattleAlpha.normalizeForgeVersionId(var2.getProperty("forgeVersionId", var1.forgeVersionId));
               var1.server = ProjectBattleAlpha.normalizeServer(var2.getProperty("server", var1.server));
               if ("PBvrsAAS.exaroton.me:33061".equalsIgnoreCase(var1.server)) {
                  var1.server = "ProjectBattle.exaroton.me:33061";
               }

               var1.manifestLocation = ProjectBattleAlpha.normalizeManifestLocation(var2.getProperty("manifestLocation", var1.manifestLocation));
               var1.authApiUrl = var2.getProperty("authApiUrl", var1.authApiUrl);
               if (var1.manifestLocation.contains("github.com/vok1su/")) {
                  var1.manifestLocation = var1.manifestLocation.replace("github.com/vok1su/", "github.com/vok1si/");
               }

               if ("https://project-battle-auth-api-1.onrender.com".equalsIgnoreCase(var1.authApiUrl)
                  || "https://project-battle-auth-api.onrender.com".equalsIgnoreCase(var1.authApiUrl)) {
                  var1.authApiUrl = "https://project-battle-auth-api-yp7n.onrender.com";
               }

               var1.authToken = var2.getProperty("authToken", var1.authToken);
               var1.authEmail = var2.getProperty("authEmail", var1.authEmail);
               var1.authNickname = ProjectBattleAlpha.sanitizeName(var2.getProperty("authNickname", var1.authNickname));
               var1.authRole = var2.getProperty("authRole", var1.authRole);
               var1.language = var2.getProperty("language", var1.language);
               var1.closeAction = var2.getProperty("closeAction", var1.closeAction);
               var1.launchAction = var2.getProperty("launchAction", var1.launchAction);
               var1.ramMb = ProjectBattleAlpha.normalizeRamMb(ProjectBattleAlpha.parseInt(var2.getProperty("ramMb", Integer.toString(var1.ramMb)), var1.ramMb));
               var1.cleanManagedDirs = Boolean.parseBoolean(var2.getProperty("cleanManagedDirs", Boolean.toString(var1.cleanManagedDirs)));
               var1.autoJoinServer = Boolean.parseBoolean(var2.getProperty("autoJoinServer", Boolean.toString(var1.autoJoinServer)));
               var1.rememberNick = Boolean.parseBoolean(var2.getProperty("rememberNick", Boolean.toString(var1.rememberNick)));
               var1.rememberAccount = Boolean.parseBoolean(var2.getProperty("rememberAccount", Boolean.toString(var1.rememberAccount)));
               var1.discordPresenceEnabled = Boolean.parseBoolean(var2.getProperty("discordPresenceEnabled", Boolean.toString(var1.discordPresenceEnabled)));
            } catch (IOException var8) {
            }

            if (!ProjectBattleAlpha.MANIFEST_OVERRIDE_URL.isEmpty()) {
               var1.manifestLocation = ProjectBattleAlpha.normalizeManifestLocation(ProjectBattleAlpha.MANIFEST_OVERRIDE_URL);
            }

            return var1;
         }
      }

      void save() throws IOException {
         Files.createDirectories(this.configDir);
         Properties var1 = new Properties();
         var1.setProperty("minecraftDir", this.minecraftDir.toString());
         var1.setProperty("instanceDir", this.instanceDir.toString());
         var1.setProperty("playerName", this.playerName);
         var1.setProperty("forgeVersionId", ProjectBattleAlpha.normalizeForgeVersionId(this.forgeVersionId));
         var1.setProperty("server", this.server);
         var1.setProperty("manifestLocation", this.manifestLocation);
         var1.setProperty("authApiUrl", this.authApiUrl);
         var1.setProperty("authToken", this.rememberAccount ? this.authToken : "");
         var1.setProperty("authEmail", this.rememberAccount ? this.authEmail : "");
         var1.setProperty("authNickname", this.rememberAccount ? this.authNickname : "");
         var1.setProperty("authRole", this.rememberAccount ? this.authRole : "user");
         var1.setProperty("language", this.language);
         var1.setProperty("closeAction", this.closeAction);
         var1.setProperty("launchAction", this.launchAction);
         var1.setProperty("ramMb", Integer.toString(ProjectBattleAlpha.normalizeRamMb(this.ramMb)));
         var1.setProperty("cleanManagedDirs", Boolean.toString(this.cleanManagedDirs));
         var1.setProperty("autoJoinServer", Boolean.toString(this.autoJoinServer));
         var1.setProperty("rememberNick", Boolean.toString(this.rememberNick));
         var1.setProperty("rememberAccount", Boolean.toString(this.rememberAccount));
         var1.setProperty("discordPresenceEnabled", Boolean.toString(this.discordPresenceEnabled));

         try (BufferedWriter var2 = Files.newBufferedWriter(this.configFile, StandardCharsets.UTF_8)) {
            var1.store(var2, "Project Battle Launcher");
         }
      }
   }

   private static final class DarkButton extends JButton {
      DarkButton(String var1) {
         super(var1);
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.setBorder(new EmptyBorder(8, 13, 8, 13));
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(46, 55, 53), this.getWidth(), this.getHeight(), new Color(22, 28, 28)));
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 14, 14);
         var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 14, 14);
         if (this.getModel().isRollover()) {
            var2.setColor(new Color(255, 255, 255, 18));
            var2.fillRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 12, 12);
         }

         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class DarkComboBox extends JComboBox<String> {
      DarkComboBox(String[] var1) {
         super(var1);
         this.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
               JButton var1x = new JButton("");
               var1x.setPreferredSize(new Dimension(28, 1));
               var1x.setBorder(new EmptyBorder(0, 0, 0, 0));
               var1x.setFocusPainted(false);
               var1x.setContentAreaFilled(false);
               var1x.setOpaque(false);
               return var1x;
            }
         });
         this.setOpaque(false);
         this.setFocusable(false);
         this.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         this.setFont(new Font("SansSerif", 1, 12));
         this.setBorder(new EmptyBorder(0, 0, 0, 0));
         this.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> var1, Object var2, int var3, boolean var4, boolean var5) {
               JLabel var6 = (JLabel)super.getListCellRendererComponent(var1, var2, var3, var4, var5);
               var6.setOpaque(true);
               var6.setBackground(var4 ? ProjectBattleAlpha.Colors.PLAY : ProjectBattleAlpha.Colors.PANEL_LIGHT);
               var6.setForeground(var4 ? ProjectBattleAlpha.Colors.PLAY_TEXT : ProjectBattleAlpha.Colors.TEXT);
               var6.setBorder(new EmptyBorder(7, 10, 7, 10));
               return var6;
            }
         });
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setColor(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 12, 12);
         var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 12, 12);
         String var3 = Objects.toString(this.getSelectedItem(), "");
         FontMetrics var4 = var2.getFontMetrics(this.getFont());
         int var5 = (this.getHeight() - var4.getHeight()) / 2 + var4.getAscent();
         var2.setFont(this.getFont());
         var2.setColor(ProjectBattleAlpha.Colors.TEXT);
         var2.drawString(var3, 12, var5);
         var2.setColor(ProjectBattleAlpha.Colors.MUTED);
         var2.drawString("▾", this.getWidth() - 19, var5);
         var2.dispose();
      }
   }

   private static final class DownloadButton extends JButton {
      DownloadButton(String var1) {
         super(var1);
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.setBorder(new EmptyBorder(8, 52, 8, 12));
         this.setHorizontalAlignment(2);
         this.setFont(new Font("SansSerif", 1, 16));
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(30, 36, 36), this.getWidth(), this.getHeight(), new Color(15, 19, 20)));
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 6, 6);
         if (this.isEnabled() && this.getModel().isRollover()) {
            var2.setColor(new Color(255, 255, 255, 18));
            var2.fillRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 6, 6);
         }

         var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 6, 6);
         var2.setStroke(new BasicStroke(2.2F, 1, 1));
         var2.setColor(this.isEnabled() ? ProjectBattleAlpha.Colors.TEXT : ProjectBattleAlpha.Colors.MUTED);
         byte var3 = 28;
         int var4 = this.getHeight() / 2;
         var2.drawLine(var3, var4 - 12, var3, var4 + 2);
         var2.drawLine(var3 - 6, var4 - 4, var3, var4 + 2);
         var2.drawLine(var3 + 6, var4 - 4, var3, var4 + 2);
         var2.drawLine(var3 - 8, var4 + 8, var3 + 8, var4 + 8);
         var2.drawLine(var3 - 8, var4 + 4, var3 - 8, var4 + 8);
         var2.drawLine(var3 + 8, var4 + 4, var3 + 8, var4 + 8);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class FadingViewPanel extends JPanel {
      FadingViewPanel(LayoutManager var1, BufferedImage var2) {
         super(var1);
         this.setOpaque(false);
      }

      void startTransition(BufferedImage var1) {
         for (Component var5 : this.getComponents()) {
            if (var5.isVisible() && var5 instanceof ProjectBattleAlpha.RevealPanel var6) {
               var6.reveal();
               break;
            }
         }
      }
   }

   private static final class FaqTabButton extends JButton {
      private boolean active;

      FaqTabButton(String var1) {
         super(var1);
         this.setPreferredSize(new Dimension(92, 36));
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setBorder(new EmptyBorder(7, 14, 7, 14));
         this.setFont(new Font("SansSerif", 1, 13));
         this.setCursor(ProjectBattleAlpha.launcherCursor(12));
      }

      boolean isActive() {
         return this.active;
      }

      void setActive(boolean var1) {
         this.active = var1;
         this.setForeground(var1 ? ProjectBattleAlpha.Colors.PLAY_TEXT : ProjectBattleAlpha.Colors.TEXT);
         this.repaint();
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         if (this.active) {
            var2.setColor(ProjectBattleAlpha.Colors.PLAY);
            var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
            var2.setColor(ProjectBattleAlpha.Colors.PLAY_LIGHT);
         } else {
            var2.setColor(this.getModel().isRollover() ? ProjectBattleAlpha.Colors.BUTTON.brighter() : ProjectBattleAlpha.Colors.BUTTON);
            var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
            var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         }

         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class GearButton extends JButton {
      GearButton() {
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setBorder(new EmptyBorder(0, 0, 0, 0));
         this.setCursor(ProjectBattleAlpha.launcherCursor(12));
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         boolean var3 = this.getModel().isRollover();
         var2.setPaint(
            new GradientPaint(0.0F, 0.0F, var3 ? new Color(54, 66, 63) : new Color(42, 52, 50), this.getWidth(), this.getHeight(), new Color(19, 26, 26))
         );
         var2.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 14, 14);
         var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 14, 14);
         int var4 = this.getWidth() / 2;
         int var5 = this.getHeight() / 2;
         var2.translate(var4, var5);
         var2.setColor(var3 ? ProjectBattleAlpha.Colors.PLAY : ProjectBattleAlpha.Colors.TEXT);

         for (int var6 = 0; var6 < 8; var6++) {
            var2.rotate(Math.PI / 4);
            var2.fillRoundRect(-1, -8, 2, 5, 2, 2);
         }

         var2.fillOval(-5, -5, 10, 10);
         var2.setColor(var3 ? new Color(31, 41, 40) : new Color(23, 30, 30));
         var2.fillOval(-2, -2, 4, 4);
         var2.dispose();
      }
   }

   private static final class HeroPanel extends JPanel {
      private final BufferedImage heroLogo;

      HeroPanel(LayoutManager var1) {
         this(var1, null);
      }

      HeroPanel(LayoutManager var1, BufferedImage var2) {
         super(var1);
         this.heroLogo = var2;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int var3 = this.getWidth();
         int var4 = this.getHeight();
         var2.setColor(new Color(225, 210, 180, 18));
         var2.fillRect(0, 0, var3, var4);
         var2.setColor(new Color(0, 0, 0, 110));
         var2.fillRect(0, 0, var3, var4);
         if (this.heroLogo != null) {
            short var5 = 446;
            short var6 = 166;
            byte var7 = 22;
            int var8 = Math.max(0, var4 - 178);
            double var9 = Math.max((double)var5 / this.heroLogo.getWidth(), (double)var6 / this.heroLogo.getHeight());
            int var11 = Math.max(1, (int)Math.round(this.heroLogo.getWidth() * var9));
            int var12 = Math.max(1, (int)Math.round(this.heroLogo.getHeight() * var9));
            int var13 = var7 + (var5 - var11) / 2;
            int var14 = var8 + (var6 - var12) / 2;
            Shape var15 = var2.getClip();
            var2.setClip(var7, var8, var5, var6);
            var2.setComposite(AlphaComposite.SrcOver.derive(0.9F));
            var2.drawImage(this.heroLogo, var13, var14, var11, var12, null);
            var2.setComposite(AlphaComposite.SrcOver);
            var2.setClip(var15);
         }

         var2.setColor(new Color(210, 210, 190, 18));

         for (byte var16 = 0; var16 < var4; var16 += 3) {
            var2.drawLine(0, var16, var3, var16);
         }

         var2.setColor(new Color(210, 210, 190, 10));

         for (byte var17 = 0; var17 < var3; var17 += 37) {
            var2.drawLine(var17, 0, var17, var4);
         }

         var2.dispose();
      }
   }

   private static final class ImagePanel extends JPanel {
      private final BufferedImage image;
      private final boolean contain;
      private final float alpha;
      private final boolean blend;

      ImagePanel(BufferedImage var1, boolean var2, float var3, boolean var4) {
         this.image = var1;
         this.contain = var2;
         this.alpha = var3;
         this.blend = var4;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         if (this.image != null) {
            Graphics2D var2 = (Graphics2D)var1.create();
            var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            if (this.blend) {
               var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(12, 15, 15), this.getWidth(), this.getHeight(), new Color(31, 29, 24)));
               var2.fillRect(0, 0, this.getWidth(), this.getHeight());
            }

            var2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
            double var3 = (double)this.getWidth() / this.image.getWidth();
            double var5 = (double)this.getHeight() / this.image.getHeight();
            double var7 = this.contain ? Math.min(var3, var5) : Math.max(var3, var5);
            int var9 = Math.max(1, (int)Math.round(this.image.getWidth() * var7));
            int var10 = Math.max(1, (int)Math.round(this.image.getHeight() * var7));
            int var11 = (this.getWidth() - var9) / 2;
            int var12 = (this.getHeight() - var10) / 2;
            var2.drawImage(this.image, var11, var12, var9, var10, null);
            var2.setComposite(AlphaComposite.SrcOver);
            var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(0, 0, 0, 45), this.getWidth(), this.getHeight(), new Color(0, 0, 0, 95)));
            var2.fillRect(0, 0, this.getWidth(), this.getHeight());
            var2.setColor(new Color(205, 184, 126, 28));
            var2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
            var2.dispose();
         }
      }
   }

   private static final class Json {
      private final String text;
      private int pos;

      private Json(String var1) {
         this.text = var1;
      }

      static Object parse(String var0) throws IOException {
         ProjectBattleAlpha.Json var1 = new ProjectBattleAlpha.Json(var0);
         Object var2 = var1.value();
         var1.ws();
         if (var1.pos != var1.text.length()) {
            throw new IOException("Unexpected JSON data at " + var1.pos);
         } else {
            return var2;
         }
      }

      private Object value() throws IOException {
         this.ws();
         if (this.pos >= this.text.length()) {
            throw new IOException("Unexpected end of JSON");
         }

         char var1 = this.text.charAt(this.pos);

         return switch (var1) {
            case '"' -> this.string();
            case '[' -> this.array();
            case 'f' -> {
               this.expect("false");
               yield Boolean.FALSE;
            }
            case 'n' -> {
               this.expect("null");
               yield null;
            }
            case 't' -> {
               this.expect("true");
               yield Boolean.TRUE;
            }
            case '{' -> this.object();
            default -> this.number();
         };
      }

      private Map<String, Object> object() throws IOException {
         LinkedHashMap var1 = new LinkedHashMap();
         this.pos++;
         this.ws();
         if (this.peek('}')) {
            this.pos++;
            return var1;
         }

         while (true) {
            this.ws();
            String var2 = this.string();
            this.ws();
            this.need(':');
            var1.put(var2, this.value());
            this.ws();
            if (this.peek('}')) {
               this.pos++;
               return var1;
            }

            this.need(',');
         }
      }

      private List<Object> array() throws IOException {
         ArrayList var1 = new ArrayList();
         this.pos++;
         this.ws();
         if (this.peek(']')) {
            this.pos++;
            return var1;
         }

         while (true) {
            var1.add(this.value());
            this.ws();
            if (this.peek(']')) {
               this.pos++;
               return var1;
            }

            this.need(',');
         }
      }

      private String string() throws IOException {
         this.need('"');
         StringBuilder var1 = new StringBuilder();

         while (this.pos < this.text.length()) {
            char var2 = this.text.charAt(this.pos++);
            if (var2 == '"') {
               return var1.toString();
            }

            if (var2 == '\\') {
               char var3 = this.text.charAt(this.pos++);
               switch (var3) {
                  case '"':
                     var1.append('"');
                     break;
                  case '/':
                     var1.append('/');
                     break;
                  case '\\':
                     var1.append('\\');
                     break;
                  case 'b':
                     var1.append('\b');
                     break;
                  case 'f':
                     var1.append('\f');
                     break;
                  case 'n':
                     var1.append('\n');
                     break;
                  case 'r':
                     var1.append('\r');
                     break;
                  case 't':
                     var1.append('\t');
                     break;
                  case 'u':
                     var1.append((char)Integer.parseInt(this.text.substring(this.pos, this.pos + 4), 16));
                     this.pos += 4;
                     break;
                  default:
                     throw new IOException("Bad escape");
               }
            } else {
               var1.append(var2);
            }
         }

         throw new IOException("Unterminated string");
      }

      private Number number() throws IOException {
         int var1 = this.pos;

         while (this.pos < this.text.length()) {
            char var2 = this.text.charAt(this.pos);
            if ((var2 < '0' || var2 > '9') && var2 != '-' && var2 != '+' && var2 != '.' && var2 != 'e' && var2 != 'E') {
               break;
            }

            this.pos++;
         }

         if (var1 == this.pos) {
            throw new IOException("Expected JSON value at " + this.pos);
         }

         String var3 = this.text.substring(var1, this.pos);
         return !var3.contains(".") && !var3.contains("e") && !var3.contains("E") ? Long.parseLong(var3) : Double.parseDouble(var3);
      }

      private void expect(String var1) throws IOException {
         if (!this.text.startsWith(var1, this.pos)) {
            throw new IOException("Expected " + var1);
         }

         this.pos = this.pos + var1.length();
      }

      private boolean peek(char var1) {
         return this.pos < this.text.length() && this.text.charAt(this.pos) == var1;
      }

      private void need(char var1) throws IOException {
         if (!this.peek(var1)) {
            throw new IOException("Expected " + var1 + " at " + this.pos);
         }

         this.pos++;
      }

      private void ws() {
         while (this.pos < this.text.length() && Character.isWhitespace(this.text.charAt(this.pos))) {
            this.pos++;
         }
      }
   }

   private static final class Library {
      String artifact;
      String artifactUrl;
      String nativeArtifact;
      String nativeArtifactUrl;
      Object rules;
      boolean required = true;

      static ProjectBattleAlpha.Library from(Map<?, ?> var0) {
         ProjectBattleAlpha.Library var1 = new ProjectBattleAlpha.Library();
         var1.rules = var0.get("rules");
         String var2 = ProjectBattleAlpha.str(var0.get("name"));
         String var3 = classifier(var2);
         if (var3 == null || !var3.startsWith("natives-")) {
            if (var0.get("downloads") instanceof Map var4) {
               if (var4.get("artifact") instanceof Map var8) {
                  var1.artifact = ProjectBattleAlpha.str(var8.get("path"));
                  var1.artifactUrl = ProjectBattleAlpha.str(var8.get("url"));
               }

               if (var4.get("classifiers") instanceof Map var9 && windowsNativeDownload(var9, var0) instanceof Map var10) {
                  var1.nativeArtifact = ProjectBattleAlpha.str(var10.get("path"));
                  var1.nativeArtifactUrl = ProjectBattleAlpha.str(var10.get("url"));
               }
            }

            if (var1.artifact == null) {
               var1.artifact = mavenPath(var2);
            }

            return var1.artifact == null && var1.nativeArtifact == null ? null : var1;
         } else {
            if (!isWantedWindowsNative(var3)) {
               return null;
            }

            var1.nativeArtifact = mavenPath(var2);
            var1.required = true;
            return var1.nativeArtifact == null ? null : var1;
         }
      }

      boolean allowedWindows() {
         return rulesAllowWindows(this.rules);
      }

      static boolean rulesAllowWindows(Object var0) {
         List var1;
         if (var0 instanceof List var2) {
            var1 = var2;
         } else {
            if (!(var0 instanceof Map var3)) {
               return true;
            }

            var1 = List.of(var3);
         }

         if (var1.isEmpty()) {
            return true;
         }

         Boolean var12 = null;
         boolean var13 = false;
         boolean var4 = false;
         boolean var5 = false;

         for (Object var7 : var1) {
            if (var7 instanceof Map var8) {
               if (var8.containsKey("features")) {
                  var4 = true;
               } else {
                  boolean var9 = true;
                  if (var8.get("os") instanceof Map var10) {
                     var5 = true;
                     String var14 = ProjectBattleAlpha.str(var10.get("name"));
                     if (var14 != null && !var14.equalsIgnoreCase("windows")) {
                        var9 = false;
                     }
                  }

                  if (var9) {
                     var13 = true;
                     var12 = "allow".equals(ProjectBattleAlpha.str(var8.get("action")));
                  }
               }
            }
         }

         if (var13) {
            return Boolean.TRUE.equals(var12);
         } else {
            return var5 ? false : !var4;
         }
      }

      private static Object windowsNativeDownload(Map<?, ?> var0, Map<?, ?> var1) {
         String var2 = windowsNativeClassifier(var1);

         for (String var4 : List.of(var2, "natives-windows", "natives-windows-64", "natives-windows-arm64", "natives-windows-x86")) {
            if (var4 != null && var0.containsKey(var4)) {
               return var0.get(var4);
            }
         }

         return null;
      }

      private static String windowsNativeClassifier(Map<?, ?> var0) {
         if (var0.get("natives") instanceof Map var1) {
            String var3 = ProjectBattleAlpha.str(var1.get("windows"));
            if (var3 != null && !var3.isBlank()) {
               return var3.replace("${arch}", System.getProperty("os.arch", "").contains("64") ? "64" : "32");
            }
         }

         return "natives-windows";
      }

      private static String mavenPath(String var0) {
         if (var0 == null) {
            return null;
         }

         String[] var1 = var0.split(":");
         if (var1.length < 3) {
            return null;
         }

         String var2 = var1[1] + "-" + var1[2] + (var1.length >= 4 ? "-" + var1[3] : "") + ".jar";
         return var1[0].replace('.', '/') + "/" + var1[1] + "/" + var1[2] + "/" + var2;
      }

      private static String classifier(String var0) {
         if (var0 == null) {
            return null;
         }

         String[] var1 = var0.split(":");
         return var1.length >= 4 ? var1[3] : null;
      }

      private static boolean isWantedWindowsNative(String var0) {
         String var1 = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
         boolean var2 = var1.contains("aarch64") || var1.contains("arm64");
         boolean var3 = var1.contains("64") && !var2;
         if ("natives-windows".equals(var0)) {
            return true;
         } else if ("natives-windows-arm64".equals(var0)) {
            return var2;
         } else {
            return !"natives-windows-x86".equals(var0) ? false : !var3 && !var2;
         }
      }
   }

   private interface LogSink {
      void log(String var1);
   }

   private static final class Manifest {
      String version;
      final List<String> protectedDirs = new ArrayList<>(List.of("mods", "config", "resourcepacks"));
      final List<ProjectBattleAlpha.ManifestFile> files = new ArrayList<>();

      static ProjectBattleAlpha.Manifest load(ProjectBattleAlpha.Config var0) throws IOException {
         String var1 = ProjectBattleAlpha.normalizeManifestLocation(var0.manifestLocation);
         var0.manifestLocation = var1;
         boolean var2 = ProjectBattleAlpha.isRemoteUrl(var1);

         String var3;
         try {
            var3 = ProjectBattleAlpha.readManifestLocation(var1);
         } catch (IOException var12) {
            if (!var2) {
               throw var12;
            }

            if (!ProjectBattleAlpha.MANIFEST_OVERRIDE_URL.isEmpty()) {
               throw var12;
            }

            String var5 = ProjectBattleAlpha.resolveReleaseManifestUrl();
            if (var5.equals(var1)) {
               throw var12;
            }

            var0.manifestLocation = var5;
            var3 = ProjectBattleAlpha.readManifestLocation(var5);
         }

         var3 = ProjectBattleAlpha.stripBom(var3);
         String var4 = var3.stripLeading();
         if (!var4.isEmpty() && var4.charAt(0) == '{') {
            if (!(ProjectBattleAlpha.Json.parse(var3) instanceof Map var14)) {
               throw new IOException("Bad manifest.");
            } else {
               ProjectBattleAlpha.Manifest var15 = new ProjectBattleAlpha.Manifest();
               var15.version = ProjectBattleAlpha.str(var14.get("version"));
               if (var14.get("protectedDirs") instanceof List var7) {
                  var15.protectedDirs.clear();

                  for (Object var9 : var7) {
                     var15.protectedDirs.add(ProjectBattleAlpha.normalPath(ProjectBattleAlpha.str(var9)));
                  }
               }

               Object var16 = var14.get("files");
               if (var16 instanceof List) {
                  for (Object var19 : (List)var16) {
                     if (var19 instanceof Map var10) {
                        ProjectBattleAlpha.ManifestFile var11 = new ProjectBattleAlpha.ManifestFile();
                        var11.path = ProjectBattleAlpha.normalPath(ProjectBattleAlpha.str(var10.get("path")));
                        var11.url = ProjectBattleAlpha.str(var10.get("url"));
                        var11.sha256 = ProjectBattleAlpha.str(var10.get("sha256"));
                        var11.size = ProjectBattleAlpha.asLong(var10.get("size"), -1L);
                        var11.required = ProjectBattleAlpha.asBool(var10.get("required"), true);
                        if (var11.path != null && !var11.path.isBlank()) {
                           var15.files.add(var11);
                        }
                     }
                  }
               }

               return var15;
            }
         } else {
            throw new IOException("Manifest link did not return JSON. Check that the GitHub release is public and the asset is the JSON file: " + var1);
         }
      }
   }

   private static final class ManifestFile {
      String path;
      String url;
      String sha256;
      long size;
      boolean required;
   }

   private static final class NavItemLabel extends JLabel {
      private boolean active;
      private boolean hover;
      private float glow;
      private Timer glowTimer;

      NavItemLabel(String var1) {
         super(var1);
         this.setOpaque(false);
         this.setFont(new Font("SansSerif", 0, 20));
         this.setBorder(new EmptyBorder(0, 0, 9, 0));
         this.setCursor(ProjectBattleAlpha.launcherCursor(12));
         this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent var1) {
               NavItemLabel.this.hover = true;
               NavItemLabel.this.animateGlow(1.0F);
               NavItemLabel.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent var1) {
               NavItemLabel.this.hover = false;
               if (NavItemLabel.this.active) {
                  NavItemLabel.this.animateGlow(1.0F);
               } else {
                  NavItemLabel.this.resetGlow();
               }

               NavItemLabel.this.repaint();
            }
         });
      }

      void setActive(boolean var1) {
         this.active = var1;
         this.setForeground(!var1 && !this.hover ? ProjectBattleAlpha.Colors.MUTED : ProjectBattleAlpha.Colors.TEXT);
         this.setFont(new Font("SansSerif", 0, 20));
         if (!var1 && !this.hover) {
            this.resetGlow();
         } else {
            this.animateGlow(1.0F);
         }

         this.repaint();
      }

      private void resetGlow() {
         if (this.glowTimer != null) {
            this.glowTimer.stop();
         }

         this.glow = 0.0F;
      }

      private void animateGlow(float var1) {
         if (this.glowTimer != null) {
            this.glowTimer.stop();
         }

         this.glowTimer = new Timer(16, null);
         this.glowTimer.addActionListener(var2 -> {
            float var3 = var1 - this.glow;
            if (Math.abs(var3) < 0.035F) {
               this.glow = var1;
               this.glowTimer.stop();
            } else {
               this.glow += var3 * 0.18F;
            }

            this.repaint();
         });
         this.glowTimer.start();
      }

      @Override
      protected void paintComponent(Graphics var1) {
         boolean var2 = this.active || this.hover || this.glow > 0.02F;
         this.setForeground(var2 ? ProjectBattleAlpha.Colors.TEXT : ProjectBattleAlpha.Colors.MUTED);
         Graphics2D var3 = (Graphics2D)var1.create();
         var3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         if (this.glow > 0.02F) {
            FontMetrics var4 = var3.getFontMetrics(this.getFont());
            Insets var5 = this.getInsets();
            int var6 = (this.getHeight() - var5.top - var5.bottom - var4.getHeight()) / 2 + var4.getAscent() + var5.top;
            int var7 = var5.left;
            int var8 = Math.max(1, Math.min(12, Math.round(this.glow * 12.0F)));
            var3.setColor(new Color(238, 232, 196, var8));
            int[][] var9 = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            for (int[] var13 : var9) {
               var3.drawString(this.getText(), var7 + var13[0], var6 + var13[1]);
            }
         }

         var3.dispose();
         super.paintComponent(var1);
         if (this.active || this.hover) {
            Graphics2D var14 = (Graphics2D)var1.create();
            int var15 = this.active ? 180 : 82;
            var14.setColor(new Color(238, 232, 196, var15));
            int var16 = Math.max(22, this.getWidth());
            var14.fillRect(0, this.getHeight() - 2, var16, 2);
            var14.dispose();
         }
      }
   }

   private static final class NewsCardPanel extends JPanel {
      private final boolean large;

      NewsCardPanel(boolean var1) {
         this.large = var1;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setColor(this.large ? new Color(23, 32, 34, 218) : new Color(24, 25, 25, 218));
         var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         var2.setPaint(
            new GradientPaint(0.0F, 0.0F, new Color(170, 210, 210, this.large ? 45 : 22), this.getWidth(), this.getHeight(), new Color(70, 48, 35, 40))
         );
         var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         var2.setColor(ProjectBattleAlpha.Colors.BORDER);
         var2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class PlainImagePanel extends JPanel {
      private final BufferedImage image;
      private final boolean contain;
      private final float alpha;

      PlainImagePanel(BufferedImage var1, boolean var2, float var3) {
         this.image = var1;
         this.contain = var2;
         this.alpha = var3;
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         if (this.image != null) {
            Graphics2D var2 = (Graphics2D)var1.create();
            var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            var2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            var2.setComposite(AlphaComposite.SrcOver.derive(this.alpha));
            double var3 = (double)this.getWidth() / this.image.getWidth();
            double var5 = (double)this.getHeight() / this.image.getHeight();
            double var7 = this.contain ? Math.min(var3, var5) : Math.max(var3, var5);
            int var9 = Math.max(1, (int)Math.round(this.image.getWidth() * var7));
            int var10 = Math.max(1, (int)Math.round(this.image.getHeight() * var7));
            int var11 = (this.getWidth() - var9) / 2;
            int var12 = (this.getHeight() - var10) / 2;
            var2.drawImage(this.image, var11, var12, var9, var10, null);
            var2.dispose();
         }
      }
   }

   private static final class PlayButton extends JButton {
      private final BufferedImage image;

      PlayButton(String var1, BufferedImage var2) {
         super(var1);
         this.image = var2;
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setBorder(new EmptyBorder(8, 16, 8, 16));
         this.setFont(new Font("SansSerif", 1, 28));
         this.setForeground(ProjectBattleAlpha.Colors.PLAY_TEXT);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         byte var3 = 5;
         Float var4 = new Float(0.0F, 0.0F, this.getWidth(), this.getHeight(), var3, var3);
         var2.setClip(var4);
         if (this.image != null) {
            double var5 = Math.max((double)this.getWidth() / this.image.getWidth(), (double)this.getHeight() / this.image.getHeight());
            int var7 = Math.max(1, (int)Math.round(this.image.getWidth() * var5));
            int var8 = Math.max(1, (int)Math.round(this.image.getHeight() * var5));
            int var9 = Math.min(0, (this.getWidth() - var7) / 2);
            int var10 = (this.getHeight() - var8) / 2;
            var2.drawImage(this.image, var9, var10, var7, var8, null);
         } else {
            var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(244, 239, 196), this.getWidth(), this.getHeight(), new Color(193, 171, 119)));
            var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(255, 250, 207, 75), this.getWidth(), this.getHeight(), new Color(120, 84, 48, 78)));
         var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         if (this.getModel().isRollover()) {
            var2.setColor(new Color(255, 255, 255, 28));
            var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         var2.setClip(null);
         var2.setColor(new Color(255, 255, 255, 70));
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, var3, var3);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class RamSelector extends JPanel {
      private final JLabel valueLabel = new JLabel();
      private final JPopupMenu popup = new JPopupMenu();
      private final JList<String> choices = new JList<>();

      RamSelector() {
         super(new BorderLayout(8, 0));
         this.setOpaque(false);
         this.setPreferredSize(new Dimension(170, 34));
         this.setCursor(ProjectBattleAlpha.launcherCursor(12));
         this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(0, 9, 0, 8)));
         this.valueLabel.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.valueLabel.setFont(new Font("SansSerif", 1, 12));
         JLabel var1 = new JLabel("▾");
         var1.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var1.setFont(new Font("SansSerif", 1, 13));
         this.add(this.valueLabel, "Center");
         this.add(var1, "East");
         this.choices.setListData(Arrays.stream(ramChoices()).mapToObj(var0 -> var0 + " MB").toArray(String[]::new));
         this.choices.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.choices.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         this.choices.setFont(new Font("SansSerif", 1, 12));
         this.choices.setSelectionMode(0);
         this.choices.setFixedCellHeight(29);
         this.choices.setBorder(new EmptyBorder(3, 3, 3, 3));
         this.choices.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> var1, Object var2, int var3, boolean var4, boolean var5) {
               JLabel var6 = (JLabel)super.getListCellRendererComponent(var1, var2, var3, var4, var5);
               var6.setFont(new Font("SansSerif", 1, 12));
               var6.setBorder(new EmptyBorder(0, 9, 0, 8));
               var6.setForeground(ProjectBattleAlpha.Colors.TEXT);
               var6.setBackground(var4 ? new Color(95, 91, 76) : ProjectBattleAlpha.Colors.PANEL_LIGHT);
               return var6;
            }
         });
         this.choices.addListSelectionListener(var1x -> {
            if (!var1x.getValueIsAdjusting()) {
               this.updateValue();
            }
         });
         this.choices.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent var1) {
               if (RamSelector.this.choices.getSelectedIndex() >= 0) {
                  RamSelector.this.popup.setVisible(false);
               }
            }
         });
         ProjectBattleAlpha.SoftPanel var2 = new ProjectBattleAlpha.SoftPanel();
         var2.setLayout(new BorderLayout(0, 10));
         var2.setBorder(new EmptyBorder(14, 16, 14, 16));
         JLabel var3 = new JLabel("Память (RAM):");
         var3.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var3.setFont(new Font("SansSerif", 1, 12));
         JLabel var4 = scaleLabel("Выберите объём памяти для Minecraft");
         JScrollPane var5 = new JScrollPane(this.choices);
         var5.setOpaque(true);
         var5.getViewport().setOpaque(true);
         var5.getViewport().setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         var5.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         var5.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
         var5.getVerticalScrollBar().setUnitIncrement(18);
         JPanel var6 = new JPanel();
         var6.setOpaque(false);
         var6.setLayout(new BoxLayout(var6, 1));
         var3.setAlignmentX(0.0F);
         var4.setAlignmentX(0.0F);
         var6.add(var3);
         var6.add(Box.createVerticalStrut(4));
         var6.add(var4);
         var2.add(var6, "North");
         var2.add(var5, "Center");
         this.popup.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
         this.popup.add(var2);
         MouseAdapter var7 = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent var1) {
               RamSelector.this.showPopup();
            }
         };
         this.addMouseListener(var7);
         this.valueLabel.addMouseListener(var7);
         var1.addMouseListener(var7);
         this.updateValue();
      }

      int getValue() {
         Object var1 = this.choices.getSelectedValue();
         return var1 == null ? 2048 : ProjectBattleAlpha.parseInt(var1.toString().replaceAll("[^0-9]", ""), 2048);
      }

      void setValue(int var1) {
         int var2 = ProjectBattleAlpha.normalizeRamMb(var1);
         int var3 = 2048;

         for (int var7 : ramChoices()) {
            if (var7 <= var2) {
               var3 = var7;
            }
         }

         this.choices.setSelectedValue(var3 + " MB", true);
         this.updateValue();
      }

      private void showPopup() {
         this.popup.setPreferredSize(new Dimension(260, Math.min(320, 94 + this.choices.getModel().getSize() * 29)));
         this.popup.show(this, 0, this.getHeight() + 4);
      }

      private void updateValue() {
         this.valueLabel.setText(this.getValue() + " MB");
         this.repaint();
      }

      private static int[] ramChoices() {
         ArrayList var0 = new ArrayList();

         for (short var1 = 2048; var1 <= ProjectBattleAlpha.MAX_RAM_MB; var1 += 2048) {
            var0.add(Integer.valueOf(var1));
         }

         if (var0.isEmpty()) {
            var0.add(2048);
         }

         return var0.stream().mapToInt(var1x -> (Integer)var1x).toArray();
      }

      private static JLabel scaleLabel(String var0) {
         JLabel var1 = new JLabel(var0, 0);
         var1.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var1.setFont(new Font("SansSerif", 0, 11));
         return var1;
      }

      private static JTextField inputDisplay() {
         JTextField var0 = new JTextField();
         var0.setBackground(ProjectBattleAlpha.Colors.PANEL_LIGHT);
         var0.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var0.setCaretColor(ProjectBattleAlpha.Colors.TEXT);
         var0.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER), new EmptyBorder(5, 7, 5, 7)));
         var0.setFont(new Font("SansSerif", 1, 12));
         return var0;
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setPaint(new GradientPaint(0.0F, 0.0F, ProjectBattleAlpha.Colors.PANEL_LIGHT, this.getWidth(), this.getHeight(), new Color(18, 24, 23)));
         var2.fillRect(0, 0, this.getWidth(), this.getHeight());
         if (this.popup.isVisible()) {
            var2.setColor(new Color(215, 205, 170, 42));
            var2.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
         }

         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class RevealPanel extends JPanel {
      private Timer revealTimer;
      private long revealStartedAt;
      private float progress = 1.0F;

      RevealPanel(JComponent var1) {
         super(new BorderLayout());
         this.setOpaque(false);
         this.add(var1, "Center");
      }

      void reveal() {
         if (this.revealTimer != null) {
            this.revealTimer.stop();
         }

         this.progress = 0.0F;
         this.revealStartedAt = System.nanoTime();
         this.revealTimer = new Timer(16, var1 -> {
            float var2 = Math.min(1.0F, (float)((System.nanoTime() - this.revealStartedAt) / 1000000L) / 260.0F);
            this.progress = 1.0F - (float)Math.pow(1.0F - var2, 3.0);
            this.repaint();
            if (var2 >= 1.0F) {
               this.revealTimer.stop();
               this.revealTimer = null;
            }
         });
         this.revealTimer.start();
         this.repaint();
      }

      @Override
      protected void paintChildren(Graphics var1) {
         if (this.revealTimer == null) {
            super.paintChildren(var1);
         } else {
            Graphics2D var2 = (Graphics2D)var1.create();
            var2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0.03F, this.progress)));
            var2.translate(0.0, (1.0F - this.progress) * 12.0F);
            super.paintChildren(var2);
            var2.dispose();
         }
      }
   }

   private static final class Runner {
      private final ProjectBattleAlpha.Config config;
      private final ProjectBattleAlpha.LogSink log;

      Runner(ProjectBattleAlpha.Config var1, ProjectBattleAlpha.LogSink var2) {
         this.config = var1;
         this.log = var2;
      }

      Process launch() throws IOException {
         String var1 = resolveForgeVersionId(this.config.minecraftDir, this.config.forgeVersionId);
         this.config.forgeVersionId = var1;
         ProjectBattleAlpha.Version var2 = ProjectBattleAlpha.Version.load(this.config.minecraftDir, var1);
         Path var3 = ProjectBattleAlpha.prepareNativeDirectory(this.config.instanceDir, var1);
         LinkedHashSet var4 = new LinkedHashSet();
         ArrayList var5 = new ArrayList();

         for (ProjectBattleAlpha.Library var7 : var2.libraries) {
            if (var7.allowedWindows()) {
               if (var7.artifact != null) {
                  Path var8 = this.config.minecraftDir.resolve("libraries").resolve(var7.artifact);
                  this.ensureLibraryFile(var8, var7.artifactUrl, "library");
                  if (Files.exists(var8)) {
                     var4.add(var8.toAbsolutePath().normalize());
                  } else if (var7.required) {
                     this.log.log("Missing library: " + var8);
                  }
               }

               if (var7.nativeArtifact != null) {
                  Path var18 = this.config.minecraftDir.resolve("libraries").resolve(var7.nativeArtifact);
                  this.ensureLibraryFile(var18, var7.nativeArtifactUrl, "native library");
                  if (Files.exists(var18)) {
                     var4.add(var18.toAbsolutePath().normalize());
                     var5.add(var18);
                  } else if (var7.required) {
                     this.log.log("Missing native library: " + var18);
                  }
               }
            }
         }

         boolean var16 = var1.toLowerCase(Locale.ROOT).contains("forge");
         Path var17 = this.config.minecraftDir.resolve("versions").resolve(var2.clientJarId).resolve(var2.clientJarId + ".jar");
         if (var16) {
            this.log.log("Skipping vanilla client jar for Forge launch.");
         } else {
            if (!Files.exists(var17)) {
               throw new FileNotFoundException("Client jar not found: " + var17);
            }

            var4.add(var17.toAbsolutePath().normalize());
         }

         Path var19 = this.config.minecraftDir.resolve("versions").resolve(var1).resolve(var1 + ".jar");
         if (Files.exists(var19)) {
            var4.add(var19.toAbsolutePath().normalize());
         }

         ProjectBattleAlpha.extractNatives(var5, var3);
         this.log.log("Classpath entries: " + var4.size() + ", native jars: " + var5.size());
         String var9 = ProjectBattleAlpha.join(new ArrayList<>(var4));
         String var10 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.config.playerName).getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");
         HashMap var11 = new HashMap();
         var11.put("auth_player_name", this.config.playerName);
         var11.put("version_name", var1);
         var11.put("game_directory", this.config.instanceDir.toString());
         var11.put("assets_root", this.config.minecraftDir.resolve("assets").toString());
         var11.put("assets_index_name", var2.assets);
         var11.put("auth_uuid", var10);
         var11.put("auth_access_token", this.config.authToken != null && !this.config.authToken.isBlank() ? this.config.authToken : "0");
         var11.put("clientid", "0");
         var11.put("auth_xuid", "0");
         var11.put("user_type", "legacy");
         var11.put("version_type", "PROJECT BATTLE");
         var11.put("natives_directory", var3.toString());
         var11.put("launcher_name", "ProjectBattleAlpha");
         var11.put("launcher_version", "0.1.78.13");
         var11.put("classpath", var9);
         var11.put("library_directory", this.config.minecraftDir.resolve("libraries").toString());
         var11.put("classpath_separator", File.pathSeparator);
         var11.put("resolution_width", "1280");
         var11.put("resolution_height", "720");
         ArrayList var12 = new ArrayList();
         Path var13 = findJava(this.config.minecraftDir);
         this.log.log("Java: " + var13);
         var12.add(var13.toString());
         var12.add("-Dprojectbattle.authTicket=" + (this.config.gameTicket == null ? "" : this.config.gameTicket));
         var12.add("-Dprojectbattle.authApi=" + this.config.authApiUrl);
         var12.add("-Xmx" + ProjectBattleAlpha.normalizeRamMb(this.config.ramMb) + "M");
         var12.add("-Xms512M");
         var12.addAll(ProjectBattleAlpha.replace(var2.jvmArgs, var11));
         if ("cpw.mods.bootstraplauncher.BootstrapLauncher".equals(var2.mainClass)) {
            var12.add("--add-opens");
            var12.add("java.base/java.lang.invoke=ALL-UNNAMED");
            var12.add("--add-opens");
            var12.add("java.base/java.util.jar=ALL-UNNAMED");
         }

         var12.add("-Djava.library.path=" + var3.toAbsolutePath().normalize());
         var12.add("-Dorg.lwjgl.librarypath=" + var3.toAbsolutePath().normalize());
         var12.add("-Dorg.lwjgl.system.SharedLibraryExtractPath=" + var3.toAbsolutePath().normalize());
         var12.add(var2.mainClass);
         var12.addAll(ProjectBattleAlpha.sanitizeGameArgs(ProjectBattleAlpha.replace(var2.gameArgs, var11)));
         if ("cpw.mods.bootstraplauncher.BootstrapLauncher".equals(var2.mainClass) && !var12.contains("--launchTarget")) {
            Collections.addAll(
               var12,
               "--launchTarget",
               "forgeclient",
               "--fml.forgeVersion",
               "47.4.28",
               "--fml.mcVersion",
               "1.20.1",
               "--fml.forgeGroup",
               "net.minecraftforge",
               "--fml.mcpVersion",
               "20230612.114412"
            );
         }

         for (int var14 = var12.size() - 1; var14 >= 0; var14--) {
            if ("--assetIndex".equals(var12.get(var14))) {
               var12.remove(var14);
               if (var14 < var12.size() && !((String)var12.get(var14)).startsWith("--")) {
                  var12.remove(var14);
               }
            }
         }

         String var20 = "1.20.1".equals(var2.clientJarId) ? "5" : var2.assets;
         if (var20 != null && !var20.isBlank()) {
            var12.add("--assetIndex");
            var12.add(var20);
         }

         if (this.config.autoJoinServer && this.config.server != null && !this.config.server.isBlank()) {
            ProjectBattleAlpha.Address var15 = ProjectBattleAlpha.Address.parse(this.config.server);
            var12.add("--server");
            var12.add(var15.host);
            var12.add("--port");
            var12.add(Integer.toString(var15.port));
         }

         writeLaunchDebug(this.config.instanceDir, var12);
         this.log.log("Launching " + var1);
         ProcessBuilder var21 = new ProcessBuilder(var12);
         var21.directory(this.config.instanceDir.toFile());
         var21.redirectErrorStream(true);
         return var21.start();
      }

      private void ensureLibraryFile(Path var1, String var2, String var3) throws IOException {
         if (!Files.exists(var1)) {
            if ((var2 == null || var2.isBlank()) && var1.startsWith(this.config.minecraftDir.resolve("libraries"))) {
               Path var4 = this.config.minecraftDir.resolve("libraries").relativize(var1);
               var2 = "https://libraries.minecraft.net/" + var4.toString().replace(File.separatorChar, '/');
            }

            if (var2 != null && !var2.isBlank()) {
               Files.createDirectories(var1.getParent());
               this.log.log("Downloading missing " + var3 + ".");
               ProjectBattleAlpha.download(new URL(var2), var1);
            }
         }
      }

      private static String resolveForgeVersionId(Path var0, String var1) throws IOException {
         Path var2 = var0.resolve("versions");
         String var3 = ProjectBattleAlpha.normalizeForgeVersionId(var1);
         if (var3.isBlank()) {
            var3 = "1.20.1-forge-47.4.28";
         }

         String var4 = var3.toLowerCase(Locale.ROOT);
         if (var4.contains("forge") && !var4.contains("optifine")) {
            Path var5 = var2.resolve(var3).resolve(var3 + ".json");
            if (isUsableForgeProfile(var0, var3)) {
               return var3;
            }
         }

         if (!Files.isDirectory(var2)) {
            throw new FileNotFoundException("Minecraft versions folder not found: " + var2);
         }

         try (Stream<Path> var15 = Files.list(var2)) {
            Optional var6 = var15.filter(var0x -> Files.isDirectory(var0x))
               .filter(
                  var1x -> {
                     String var2x = var1x.getFileName().toString().toLowerCase(Locale.ROOT);
                     return var2x.contains("1.20.1")
                        && var2x.contains("forge")
                        && !var2x.contains("optifine")
                        && isUsableForgeProfile(var0, var1x.getFileName().toString());
                  }
               )
               .sorted(Comparator.comparing(var0x -> var0x.getFileName().toString(), Comparator.reverseOrder()))
               .findFirst();
            if (var6.isPresent()) {
               return ((Path)var6.get()).getFileName().toString();
            }
         }

         try (Stream<Path> var16 = Files.list(var2)) {
            Optional var18 = var16.filter(var0x -> Files.isDirectory(var0x)).filter(var1x -> {
               String var2x = var1x.getFileName().toString().toLowerCase(Locale.ROOT);
               return var2x.contains("1.20.1") && var2x.contains("forge") && isUsableForgeProfile(var0, var1x.getFileName().toString());
            }).sorted(Comparator.comparing(var0x -> var0x.getFileName().toString(), Comparator.reverseOrder())).findFirst();
            if (var18.isPresent()) {
               return ((Path)var18.get()).getFileName().toString();
            }
         }

         StringBuilder var17 = new StringBuilder();

         try (Stream<Path> var19 = Files.list(var2)) {
            var19.filter(var0x -> Files.isDirectory(var0x)).limit(12L).forEach(var1x -> var17.append(var1x.getFileName()).append(", "));
         }

         throw new FileNotFoundException(
            "Clean Forge 1.20.1 is not installed. Install Forge 1.20.1 without OptiFine, or set the exact Forge folder name. Available versions: " + var17
         );
      }

      private static boolean isUsableForgeProfile(Path var0, String var1) {
         try {
            ProjectBattleAlpha.Version var2 = ProjectBattleAlpha.Version.load(var0, var1);
            return "cpw.mods.bootstraplauncher.BootstrapLauncher".equals(var2.mainClass)
               && var2.gameArgs.contains("--launchTarget")
               && var2.gameArgs.contains("forgeclient")
               && var2.jvmArgs.contains("--add-opens");
         } catch (IOException var3) {
            return false;
         }
      }

      private static Path findJava(Path var0) {
         Path var1 = var0.resolve("runtime")
            .resolve("java-runtime-gamma")
            .resolve("windows-x64")
            .resolve("java-runtime-gamma")
            .resolve("bin")
            .resolve("java.exe");
         if (Files.exists(var1)) {
            return var1;
         }

         String var2 = System.getProperty("java.home");
         if (var2 != null && !var2.isBlank()) {
            Path var3 = Paths.get(var2).resolve("bin").resolve("java.exe");
            if (Files.exists(var3)) {
               return var3;
            }
         }

         String var6 = System.getenv("JAVA_HOME");
         if (var6 != null && !var6.isBlank()) {
            Path var4 = Paths.get(var6).resolve("bin").resolve("java.exe");
            if (Files.exists(var4)) {
               return var4;
            }
         }

         Path var7 = newestJava(Paths.get("C:\\Program Files\\Eclipse Adoptium"));
         if (var7 != null) {
            return var7;
         }

         Path var5 = newestJava(Paths.get("C:\\Program Files\\Java"));
         return var5 != null ? var5 : Paths.get("java.exe");
      }

      private static Path newestJava(Path var0) {
         if (!Files.isDirectory(var0)) {
            return null;
         }

         try (Stream<Path> var2 = Files.list(var0)) {
            return var2.filter(var0x -> Files.isDirectory(var0x))
               .sorted(Comparator.comparing(var0x -> var0x.getFileName().toString(), Comparator.reverseOrder()))
               .map(var0x -> var0x.resolve("bin").resolve("java.exe"))
               .filter(var0x -> Files.exists(var0x))
               .findFirst()
               .orElse(null);
         } catch (IOException var7) {
            return null;
         }
      }

      private static void writeLaunchDebug(Path var0, List<String> var1) {
         try {
            Files.createDirectories(var0);
            List var2 = var1.stream().map(var0x -> var0x.startsWith("-Dprojectbattle.authTicket=") ? "-Dprojectbattle.authTicket=<redacted>" : var0x).toList();
            Files.writeString(var0.resolve("last-launch-command.txt"), String.join(System.lineSeparator(), var2), StandardCharsets.UTF_8);
         } catch (IOException var3) {
         }
      }
   }

   private static final class SettingsPanel extends JPanel {
      private final BufferedImage background;

      SettingsPanel(BufferedImage var1) {
         this.background = var1;
         this.setOpaque(true);
         this.setBackground(ProjectBattleAlpha.Colors.PANEL);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         int var3 = this.getWidth();
         int var4 = this.getHeight();
         if (this.background != null) {
            double var5 = Math.max((double)var3 / this.background.getWidth(), (double)var4 / this.background.getHeight());
            int var7 = (int)Math.round(this.background.getWidth() * var5);
            int var8 = (int)Math.round(this.background.getHeight() * var5);
            var2.drawImage(this.background, (var3 - var7) / 2, (var4 - var8) / 2, var7, var8, null);
         }

         var2.setColor(new Color(0, 0, 0, 122));
         var2.fillRect(0, 0, var3, var4);
         var2.setColor(new Color(210, 210, 190, 22));

         for (byte var9 = 0; var9 < var4; var9 += 3) {
            var2.drawLine(0, var9, var3, var9);
         }

         var2.dispose();
      }
   }

   private final class SettingsWorkspace extends JPanel {
      private final CardLayout pages = new CardLayout();
      private final JPanel pageHost = new JPanel(this.pages);
      private final Map<String, JButton> navigation = new LinkedHashMap<>();
      private final Map<String, JComponent> navigationRows = new LinkedHashMap<>();
      private final JDialog dialog;
      private final JTextField search = new JTextField();
      private String currentSection = "";
      private JComboBox<String> language;
      private JComboBox<String> launchAction;
      private ProjectBattleAlpha.RamSelector ram;
      private JCheckBox staySignedIn;
      private JCheckBox discordActivity;
      private JCheckBox autoJoin;
      private JCheckBox cleanDirectories;
      private JLabel savedState;
      private JTextArea filesState;
      private JTextArea downloadState;
      private JTextArea storageState;
      private JTextArea diagnosticsState;
      private JTextArea serverState;
      private volatile String latestDiagnostics = "";

      SettingsWorkspace(JDialog nullx, String nullxx) {
         super(new BorderLayout());
         this.dialog = nullx;
         this.setBackground(ProjectBattleAlpha.Colors.STAGE_CLEAR);
         this.setBorder(BorderFactory.createLineBorder(ProjectBattleAlpha.Colors.BORDER));
         this.add(this.buildHeader(), "North");
         this.add(this.buildSidebar(), "West");
         this.add(this.buildPages(), "Center");
         this.showSection(nullxx == null ? "general" : nullxx);
      }

      private JComponent buildHeader() {
         JPanel var1 = new JPanel(new BorderLayout(16, 0));
         var1.setOpaque(true);
         var1.setBackground(new Color(18, 21, 20));
         var1.setBorder(new EmptyBorder(12, 18, 12, 22));
         JPanel var2 = new JPanel();
         var2.setOpaque(false);
         var2.setLayout(new BoxLayout(var2, 0));
         JButton var3 = this.commandButton("← " + ProjectBattleAlpha.this.tr("Вернуться в лаунчер", "Return to launcher"));
         var3.setPreferredSize(new Dimension(220, 38));
         var3.setMaximumSize(new Dimension(220, 38));
         var3.setAlignmentY(0.5F);
         var3.addActionListener(var1x -> this.dialog.dispose());
         JLabel var4 = new JLabel(ProjectBattleAlpha.this.tr("Настройки лаунчера", "Launcher settings"));
         var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var4.setFont(new Font("SansSerif", 1, 19));
         var4.setAlignmentY(0.5F);
         var2.add(var3);
         var2.add(Box.createHorizontalStrut(98));
         var2.add(var4);
         this.savedState = new JLabel("");
         this.savedState.setForeground(ProjectBattleAlpha.Colors.MUTED);
         this.savedState.setFont(new Font("SansSerif", 0, 12));
         var1.add(var2, "West");
         var1.add(this.savedState, "East");
         return var1;
      }

      private JComponent buildSidebar() {
         JPanel var1 = new JPanel();
         var1.setOpaque(true);
         var1.setBackground(new Color(15, 19, 18));
         var1.setPreferredSize(new Dimension(294, 10));
         var1.setBorder(new EmptyBorder(18, 16, 18, 16));
         var1.setLayout(new BoxLayout(var1, 1));
         this.search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
         this.search.setPreferredSize(new Dimension(256, 40));
         this.search.setBackground(new Color(28, 34, 33));
         this.search.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.search.setCaretColor(ProjectBattleAlpha.Colors.TEXT);
         this.search.setFont(new Font("SansSerif", 0, 13));
         this.search
            .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(76, 88, 84), 1, true), new EmptyBorder(0, 13, 0, 13)));
         this.search.setToolTipText(ProjectBattleAlpha.this.tr("Поиск настроек", "Search settings"));
         this.search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent var1) {
               SettingsWorkspace.this.filterSections();
            }

            @Override
            public void removeUpdate(DocumentEvent var1) {
               SettingsWorkspace.this.filterSections();
            }

            @Override
            public void changedUpdate(DocumentEvent var1) {
               SettingsWorkspace.this.filterSections();
            }
         });
         var1.add(this.search);
         var1.add(Box.createVerticalStrut(22));
         JLabel var2 = new JLabel(ProjectBattleAlpha.this.tr("НАСТРОЙКИ", "SETTINGS"));
         var2.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var2.setFont(new Font("SansSerif", 1, 11));
         var2.setAlignmentX(0.0F);
         var1.add(var2);
         var1.add(Box.createVerticalStrut(8));
         var1.add(this.sectionButton("general", "⚙", ProjectBattleAlpha.this.tr("Общие", "General")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("game", "◆", ProjectBattleAlpha.this.tr("Игра", "Game")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("profile", "●", ProjectBattleAlpha.this.tr("Профиль", "Profile")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("security", "◈", ProjectBattleAlpha.this.tr("Безопасность", "Security")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("files", "▣", ProjectBattleAlpha.this.tr("Файлы и обновления", "Files and updates")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("diagnostics", "✦", ProjectBattleAlpha.this.tr("Диагностика", "Diagnostics")));
         var1.add(Box.createVerticalStrut(7));
         var1.add(this.sectionButton("integrations", "◇", ProjectBattleAlpha.this.tr("Интеграции", "Integrations")));
         var1.add(Box.createVerticalGlue());
         JLabel var3 = new JLabel("Project Battle Alpha 0.1.78.13");
         var3.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var3.setFont(new Font("SansSerif", 0, 11));
         var3.setAlignmentX(0.0F);
         var1.add(var3);
         return var1;
      }

      private JComponent sectionButton(String var1, String var2, String var3) {
         JButton var4 = this.commandButton(var2 + "  " + var3);
         var4.setHorizontalAlignment(2);
         var4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
         var4.setPreferredSize(new Dimension(256, 42));
         var4.addActionListener(var2x -> this.showSection(var1));
         this.navigation.put(var1, var4);
         this.navigationRows.put(var1, var4);
         return var4;
      }

      private JComponent buildPages() {
         this.pageHost.setOpaque(true);
         this.pageHost.setBackground(ProjectBattleAlpha.Colors.STAGE_CLEAR);
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.generalPage())), "general");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.gamePage())), "game");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.profilePage())), "profile");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.securityPage())), "security");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.filesPage())), "files");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.diagnosticsPage())), "diagnostics");
         this.pageHost.add(new ProjectBattleAlpha.RevealPanel(this.scrollPage(this.integrationsPage())), "integrations");
         return this.pageHost;
      }

      private JComponent scrollPage(JPanel var1) {
         JScrollPane var2 = new JScrollPane(var1);
         var2.setBorder(null);
         var2.setOpaque(false);
         var2.getViewport().setOpaque(true);
         var2.getViewport().setBackground(ProjectBattleAlpha.Colors.STAGE_CLEAR);
         var2.getVerticalScrollBar().setUnitIncrement(18);
         return var2;
      }

      private JPanel page(String var1, String var2) {
         JPanel var3 = new JPanel();
         var3.setOpaque(false);
         var3.setBorder(new EmptyBorder(38, 42, 34, 48));
         var3.setLayout(new BoxLayout(var3, 1));
         JLabel var4 = new JLabel(var1);
         var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var4.setFont(new Font("SansSerif", 1, 27));
         var4.setAlignmentX(0.0F);
         JLabel var5 = new JLabel(var2);
         var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var5.setFont(new Font("SansSerif", 0, 13));
         var5.setAlignmentX(0.0F);
         var3.add(var4);
         var3.add(Box.createVerticalStrut(7));
         var3.add(var5);
         var3.add(Box.createVerticalStrut(24));
         return var3;
      }

      private JPanel group(String var1, String var2) {
         ProjectBattleAlpha.SoftPanel var3 = new ProjectBattleAlpha.SoftPanel();
         var3.setLayout(new BoxLayout(var3, 1));
         var3.setBorder(new EmptyBorder(19, 20, 19, 20));
         var3.setAlignmentX(0.0F);
         var3.setMinimumSize(new Dimension(0, 0));
         var3.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
         JLabel var4 = new JLabel(var1);
         var4.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var4.setFont(new Font("SansSerif", 1, 15));
         var4.setAlignmentX(0.0F);
         var4.setHorizontalAlignment(2);
         var4.setMaximumSize(new Dimension(Integer.MAX_VALUE, var4.getPreferredSize().height));
         var3.add(var4);
         if (var2 != null && !var2.isBlank()) {
            var3.add(Box.createVerticalStrut(4));
            JLabel var5 = new JLabel(var2);
            var5.setForeground(ProjectBattleAlpha.Colors.MUTED);
            var5.setFont(new Font("SansSerif", 0, 12));
            var5.setAlignmentX(0.0F);
            var5.setHorizontalAlignment(2);
            var5.setMaximumSize(new Dimension(Integer.MAX_VALUE, var5.getPreferredSize().height));
            var3.add(var5);
         }

         var3.add(Box.createVerticalStrut(16));
         return var3;
      }

      private JPanel generalPage() {
         JPanel var1 = this.page(
            ProjectBattleAlpha.this.tr("Общие", "General"), ProjectBattleAlpha.this.tr("Основные параметры работы лаунчера", "Core launcher behaviour")
         );
         JPanel var2 = this.group(
            ProjectBattleAlpha.this.tr("Работа лаунчера", "Launcher behaviour"),
            ProjectBattleAlpha.this.tr("Настройте язык, запуск игры и обслуживание файлов.", "Set language, launch behaviour and file maintenance.")
         );
         this.language = ProjectBattleAlpha.this.settingsCombo(new String[]{"Русский", "English"});
         this.language.setSelectedIndex(ProjectBattleAlpha.this.english() ? 1 : 0);
         this.launchAction = ProjectBattleAlpha.this.settingsCombo(
            new String[]{
               ProjectBattleAlpha.this.tr("Оставить лаунчер включённым", "Keep launcher open"), ProjectBattleAlpha.this.tr("Закрыть лаунчер", "Close launcher")
            }
         );
         this.launchAction.setSelectedIndex("exit".equals(ProjectBattleAlpha.this.config.launchAction) ? 1 : 0);
         var2.add(ProjectBattleAlpha.this.settingsRow(ProjectBattleAlpha.this.tr("Язык лаунчера", "Launcher language"), this.language, 290));
         var2.add(Box.createVerticalStrut(14));
         var2.add(ProjectBattleAlpha.this.settingsRow(ProjectBattleAlpha.this.tr("При запуске игры", "When launching game"), this.launchAction, 360));
         var1.add(var2);
         var1.add(Box.createVerticalStrut(18));
         var1.add(this.saveButton());
         var1.add(Box.createVerticalStrut(18));
         JButton close = this.commandButton(ProjectBattleAlpha.this.tr("Закрыть лаунчер", "Close launcher"));
         close.setForeground(new Color(239, 146, 146));
         close.setAlignmentX(0.0F);
         close.addActionListener(event -> ProjectBattleAlpha.this.closeLauncher());
         var1.add(close);
         return var1;
      }

      private void refreshServerStatus() {
         this.serverState.setText(ProjectBattleAlpha.this.tr("Проверяем сервер...", "Checking server..."));
         ProjectBattleAlpha.this.profileWorker.submit(() -> {
            String status = ProjectBattleAlpha.this.serverStatusText();
            SwingUtilities.invokeLater(() -> this.serverState.setText(status));
         });
      }

      private JPanel gamePage() {
         JPanel var1 = this.page(
            ProjectBattleAlpha.this.tr("Игра", "Game"),
            ProjectBattleAlpha.this.tr("Параметры запуска Minecraft и подключения", "Minecraft launch and connection settings")
         );
         JPanel var2 = this.group(
            ProjectBattleAlpha.this.tr("Производительность", "Performance"),
            ProjectBattleAlpha.this.tr("Выберите объём памяти для игры.", "Choose memory allocated to the game.")
         );
         this.ram = new ProjectBattleAlpha.RamSelector();
         this.ram.setValue(ProjectBattleAlpha.normalizeRamMb(ProjectBattleAlpha.this.config.ramMb));
         this.autoJoin = ProjectBattleAlpha.this.check(ProjectBattleAlpha.this.tr("Автоматически подключаться к серверу", "Automatically join server"));
         this.autoJoin.setSelected(ProjectBattleAlpha.this.config.autoJoinServer);
         var2.add(ProjectBattleAlpha.this.settingsRow(ProjectBattleAlpha.this.tr("Память (RAM)", "Memory (RAM)"), this.ram, 360));
         var2.add(Box.createVerticalStrut(14));
         this.autoJoin.setAlignmentX(0.0F);
         var2.add(this.autoJoin);
         var1.add(var2);
         var1.add(Box.createVerticalStrut(18));
         JPanel server = this.group(ProjectBattleAlpha.this.tr("Сервер ETS", "ETS server"),
               ProjectBattleAlpha.this.tr("Статус exaroton и доступность подключения.", "Exaroton status and connection availability."));
         this.serverState = this.statusArea(ProjectBattleAlpha.this.tr("Нажмите «Обновить статус».", "Click Refresh status."));
         this.serverState.setRows(4);
         server.add(this.serverState);
         JButton refresh = this.commandButton(ProjectBattleAlpha.this.tr("Обновить статус", "Refresh status"));
         refresh.setAlignmentX(0.0F);
         refresh.addActionListener(event -> this.refreshServerStatus());
         server.add(refresh);
         var1.add(server);
         var1.add(Box.createVerticalStrut(18));
         var1.add(this.saveButton());
         return var1;
      }

      private JPanel profilePage() {
         JPanel var1 = this.page(
            ProjectBattleAlpha.this.tr("Профиль", "Profile"),
            ProjectBattleAlpha.this.tr("Аккаунт Project Battle и поддержка", "Project Battle account and support")
         );
         boolean var2 = ProjectBattleAlpha.this.config.authToken != null && !ProjectBattleAlpha.this.config.authToken.isBlank();
         JPanel var3 = this.group(
            ProjectBattleAlpha.this.tr("Аккаунт Project Battle", "Project Battle account"),
            var2
               ? ProjectBattleAlpha.this.tr("Данные вашей учётной записи.", "Your account details.")
               : ProjectBattleAlpha.this.tr("Войдите, чтобы использовать аккаунт Project Battle.", "Sign in to use your Project Battle account.")
         );
         if (var2) {
            String var4 = ProjectBattleAlpha.this.config.authNickname != null && !ProjectBattleAlpha.this.config.authNickname.isBlank()
               ? ProjectBattleAlpha.this.config.authNickname
               : ProjectBattleAlpha.this.config.playerName;
            var3.add(this.infoLine(ProjectBattleAlpha.this.tr("Логин", "Login"), var4));
            var3.add(Box.createVerticalStrut(8));
            var3.add(this.infoLine("Email", ProjectBattleAlpha.this.maskEmail(ProjectBattleAlpha.this.config.authEmail)));
            var3.add(Box.createVerticalStrut(16));
            JPanel var5 = this.controls();
            JButton var6 = this.commandButton(ProjectBattleAlpha.this.tr("Скопировать логин", "Copy login"));
            var6.addActionListener(
               var3x -> this.copyText(
                  var4, var6, ProjectBattleAlpha.this.tr("Логин скопирован", "Login copied"), ProjectBattleAlpha.this.tr("Скопировать логин", "Copy login")
               )
            );
            JButton var7 = this.commandButton(ProjectBattleAlpha.this.tr("Изменить пароль", "Change password"));
            var7.addActionListener(var1x -> ProjectBattleAlpha.this.openForgotPasswordDialog());
            JButton var8 = this.commandButton(ProjectBattleAlpha.this.tr("Выйти из аккаунта", "Sign out"));
            var8.setForeground(new Color(239, 146, 146));
            var8.addActionListener(var1x -> this.signOut());
            var5.add(var6);
            var5.add(var7);
            var5.add(var8);
            var3.add(var5);
         } else {
            JPanel var10 = this.controls();
            JButton var12 = this.commandButton(ProjectBattleAlpha.this.tr("Войти", "Sign in"));
            var12.addActionListener(var1x -> ProjectBattleAlpha.this.openLoginDialog());
            JButton var14 = this.commandButton(ProjectBattleAlpha.this.tr("Создать аккаунт", "Create account"));
            var14.addActionListener(var1x -> ProjectBattleAlpha.this.openRegisterDialog());
            var10.add(var12);
            var10.add(var14);
            var3.add(var10);
         }

         var1.add(var3);
         var1.add(Box.createVerticalStrut(18));
         JPanel var16 = this.group(
            "Project Battle Support",
            ProjectBattleAlpha.this.tr("Свяжитесь с нами по вопросам лаунчера и аккаунта.", "Contact us about the launcher and account.")
         );
         var16.add(this.infoLine("Email", "ourffers@gmail.com"));
         var16.add(Box.createVerticalStrut(16));
         JPanel var17 = this.controls();
         JButton var18 = this.commandButton(ProjectBattleAlpha.this.tr("Скопировать почту", "Copy email"));
         var18.addActionListener(
            var2x -> this.copyText(
               "ourffers@gmail.com",
               var18,
               ProjectBattleAlpha.this.tr("Почта скопирована", "Email copied"),
               ProjectBattleAlpha.this.tr("Скопировать почту", "Copy email")
            )
         );
         var17.add(var18);
         var16.add(var17);
         var16.add(Box.createVerticalStrut(12));
         JButton var9 = this.commandButton(ProjectBattleAlpha.this.tr("Открыть Telegram-поддержку", "Open Telegram support"));
         var9.setAlignmentX(0.0F);
         var9.addActionListener(var1x -> ProjectBattleAlpha.this.openTelegramSupport(this.dialog));
         var16.add(var9);
         var1.add(var16);
         return var1;
      }

      private JPanel securityPage() {
         JPanel var1 = this.page(
            ProjectBattleAlpha.this.tr("Безопасность", "Security"),
            ProjectBattleAlpha.this.tr("Вход и управление аккаунтом", "Sign-in and account access")
         );
         JPanel var2 = this.group(
            ProjectBattleAlpha.this.tr("Сохранение входа", "Sign-in storage"),
            ProjectBattleAlpha.this.tr("Управляйте сохранением учётной записи на этом компьютере.", "Control account storage on this computer.")
         );
         this.staySignedIn = ProjectBattleAlpha.this.check(ProjectBattleAlpha.this.tr("Оставаться в системе", "Stay signed in"));
         this.staySignedIn.setSelected(ProjectBattleAlpha.this.config.rememberAccount);
         this.staySignedIn.setAlignmentX(0.0F);
         var2.add(this.staySignedIn);
         var1.add(var2);
         if ("admin".equalsIgnoreCase(ProjectBattleAlpha.this.config.authRole)) {
            var1.add(Box.createVerticalStrut(18));
            JPanel admin = this.group(ProjectBattleAlpha.this.tr("Администрирование", "Administration"), null);
            JButton var4 = this.commandButton(ProjectBattleAlpha.this.tr("Панель администратора", "Administrator panel"));
            var4.addActionListener(var1x -> ProjectBattleAlpha.this.openAdminPanel(this.dialog));
            var4.setAlignmentX(0.0F);
            admin.add(var4);
            var1.add(admin);
         }
         var1.add(Box.createVerticalStrut(18));
         var1.add(this.saveButton());
         return var1;
      }

      private JTextArea statusArea(String initial) {
         JTextArea area = new JTextArea(initial, 5, 48);
         area.setEditable(false);
         area.setOpaque(false);
         area.setLineWrap(true);
         area.setWrapStyleWord(true);
         area.setForeground(ProjectBattleAlpha.Colors.TEXT);
         area.setFont(new Font("SansSerif", Font.PLAIN, 12));
         area.setAlignmentX(0.0F);
         return area;
      }

      private JPanel filesPage() {
         JPanel page = this.page(ProjectBattleAlpha.this.tr("Файлы и обновления", "Files and updates"),
               ProjectBattleAlpha.this.tr("Проверка и восстановление игровой сборки", "Inspect and repair the game pack"));
         JPanel pack = this.group(ProjectBattleAlpha.this.tr("Состояние сборки", "Pack status"),
               ProjectBattleAlpha.this.tr("Размер и SHA-256 сравниваются с текущим манифестом.", "Size and SHA-256 are compared with the current manifest."));
         this.filesState = this.statusArea(ProjectBattleAlpha.this.tr("Файлы ещё не проверены.", "Files have not been checked yet."));
         pack.add(this.filesState);
         pack.add(Box.createVerticalStrut(12));
         JPanel actions = this.controls();
         JButton check = this.commandButton(ProjectBattleAlpha.this.tr("Проверить файлы", "Check files"));
         JButton repair = this.commandButton(ProjectBattleAlpha.this.tr("Восстановить", "Repair"));
         JButton extraMods = this.commandButton(ProjectBattleAlpha.this.tr("Лишние моды", "Extra mods"));
         check.addActionListener(event -> this.checkFiles());
         repair.addActionListener(event -> this.repairFiles());
         extraMods.addActionListener(event -> this.reviewExtraMods());
         actions.add(check);
         actions.add(repair);
         actions.add(extraMods);
         pack.add(actions);
         page.add(pack);
         page.add(Box.createVerticalStrut(18));

         JPanel transfers = this.group(ProjectBattleAlpha.this.tr("Менеджер загрузок", "Download manager"),
               ProjectBattleAlpha.this.tr("Файлы сборки, скорость и повтор после ошибки.", "Pack files, speed and retry after an error."));
         this.downloadState = this.statusArea(ProjectBattleAlpha.this.packDownloads.snapshot());
         this.downloadState.setRows(7);
         transfers.add(this.downloadState);
         ProjectBattleAlpha.this.packDownloads.setListener(status -> SwingUtilities.invokeLater(() -> this.downloadState.setText(status)));
         transfers.add(Box.createVerticalStrut(12));
         JPanel transferActions = this.controls();
         JButton pause = this.commandButton(ProjectBattleAlpha.this.tr("Пауза", "Pause"));
         JButton resume = this.commandButton(ProjectBattleAlpha.this.tr("Продолжить", "Resume"));
         JButton retry = this.commandButton(ProjectBattleAlpha.this.tr("Повторить", "Retry"));
         pause.addActionListener(event -> ProjectBattleAlpha.this.packDownloads.pause());
         resume.addActionListener(event -> ProjectBattleAlpha.this.packDownloads.resume());
         retry.addActionListener(event -> this.repairFiles());
         transferActions.add(pause);
         transferActions.add(resume);
         transferActions.add(retry);
         transfers.add(transferActions);
         page.add(transfers);
         page.add(Box.createVerticalStrut(18));

         JPanel storage = this.group(ProjectBattleAlpha.this.tr("Хранилище", "Storage"),
               ProjectBattleAlpha.this.tr("Размеры категорий и безопасная очистка временных файлов.", "Category sizes and safe temporary-file cleanup."));
         this.storageState = this.statusArea(ProjectBattleAlpha.this.tr("Размеры ещё не подсчитаны.", "Sizes have not been calculated yet."));
         storage.add(this.storageState);
         storage.add(Box.createVerticalStrut(12));
         JPanel storageActions = this.controls();
         JButton size = this.commandButton(ProjectBattleAlpha.this.tr("Подсчитать размер", "Calculate size"));
         JButton clean = this.commandButton(ProjectBattleAlpha.this.tr("Очистить временные файлы", "Clean temporary files"));
         JButton move = this.commandButton(ProjectBattleAlpha.this.tr("Перенести игру", "Move game"));
         size.addActionListener(event -> this.calculateStorage());
         clean.addActionListener(event -> this.cleanupTemporary());
         move.addActionListener(event -> this.migrateGame());
         storageActions.add(size);
         storageActions.add(clean);
         storageActions.add(move);
         storage.add(storageActions);
         page.add(storage);
         return page;
      }

      private JPanel diagnosticsPage() {
         JPanel page = this.page(ProjectBattleAlpha.this.tr("Диагностика", "Diagnostics"),
               ProjectBattleAlpha.this.tr("Проверка запуска и отчёт для поддержки", "Launch checks and support report"));
         JPanel group = this.group(ProjectBattleAlpha.this.tr("Проверка системы", "System checks"),
               ProjectBattleAlpha.this.tr("Java, Forge, библиотеки, сборка, сеть и диск.", "Java, Forge, libraries, pack, network and disk."));
         this.diagnosticsState = this.statusArea(ProjectBattleAlpha.this.tr("Проверка ещё не запускалась.", "Checks have not been run yet."));
         this.diagnosticsState.setRows(11);
         group.add(this.diagnosticsState);
         group.add(Box.createVerticalStrut(12));
         JPanel actions = this.controls();
         JButton run = this.commandButton(ProjectBattleAlpha.this.tr("Проверить систему", "Run checks"));
         JButton report = this.commandButton(ProjectBattleAlpha.this.tr("Подготовить отчёт для поддержки", "Create support report"));
         JButton copy = this.commandButton(ProjectBattleAlpha.this.tr("Скопировать логи", "Copy logs"));
         run.addActionListener(event -> this.runDiagnostics());
         report.addActionListener(event -> this.createSupportReport());
         copy.addActionListener(event -> this.copyLogs(copy));
         actions.add(run);
         actions.add(report);
         actions.add(copy);
         group.add(actions);
         page.add(group);
         return page;
      }

      private JPanel integrationsPage() {
         JPanel page = this.page(ProjectBattleAlpha.this.tr("Интеграции", "Integrations"),
               ProjectBattleAlpha.this.tr("Discord и Telegram", "Discord and Telegram"));
         JPanel discord = this.group("Discord Rich Presence",
               ProjectBattleAlpha.this.tr("Показывать активность Project Battle в Discord.", "Show Project Battle activity in Discord."));
         this.discordActivity = ProjectBattleAlpha.this.check(ProjectBattleAlpha.this.tr("Показывать активность", "Show activity"));
         this.discordActivity.setSelected(ProjectBattleAlpha.this.config.discordPresenceEnabled);
         this.discordActivity.setAlignmentX(0.0F);
         discord.add(this.discordActivity);
         page.add(discord);
         page.add(Box.createVerticalStrut(18));

         boolean signedIn = ProjectBattleAlpha.this.config.authToken != null && !ProjectBattleAlpha.this.config.authToken.isBlank();
         JPanel telegram = this.group("Telegram", signedIn
               ? ProjectBattleAlpha.this.tr("Состояние привязки к аккаунту Project Battle.", "Project Battle account link status.")
               : ProjectBattleAlpha.this.tr("Войдите в аккаунт, чтобы привязать Telegram.", "Sign in to link Telegram."));
         JLabel status = new JLabel(signedIn ? ProjectBattleAlpha.this.tr("Проверяем статус...", "Checking status...")
               : ProjectBattleAlpha.this.tr("Требуется вход в аккаунт", "Account sign-in required"));
         status.setForeground(ProjectBattleAlpha.Colors.MUTED);
         status.setFont(new Font("SansSerif", Font.BOLD, 12));
         status.setAlignmentX(0.0F);
         telegram.add(status);
         if (signedIn) {
            telegram.add(Box.createVerticalStrut(14));
            JButton link = this.commandButton(ProjectBattleAlpha.this.tr("Привязать Telegram", "Link Telegram"));
            link.setAlignmentX(0.0F);
            link.setEnabled(false);
            link.addActionListener(event -> ProjectBattleAlpha.this.requestTelegramLink(this.dialog, link, status));
            telegram.add(link);
            telegram.add(Box.createVerticalStrut(10));
            JPanel telegramActions = this.controls();
            JButton unlink = this.commandButton(ProjectBattleAlpha.this.tr("Отключить", "Disconnect"));
            JButton relink = this.commandButton(ProjectBattleAlpha.this.tr("Перепривязать", "Relink"));
            unlink.setVisible(false);
            relink.setVisible(false);
            unlink.addActionListener(event -> ProjectBattleAlpha.this.unlinkTelegramLink(this.dialog, status, link, unlink, relink, false));
            relink.addActionListener(event -> ProjectBattleAlpha.this.unlinkTelegramLink(this.dialog, status, link, unlink, relink, true));
            telegramActions.add(unlink);
            telegramActions.add(relink);
            telegram.add(telegramActions);
            ProjectBattleAlpha.this.refreshTelegramStatus(status, link, unlink, relink);
         }
         page.add(telegram);
         page.add(Box.createVerticalStrut(18));
         page.add(this.saveButton());
         return page;
      }

      private List<PackMaintenance.FileSpec> fileSpecs(ProjectBattleAlpha.Manifest manifest) {
         List<PackMaintenance.FileSpec> files = new ArrayList<>();
         for (ProjectBattleAlpha.ManifestFile file : manifest.files) {
            files.add(new PackMaintenance.FileSpec(file.path, file.url, file.sha256, file.size, file.required));
         }
         return files;
      }

      private void runMaintenance(Runnable task) {
         if (ProjectBattleAlpha.this.updateRunning || ProjectBattleAlpha.this.packDownloads.isActive()) {
            JOptionPane.showMessageDialog(this.dialog, ProjectBattleAlpha.this.tr("Дождитесь завершения загрузки.", "Wait for the download to finish."));
            return;
         }
         if (!ProjectBattleAlpha.this.maintenanceRunning.compareAndSet(false, true)) {
            JOptionPane.showMessageDialog(this.dialog, ProjectBattleAlpha.this.tr("Другая операция с файлами ещё выполняется.", "Another file operation is running."));
            return;
         }
         ProjectBattleAlpha.this.worker.submit(() -> {
            try { task.run(); }
            finally { ProjectBattleAlpha.this.maintenanceRunning.set(false); }
         });
      }

      private void checkFiles() {
         this.filesState.setText(ProjectBattleAlpha.this.tr("Проверяем...", "Checking..."));
         this.runMaintenance(() -> {
            try {
               ProjectBattleAlpha.Manifest manifest = ProjectBattleAlpha.Manifest.load(ProjectBattleAlpha.this.config);
               List<PackMaintenance.FileIssue> issues = PackMaintenance.inspect(ProjectBattleAlpha.this.config.instanceDir, this.fileSpecs(manifest));
               StringBuilder result = new StringBuilder(ProjectBattleAlpha.this.tr("Версия: ", "Version: "))
                     .append(manifest.version).append("\n").append(ProjectBattleAlpha.this.tr("Проверено файлов: ", "Files checked: "))
                     .append(manifest.files.size()).append("\n").append(ProjectBattleAlpha.this.tr("Нуждаются в восстановлении: ", "Need repair: "))
                     .append(issues.size());
               for (PackMaintenance.FileIssue issue : issues.stream().limit(15).toList()) result.append("\n• ").append(issue.file().path()).append(" (").append(issue.reason()).append(")");
               if (issues.size() > 15) result.append("\n… +").append(issues.size() - 15);
               SwingUtilities.invokeLater(() -> this.filesState.setText(result.toString()));
            } catch (Exception ex) { this.showMaintenanceError(this.filesState, ex); }
         });
      }

      private void repairFiles() {
         this.filesState.setText(ProjectBattleAlpha.this.tr("Ищем повреждённые файлы...", "Finding damaged files..."));
         this.runMaintenance(() -> {
            try {
               if (ProjectBattleAlpha.this.minecraftRunning || ProjectBattleAlpha.this.isProjectBattleMinecraftProcessRunning())
                  throw new IOException(ProjectBattleAlpha.this.tr("Сначала закройте Minecraft.", "Close Minecraft first."));
               ProjectBattleAlpha.Manifest manifest = ProjectBattleAlpha.Manifest.load(ProjectBattleAlpha.this.config);
               List<PackMaintenance.FileIssue> issues = PackMaintenance.inspect(ProjectBattleAlpha.this.config.instanceDir, this.fileSpecs(manifest));
               if (issues.isEmpty()) {
                  SwingUtilities.invokeLater(() -> this.filesState.setText(ProjectBattleAlpha.this.tr("Все файлы сборки целы.", "All pack files are intact.")));
                  return;
               }
               StringBuilder preview = new StringBuilder(ProjectBattleAlpha.this.tr("Восстановить файлов: ", "Repair files: ")).append(issues.size());
               for (PackMaintenance.FileIssue issue : issues.stream().limit(12).toList()) preview.append("\n• ").append(issue.file().path());
               int answer = onUiConfirm(preview.toString(), ProjectBattleAlpha.this.tr("Подтвердить восстановление", "Confirm repair"));
               if (answer != JOptionPane.YES_OPTION) return;
               SwingUtilities.invokeLater(() -> this.filesState.setText(ProjectBattleAlpha.this.tr("Скачиваем только отсутствующие и повреждённые файлы...", "Downloading only missing and damaged files...")));
               ProjectBattleAlpha.this.sync(manifest);
               SwingUtilities.invokeLater(() -> this.filesState.setText(ProjectBattleAlpha.this.tr("Восстановление завершено.", "Repair complete.")));
            } catch (Exception ex) { this.showMaintenanceError(this.filesState, ex); }
         });
      }

      private void reviewExtraMods() {
         this.runMaintenance(() -> {
            try {
               if (ProjectBattleAlpha.this.minecraftRunning || ProjectBattleAlpha.this.isProjectBattleMinecraftProcessRunning())
                  throw new IOException(ProjectBattleAlpha.this.tr("Сначала закройте Minecraft.", "Close Minecraft first."));
               ProjectBattleAlpha.Manifest manifest = ProjectBattleAlpha.Manifest.load(ProjectBattleAlpha.this.config);
               List<Path> extras = PackMaintenance.unmanagedMods(ProjectBattleAlpha.this.config.instanceDir, this.fileSpecs(manifest));
               if (extras.isEmpty()) {
                  SwingUtilities.invokeLater(() -> this.filesState.setText(ProjectBattleAlpha.this.tr("Лишних модов нет.", "No extra mods found.")));
                  return;
               }
               StringBuilder preview = new StringBuilder(ProjectBattleAlpha.this.tr("Эти моды будут перемещены в резервную папку:\n", "These mods will be moved to a backup folder:\n"));
               for (Path file : extras.stream().limit(20).toList()) preview.append("\n• ").append(file.getFileName());
               if (extras.size() > 20) preview.append("\n… +").append(extras.size() - 20);
               if (onUiConfirm(preview.toString(), ProjectBattleAlpha.this.tr("Подтвердить перенос", "Confirm move")) != JOptionPane.YES_OPTION) return;
               Path backup = PackMaintenance.quarantineMods(ProjectBattleAlpha.this.config.instanceDir, ProjectBattleAlpha.this.config.configDir, extras);
               SwingUtilities.invokeLater(() -> this.filesState.setText(ProjectBattleAlpha.this.tr("Моды сохранены в: ", "Mods backed up to: ") + backup));
            } catch (Exception ex) { this.showMaintenanceError(this.filesState, ex); }
         });
      }

      private int onUiConfirm(String message, String title) throws Exception {
         final int[] answer = new int[1];
         SwingUtilities.invokeAndWait(() -> answer[0] = JOptionPane.showConfirmDialog(this.dialog, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE));
         return answer[0];
      }

      private void calculateStorage() {
         this.storageState.setText(ProjectBattleAlpha.this.tr("Подсчитываем...", "Calculating..."));
         this.runMaintenance(() -> {
            try {
               PackMaintenance.Storage size = PackMaintenance.storage(ProjectBattleAlpha.this.config.instanceDir, ProjectBattleAlpha.this.config.minecraftDir);
               String result = ProjectBattleAlpha.this.tr("Моды: ", "Mods: ") + mb(size.mods()) + "\n"
                     + ProjectBattleAlpha.this.tr("Библиотеки: ", "Libraries: ") + mb(size.libraries()) + "\n"
                     + "Java: " + mb(size.java()) + "\n"
                     + ProjectBattleAlpha.this.tr("Кеш и ресурсы: ", "Cache and assets: ") + mb(size.cache()) + "\n"
                     + ProjectBattleAlpha.this.tr("Логи: ", "Logs: ") + mb(size.logs());
               SwingUtilities.invokeLater(() -> this.storageState.setText(result));
            } catch (Exception ex) { this.showMaintenanceError(this.storageState, ex); }
         });
      }

      private String mb(long bytes) { return String.format(Locale.ROOT, "%.1f MB", bytes / 1048576.0); }

      private void cleanupTemporary() {
         this.runMaintenance(() -> {
            try {
               if (ProjectBattleAlpha.this.packDownloads.isActive() || ProjectBattleAlpha.this.updateRunning)
                  throw new IOException(ProjectBattleAlpha.this.tr("Дождитесь завершения загрузки.", "Wait for the download to finish."));
               Path root = ProjectBattleAlpha.this.config.instanceDir;
               List<Path> files = PackMaintenance.temporaryFiles(root);
               if (files.isEmpty()) {
                  SwingUtilities.invokeLater(() -> this.storageState.setText(ProjectBattleAlpha.this.tr("Временных файлов для очистки нет.", "No temporary files to clean.")));
                  return;
               }
               StringBuilder preview = new StringBuilder(ProjectBattleAlpha.this.tr("Будут удалены только временные файлы загрузки:\n", "Only temporary download files will be deleted:\n"));
               for (Path file : files.stream().limit(20).toList()) preview.append("\n• ").append(root.relativize(file));
               if (files.size() > 20) preview.append("\n… +").append(files.size() - 20);
               if (onUiConfirm(preview.toString(), ProjectBattleAlpha.this.tr("Подтвердить очистку", "Confirm cleanup")) != JOptionPane.YES_OPTION) return;
               PackMaintenance.deleteTemporary(root, files);
               SwingUtilities.invokeLater(() -> this.storageState.setText(ProjectBattleAlpha.this.tr("Удалено временных файлов: ", "Temporary files removed: ") + files.size()));
            } catch (Exception ex) { this.showMaintenanceError(this.storageState, ex); }
         });
      }

      private void migrateGame() {
         JFileChooser chooser = new JFileChooser();
         chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
         chooser.setDialogTitle(ProjectBattleAlpha.this.tr("Выберите новую папку игры", "Choose the new game folder"));
         if (chooser.showSaveDialog(this.dialog) != JFileChooser.APPROVE_OPTION) return;
         Path destination = chooser.getSelectedFile().toPath();
         String message = ProjectBattleAlpha.this.tr("Скопировать профиль, библиотеки и Java в указанную папку и переключить лаунчер? Старые папки останутся резервными копиями.",
               "Copy profile, libraries and Java to this folder and switch the launcher? Old folders will remain as backups.") + "\n" + destination;
         if (JOptionPane.showConfirmDialog(this.dialog, message, ProjectBattleAlpha.this.tr("Перенос игры", "Move game"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
         this.storageState.setText(ProjectBattleAlpha.this.tr("Копируем и проверяем файлы...", "Copying and verifying files..."));
         this.runMaintenance(() -> {
            try {
               if (ProjectBattleAlpha.this.minecraftRunning || ProjectBattleAlpha.this.isProjectBattleMinecraftProcessRunning())
                  throw new IOException(ProjectBattleAlpha.this.tr("Сначала закройте Minecraft.", "Close Minecraft first."));
               long copied = PackMaintenance.migrateGame(ProjectBattleAlpha.this.config.instanceDir, ProjectBattleAlpha.this.config.minecraftDir, destination);
               ProjectBattleAlpha.this.config.instanceDir = destination.toAbsolutePath().normalize().resolve("profile");
               ProjectBattleAlpha.this.config.minecraftDir = destination.toAbsolutePath().normalize().resolve("minecraft");
               ProjectBattleAlpha.this.config.save();
               SwingUtilities.invokeLater(() -> this.storageState.setText(ProjectBattleAlpha.this.tr("Папка перенесена: ", "Game folder moved: ")
                     + destination + "\n" + mb(copied) + ProjectBattleAlpha.this.tr(" скопировано. Старая папка сохранена.", " copied. Old folder retained.")));
            } catch (Exception ex) { this.showMaintenanceError(this.storageState, ex); }
         });
      }

      private void runDiagnostics() {
         this.diagnosticsState.setText(ProjectBattleAlpha.this.tr("Проверяем систему...", "Checking system..."));
         this.runMaintenance(() -> {
            try {
               String report = ProjectBattleAlpha.this.diagnosticText();
               this.latestDiagnostics = report;
               SwingUtilities.invokeLater(() -> this.diagnosticsState.setText(report));
            } catch (Exception ex) { this.showMaintenanceError(this.diagnosticsState, ex); }
         });
      }

      private void createSupportReport() {
         this.runMaintenance(() -> {
            try {
               String diagnostics = ProjectBattleAlpha.this.diagnosticText();
               this.latestDiagnostics = diagnostics;
               Path report = ProjectBattleAlpha.this.writeSupportReport(diagnostics);
               SwingUtilities.invokeLater(() -> {
                  this.diagnosticsState.setText(diagnostics + "\n\n" + ProjectBattleAlpha.this.tr("Отчёт сохранён: ", "Report saved: ") + report);
                  try { Desktop.getDesktop().open(report.getParent().toFile()); } catch (Exception ignored) {}
               });
            } catch (Exception ex) { this.showMaintenanceError(this.diagnosticsState, ex); }
         });
      }

      private void showMaintenanceError(JTextArea output, Exception ex) {
         ProjectBattleAlpha.this.log("Maintenance error: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
         SwingUtilities.invokeLater(() -> output.setText(ProjectBattleAlpha.this.tr("Ошибка: ", "Error: ") + ex.getMessage()));
      }

      private JPanel controls() {
         JPanel var1 = new JPanel(new FlowLayout(0, 8, 0));
         var1.setOpaque(false);
         var1.setAlignmentX(0.0F);
         return var1;
      }

      private JComponent infoLine(String var1, String var2) {
         JPanel var3 = new JPanel(new FlowLayout(0, 8, 0));
         var3.setOpaque(false);
         var3.setAlignmentX(0.0F);
         JLabel var4 = new JLabel(var1 + ":");
         var4.setForeground(ProjectBattleAlpha.Colors.MUTED);
         var4.setFont(new Font("SansSerif", 0, 12));
         JLabel var5 = new JLabel(var2 != null && !var2.isBlank() ? var2 : "—");
         var5.setForeground(ProjectBattleAlpha.Colors.TEXT);
         var5.setFont(new Font("SansSerif", 1, 12));
         var3.add(var4);
         var3.add(var5);
         return var3;
      }

      private JButton commandButton(String var1) {
         JButton var2 = ProjectBattleAlpha.this.actionButton(var1);
         var2.setFont(new Font("SansSerif", 1, 12));
         var2.setCursor(ProjectBattleAlpha.launcherCursor(12));
         return var2;
      }

      private JButton saveButton() {
         JButton var1 = this.commandButton(ProjectBattleAlpha.this.tr("Сохранить изменения", "Save changes"));
         var1.setAlignmentX(0.0F);
         var1.setPreferredSize(new Dimension(194, 40));
         var1.addActionListener(var1x -> this.saveSettings());
         return var1;
      }

      private void copyText(String var1, JButton var2, String var3, String var4) {
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(var1), null);
         var2.setText(var3);
         Timer var5 = new Timer(1100, var2x -> var2.setText(var4));
         var5.setRepeats(false);
         var5.start();
      }

      private void signOut() {
         String var1 = ProjectBattleAlpha.this.config.authToken;
         ProjectBattleAlpha.this.clearAuth();
         ProjectBattleAlpha.this.worker.submit(() -> {
            try {
               if (var1 != null && !var1.isBlank()) {
                  ProjectBattleAlpha.this.authPost("/auth/logout", "{}", var1);
               }
            } catch (Exception var3) {
            }
         });
         this.dialog.dispose();
         ProjectBattleAlpha.this.openLauncherSettings("profile");
      }

      private void copyLogs(JButton var1) {
         String var2 = ProjectBattleAlpha.this.logArea == null ? "" : ProjectBattleAlpha.this.logArea.getText();
         this.copyText(var2, var1, ProjectBattleAlpha.this.tr("Логи скопированы", "Logs copied"), ProjectBattleAlpha.this.tr("Скопировать логи", "Copy logs"));
      }

      private void saveSettings() {
         ProjectBattleAlpha.this.config.language = this.language != null && this.language.getSelectedIndex() == 1 ? "en" : "ru";
         if (this.launchAction != null) {
            ProjectBattleAlpha.this.config.launchAction = this.launchAction.getSelectedIndex() == 1 ? "exit" : "keep";
         }

         if (this.ram != null) {
            ProjectBattleAlpha.this.config.ramMb = ProjectBattleAlpha.normalizeRamMb(this.ram.getValue());
         }

         if (this.staySignedIn != null) {
            ProjectBattleAlpha.this.config.rememberAccount = this.staySignedIn.isSelected();
         }

         if (this.discordActivity != null) {
            ProjectBattleAlpha.this.config.discordPresenceEnabled = this.discordActivity.isSelected();
         }

         if (this.autoJoin != null) {
            ProjectBattleAlpha.this.config.autoJoinServer = this.autoJoin.isSelected();
         }

         if (this.cleanDirectories != null) {
            ProjectBattleAlpha.this.config.cleanManagedDirs = this.cleanDirectories.isSelected();
         }

         if (ProjectBattleAlpha.this.ramSelector != null) {
            ProjectBattleAlpha.this.ramSelector.setValue(ProjectBattleAlpha.this.config.ramMb);
         }

         ProjectBattleAlpha.this.discordPresence.setEnabled(ProjectBattleAlpha.this.config.discordPresenceEnabled);

         try {
            ProjectBattleAlpha.this.config.save();
            ProjectBattleAlpha.this.log("Launcher settings saved.");
            this.savedState.setText(ProjectBattleAlpha.this.tr("Сохранено", "Saved"));
         } catch (IOException var2) {
            ProjectBattleAlpha.this.log("Could not save launcher settings: " + var2.getMessage());
            this.savedState.setText(ProjectBattleAlpha.this.tr("Ошибка сохранения", "Save failed"));
         }
      }

      private void showSection(String var1) {
         if (!this.navigation.containsKey(var1)) {
            var1 = "general";
         }

         if (!var1.equals(this.currentSection)) {
            this.currentSection = var1;
            this.pages.show(this.pageHost, var1);

            for (Entry var3 : this.navigation.entrySet()) {
               ((JButton)var3.getValue())
                  .setForeground(((String)var3.getKey()).equals(var1) ? ProjectBattleAlpha.Colors.TEXT : ProjectBattleAlpha.Colors.MUTED);
            }

            for (Component var5 : this.pageHost.getComponents()) {
               if (var5.isVisible() && var5 instanceof ProjectBattleAlpha.RevealPanel) {
                  ((ProjectBattleAlpha.RevealPanel)var5).reveal();
                  break;
               }
            }
         }
      }

      private void filterSections() {
         String var1 = this.search.getText().trim().toLowerCase(Locale.ROOT);
         Map var2 = Map.of(
            "general",
            "общие язык запуск лаунчер папки загрузка general language launch launcher folders download",
            "game",
            "игра ram память minecraft сервер подключение game memory join server",
            "profile",
            "профиль аккаунт логин пароль почта email поддержка account profile support login password",
            "security",
            "безопасность вход администратор security admin sign in",
            "files",
            "файлы обновления проверка восстановление хранилище очистка перенос загрузка files update repair storage cleanup move download",
            "diagnostics",
            "диагностика java forge библиотеки сервер диск отчёт логи diagnostics libraries server disk report logs",
            "integrations",
            "интеграции discord telegram привязка активность integrations presence link"
         );
         String var3 = null;

         for (Entry var5 : this.navigation.entrySet()) {
            boolean var6 = var1.isEmpty() || ((String)var2.get(var5.getKey())).contains(var1);
            this.navigationRows.get(var5.getKey()).setVisible(var6);
            if (var6 && var3 == null) {
               var3 = (String)var5.getKey();
            }
         }

         if (!var1.isEmpty() && var3 != null && !var3.equals(this.currentSection)) {
            this.showSection(var3);
         }

         this.revalidate();
         this.repaint();
      }
   }

   private static final class SidebarProjectItem extends JPanel {
      private final String initials;
      private final String title;
      private final boolean arena;
      private boolean active;
      private boolean hovered;

      SidebarProjectItem(String var1, String var2, boolean var3, boolean var4) {
         this.initials = var1;
         this.title = var2;
         this.arena = var3;
         this.active = var4;
         Dimension var5 = new Dimension(176, 56);
         this.setPreferredSize(var5);
         this.setMinimumSize(var5);
         this.setMaximumSize(var5);
         this.setOpaque(false);
         this.setBorder(new EmptyBorder(0, 0, 0, 0));
         this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent var1) {
               SidebarProjectItem.this.hovered = true;
               SidebarProjectItem.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent var1) {
               SidebarProjectItem.this.hovered = false;
               SidebarProjectItem.this.repaint();
            }
         });
      }

      void setActive(boolean var1) {
         this.active = var1;
         this.repaint();
      }

      @Override
      protected void paintComponent(Graphics var1) {
         super.paintComponent(var1);
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         Color var3 = this.arena ? new Color(198, 211, 218) : new Color(231, 205, 133);
         if (this.active) {
            var2.setColor(new Color(var3.getRed(), var3.getGreen(), var3.getBlue(), 16));
            var2.fillRoundRect(0, 1, this.getWidth() - 1, this.getHeight() - 2, 12, 12);

            for (byte var4 = 8; var4 >= 2; var4 -= 2) {
               int var5 = Math.max(5, 32 - var4 * 3);
               var2.setColor(new Color(var3.getRed(), var3.getGreen(), var3.getBlue(), var5));
               var2.fillRoundRect(0, 9 - var4 / 2, 3 + var4 / 2, 38 + var4, 5, 5);
            }

            var2.setColor(var3);
            var2.fillRoundRect(0, 10, 3, 36, 4, 4);
         } else if (this.hovered) {
            var2.setColor(new Color(255, 255, 255, 18));
            var2.fillRoundRect(0, 1, this.getWidth() - 1, this.getHeight() - 2, 12, 12);
         }

         byte var16 = 4;
         byte var17 = 5;
         byte var6 = 50;
         byte var7 = 46;
         Color var8;
         if (this.arena) {
            var8 = this.active ? new Color(190, 198, 201) : new Color(139, 148, 151);
         } else {
            var8 = this.active ? new Color(219, 202, 151) : new Color(164, 151, 112);
         }

         if (this.hovered && !this.active) {
            var8 = new Color(Math.min(255, var8.getRed() + 16), Math.min(255, var8.getGreen() + 16), Math.min(255, var8.getBlue() + 16));
         }

         var2.setColor(var8);
         var2.fillRoundRect(var16, var17, var6, var7, 10, 10);
         var2.setColor(new Color(255, 255, 255, this.active ? 80 : 36));
         var2.drawRoundRect(var16, var17, var6 - 1, var7 - 1, 10, 10);
         var2.setFont(new Font("SansSerif", 1, 14));
         FontMetrics var9 = var2.getFontMetrics();
         int var10 = var16 + (var6 - var9.stringWidth(this.initials)) / 2;
         int var11 = var17 + (var7 - var9.getHeight()) / 2 + var9.getAscent();
         var2.setColor(new Color(26, 27, 26));
         var2.drawString(this.initials, var10, var11);
         Font var12 = new Font("SansSerif", this.active ? 1 : 0, this.arena ? 11 : 14);
         var2.setFont(var12);
         byte var13 = 66;
         Color var14 = this.active ? ProjectBattleAlpha.Colors.TEXT : ProjectBattleAlpha.Colors.MUTED;
         Color var15 = this.arena ? new Color(196, 208, 214, 46) : new Color(230, 205, 140, 50);
         if (this.arena) {
            if (this.active) {
               drawBloomText(var2, "Project Battle", var13, 25, var15);
               drawBloomText(var2, "Arena", var13, 42, var15);
            }

            var2.setColor(var14);
            var2.drawString("Project Battle", var13, 25);
            var2.drawString("Arena", var13, 42);
         } else {
            if (this.active) {
               drawBloomText(var2, this.title, var13, 34, var15);
            }

            var2.setColor(var14);
            var2.drawString(this.title, var13, 34);
         }

         var2.dispose();
      }

      private static void drawBloomText(Graphics2D var0, String var1, int var2, int var3, Color var4) {
         var0.setColor(var4);
         var0.drawString(var1, var2 - 1, var3);
         var0.drawString(var1, var2 + 1, var3);
         var0.drawString(var1, var2, var3 - 1);
         var0.drawString(var1, var2, var3 + 1);
      }
   }

   private static final class SoftActionButton extends JButton {
      SoftActionButton(String var1) {
         super(var1);
         this.setContentAreaFilled(false);
         this.setOpaque(false);
         this.setFocusPainted(false);
         this.setForeground(ProjectBattleAlpha.Colors.TEXT);
         this.setBorder(new EmptyBorder(8, 14, 8, 14));
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(33, 42, 41), this.getWidth(), this.getHeight(), new Color(18, 24, 24)));
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 14, 14);
         if (this.getModel().isRollover()) {
            var2.setColor(new Color(255, 255, 255, 18));
            var2.fillRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 12, 12);
         }

         var2.setColor(new Color(114, 124, 118));
         var2.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 14, 14);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class SoftPanel extends JPanel {
      SoftPanel() {
         this.setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var2.setPaint(new GradientPaint(0.0F, 0.0F, new Color(22, 25, 24, 238), this.getWidth(), this.getHeight(), new Color(13, 17, 17, 238)));
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 18, 18);
         var2.setPaint(new GradientPaint(this.getWidth() * 0.15F, 0.0F, new Color(210, 185, 120, 24), this.getWidth(), this.getHeight(), new Color(0, 0, 0, 0)));
         var2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 18, 18);
         var2.dispose();
         super.paintComponent(var1);
      }
   }

   private static final class StartupSplash extends JWindow {
      StartupSplash() {
         this.setContentPane(new ProjectBattleAlpha.StartupSplashPanel());
         this.setSize(980, 520);
         this.setBackground(new Color(0, 0, 0, 0));
      }

      void showCentered() {
         this.setLocationRelativeTo(null);
         this.setVisible(true);
      }
   }

   private static final class StartupSplashPanel extends JPanel {
      private final long startedAt = System.nanoTime();
      private final Timer timer;
      private final BufferedImage planetImage = ProjectBattleAlpha.loadImage("/assets/startup-planet.png");

      StartupSplashPanel() {
         this.setOpaque(true);
         this.timer = new Timer(60, var1 -> this.repaint());
         this.timer.start();
      }

      void stopAnimation() {
         this.timer.stop();
      }

      @Override
      protected void paintComponent(Graphics var1) {
         Graphics2D var2 = (Graphics2D)var1.create();
         int var3 = this.getWidth();
         int var4 = this.getHeight();
         var2.setColor(new Color(15, 17, 18));
         var2.fillRect(0, 0, var3, var4);
         var2.setColor(new Color(255, 255, 255, 11));

         for (byte var5 = 0; var5 < var4; var5 += 3) {
            var2.drawLine(0, var5, var3, var5);
         }

         long var21 = (System.nanoTime() - this.startedAt) / 1000000L;
         double var7 = Math.min(1.0, var21 / 460.0);
         int var9 = var3 / 2;
         int var10 = var4 / 2;
         byte var11 = 38;
         var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         if (this.planetImage != null) {
            var2.setComposite(AlphaComposite.getInstance(3, (float)var7));
            var2.drawImage(this.planetImage, var9 - var11, var10 - var11, var11 * 2, var11 * 2, null);
            var2.setComposite(AlphaComposite.SrcOver);
         } else {
            var2.setStroke(new BasicStroke(1.5F));
            var2.setColor(new Color(201, 176, 123, (int)(210.0 * var7)));
            var2.drawOval(var9 - var11, var10 - var11, var11 * 2, var11 * 2);
         }

         int[][] var12 = new int[][]{{0, -57}, {-50, 32}, {50, 32}};

         for (int var13 = 0; var13 < var12.length; var13++) {
            double var14 = ((var21 - var13 * 240) % 900.0 + 900.0) % 900.0 / 900.0;
            float var16 = (float)(0.24 + 0.76 * Math.max(0.0, Math.sin(var14 * Math.PI)) * var7);
            int var17 = (int)(185.0F * var16);
            var2.setColor(new Color(201, 176, 123, var17));
            int var18 = var9 + var12[var13][0];
            int var19 = var10 + var12[var13][1];
            int var20 = 5 + (int)(2.0F * var16);
            var2.fillOval(var18 - var20, var19 - var20, var20 * 2, var20 * 2);
         }

         var2.dispose();
      }
   }

   private static final class Version {
      String id;
      String clientJarId;
      String mainClass = "net.minecraft.client.main.Main";
      String assets = "1.20";
      final List<ProjectBattleAlpha.Library> libraries = new ArrayList<>();
      final List<String> jvmArgs = new ArrayList<>();
      final List<String> gameArgs = new ArrayList<>();

      static ProjectBattleAlpha.Version load(Path var0, String var1) throws IOException {
         return load(var0, var1, new HashSet<>());
      }

      private static ProjectBattleAlpha.Version load(Path var0, String var1, Set<String> var2) throws IOException {
         var1 = var1 == null ? "" : var1.trim();
         if (var1.isEmpty()) {
            var1 = "1.20.1-forge-47.4.28";
         }

         if (!var2.add(var1)) {
            throw new IOException("Circular version inheritance: " + var1);
         }

         Path var3 = var0.resolve("versions").resolve(var1).resolve(var1 + ".json");
         if (!Files.exists(var3)) {
            throw new FileNotFoundException("Forge version not installed: " + var3);
         }

         if (ProjectBattleAlpha.Json.parse(Files.readString(var3, StandardCharsets.UTF_8)) instanceof Map var4) {
            String var15 = ProjectBattleAlpha.str(var4.get("inheritsFrom"));
            if (var15 != null) {
               var15 = var15.trim();
            }

            if (var15 != null && (var15.isBlank() || var15.equals(var1))) {
               var15 = null;
            }

            ProjectBattleAlpha.Version var6 = var15 == null ? new ProjectBattleAlpha.Version() : load(var0, var15, var2);
            var6.id = var1;
            if (var6.clientJarId == null) {
               var6.clientJarId = var15 == null ? var1 : var15;
            }

            String var7 = ProjectBattleAlpha.str(var4.get("mainClass"));
            if (var7 != null) {
               var6.mainClass = var7;
            }

            String var8 = ProjectBattleAlpha.str(var4.get("assets"));
            if (var8 != null) {
               var6.assets = var8;
            }

            Object var9 = var4.get("libraries");
            if (var9 instanceof List) {
               for (Object var11 : (List)var9) {
                  if (var11 instanceof Map var12) {
                     ProjectBattleAlpha.Library var13 = ProjectBattleAlpha.Library.from(var12);
                     if (var13 != null) {
                        var6.libraries.add(var13);
                     }
                  }
               }
            }

            if (var4.get("arguments") instanceof Map var16) {
               addArgs(var6.jvmArgs, var16.get("jvm"));
               addArgs(var6.gameArgs, var16.get("game"));
            }

            if (var6.jvmArgs.isEmpty()) {
               var6.jvmArgs.add("-Djava.library.path=${natives_directory}");
               var6.jvmArgs.add("-Dminecraft.launcher.brand=${launcher_name}");
               var6.jvmArgs.add("-Dminecraft.launcher.version=${launcher_version}");
               var6.jvmArgs.add("-cp");
               var6.jvmArgs.add("${classpath}");
            }

            if (var6.gameArgs.isEmpty()) {
               String var17 = ProjectBattleAlpha.str(var4.get("minecraftArguments"));
               if (var17 != null) {
                  var6.gameArgs.addAll(Arrays.asList(var17.split(" ")));
               } else {
                  Collections.addAll(
                     var6.gameArgs,
                     "--username",
                     "${auth_player_name}",
                     "--version",
                     "${version_name}",
                     "--gameDir",
                     "${game_directory}",
                     "--assetsDir",
                     "${assets_root}",
                     "--assetIndex",
                     "${assets_index_name}",
                     "--uuid",
                     "${auth_uuid}",
                     "--accessToken",
                     "${auth_access_token}",
                     "--userType",
                     "${user_type}",
                     "--versionType",
                     "${version_type}"
                  );
               }
            }

            return var6;
         } else {
            throw new IOException("Bad version json.");
         }
      }

      private static void addArgs(List<String> var0, Object var1) {
         if (var1 instanceof List var2) {
            for (Object var4 : var2) {
               if (var4 instanceof String var5) {
                  var0.add(var5);
               } else if (var4 instanceof Map var6 && ProjectBattleAlpha.Library.rulesAllowWindows(var6.get("rules"))) {
                  Object var7 = var6.get("value");
                  if (var7 instanceof String var8) {
                     var0.add(var8);
                  } else if (var7 instanceof List) {
                     for (Object var10 : (List)var7) {
                        if (var10 instanceof String var11) {
                           var0.add(var11);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
