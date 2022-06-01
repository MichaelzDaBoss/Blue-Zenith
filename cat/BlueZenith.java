package cat;

import cat.client.ConfigManager;
import cat.client.Connection;
import cat.client.HWID;
import cat.command.CommandManager;
import cat.events.EventManager;
import cat.module.ModuleManager;
import cat.module.modules.player.Blink;
import cat.module.modules.render.ClickGUI;
import cat.ui.GuiMain;
import cat.ui.clickgui.ClickGui;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;
import com.google.common.eventbus.EventBus;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import org.lwjgl.opengl.Display;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlueZenith {
    private static final String[] devs =
            {"3c120deab28db9574f345f62f4fbca55acf2fb53930a0c7758610e4975997", "2129e0c24fc31b3c1963c7ff6732b233ca58c4e135dfe43b52e5abd5df0339e",
                    "ea5259b2e38173c336a8fee84c4cb7edce5e066a3189a4bbce882ec3670638", "35f118310bcc65cc849156a559a933771f8de125a277c344fdbdd9af822e8"};
    public static String currentServerIP;
    public static String name = "Blue Zenith"; // sued by angles
    public static String version = "b3.0";
    public static boolean useExperimentalEventBus = true;
    public static EventManager eventManager;
    public static EventBus eventBus;
    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static GuiMain guiMain;
    public static DiscordUser discordUser;
    public static Connection connection;
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final String applicationID = "968847033463103528";
    private static DiscordRichPresence rpc;
    private static long startTime;
    private static boolean isDeveloper = false;
    private static final boolean startRPC = true;
    public static final String envAlt = System.getenv("BLUE_ZENITH_ENV_ALT");

    public BlueZenith() {}

    public static void start() {
        startTime = System.currentTimeMillis();
        HWID.getHWID();
        isDeveloper = false /* Arrays.stream(devs).anyMatch(hwid -> hwid.equalsIgnoreCase(HWID.hwid)) */;
        ClientUtils.getLogger().info("Starting BlueZenith " + version);
        if(useExperimentalEventBus)
        eventBus = new EventBus();
        eventManager = new EventManager();
        ClientUtils.getLogger().info("Started event manager.");
        moduleManager = new ModuleManager();
        ClientUtils.getLogger().info("Loaded " + moduleManager.getModules().size() + " modules.");
        commandManager = new CommandManager();
        ClientUtils.getLogger().info("Loaded " + commandManager.commands.size() + " commands.");
        hook();
        ClientUtils.getLogger().info("Added a shutdown hook.");
        ConfigManager.load("default", false, false);
        ConfigManager.loadBinds();
        moduleManager.getModule(Blink.class).setState(false);
        ClientUtils.getLogger().info("Loaded the default config and binds.");
        if(startRPC)
            initRPC();
        guiMain = new GuiMain();
        ClickGUI.clickGui = new ClickGui();
        ConfigManager.loadClickGUIPanels();
        ClientUtils.getLogger().info("Created ClickGUI.");
        Display.setTitle(name + " | 1.8.9 | " + version + (isDeveloper ? " | Developer" : " Beta"));
        ClientUtils.getLogger().info("Started in " + (System.currentTimeMillis() - startTime) + " ms.");
        NotificationManager.addNoti("Started in " + (System.currentTimeMillis() - startTime) + " ms.", "", NotificationType.SUCCESS, 5000);
    }

    private static void hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            moduleManager.getModule(ClickGUI.class).setState(false);
            eventManager.shutdown();
            ConfigManager.save("default");
            ConfigManager.saveBinds();
            ConfigManager.saveClickGUIPanels();
            DiscordRPC.discordShutdown();
        }));
    }

    private static void initRPC() {
        rpc = new DiscordRichPresence.Builder("Starting up...")
                .setBigImage("main", "Build " + version)
                .setStartTimestamps(System.currentTimeMillis())
                .setEndTimestamp(System.currentTimeMillis() - startTime)
                .build();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> {
                    ClientUtils.getLogger().info("Started Discord RPC! Account: " + user.username + "#" + user.discriminator);
                    discordUser = user;
                        })
                .build();

        DiscordRPC.discordInitialize(applicationID, handlers, true);

        DiscordRPC.discordUpdatePresence(rpc);

        scheduledExecutorService.scheduleAtFixedRate(() -> { //update presence every second
            DiscordRPC.discordRunCallbacks();
            rpc.startTimestamp = System.currentTimeMillis();
            rpc.endTimestamp = System.currentTimeMillis() + 447600;
            DiscordRPC.discordUpdatePresence(rpc);
        }, 0, 700, TimeUnit.MILLISECONDS);
    }

    public static void updateRPC(String line1, String line2) {
        //developer badge :flushed:
        if(line1.contains("trans") || line2.contains("trans"))
            throw new RuntimeException("get fucked");

        DiscordRichPresence.Builder f = new DiscordRichPresence.Builder(line2);
        f.setBigImage("bz", version).setDetails(line1).build();
  //      if(isDeveloper){
  //          f.setSmallImage("developer_badge", "Maintainer");
  //      }
        rpc = f.build();
        DiscordRPC.discordUpdatePresence(rpc);
    }

    public static void register(Object listener) {
        if(useExperimentalEventBus)
            eventBus.register(listener);
        else eventManager.registerListener(listener);
    }

    public static void unregister(Object listener) {
        if(useExperimentalEventBus)
            eventBus.unregister(listener);
        else eventManager.unregisterListener(listener);
    }
}
