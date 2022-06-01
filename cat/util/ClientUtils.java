package cat.util;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ClientUtils extends MinecraftInstance {
    private static final Logger logger = LogManager.getLogger("BlueZenith");

    public static Logger getLogger() {
        return logger;
    }

    public static void displayChatMessage(final String message) {
        if (mc.thePlayer == null) {
            getLogger().info("(MCChat)" + message.replaceAll("§", ""));
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }
    
    public static void fancyMessage(String f){
        ClientUtils.displayChatMessage("§3§l[§r§bBlue Zenith§3§l] §r§9"+f);
    }
    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
    public static float getRandomFloat(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }
    public static long getRandomLong(long min, long max) {
        return (long) (Math.random() * (1000 / min - 1000 / max + 1) + 1000 / max);
    }
    public static File openFileChooser(File initFile, FileChooser.ExtensionFilter...ex){
        AtomicReference<File> o = new AtomicReference<>();
        o.set(null);

        runAndWait(() -> {
            FileChooser d = new FileChooser();
            if (initFile != null) {
                if(initFile.getParentFile().isDirectory()){
                    d.setInitialDirectory(initFile.getParentFile());
                }
                d.setInitialFileName(initFile.getName());
            }
            d.getExtensionFilters().addAll(ex);
            o.set(d.showOpenDialog(null));
        });

        return o.get();
    }
    public static File openFileSaver(File initFile, FileChooser.ExtensionFilter...ex){
        AtomicReference<File> o = new AtomicReference<>();
        o.set(null);

        runAndWait(() -> {
            FileChooser d = new FileChooser();
            if (initFile != null) {
                if(initFile.getParentFile().isDirectory()){
                    d.setInitialDirectory(initFile.getParentFile());
                }

                d.setInitialFileName(initFile.getName());
            }
            d.getExtensionFilters().addAll(ex);
            o.set(d.showSaveDialog(null));
        });

        return o.get();
    }
    /**
     * THIS WASN'T MADE BY ME! https://news.kynosarges.org/2014/05/01/simulating-platform-runandwait/ WHY THIS TOOK SO LONG OMG
     */
    public static void runAndWait(Runnable action) {
        if (action == null)
            throw new NullPointerException("action");

        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        final CountDownLatch doneLatch = new CountDownLatch(1);
        new JFXPanel();
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });

        try {
            doneLatch.await();
        } catch (InterruptedException ignored) {}
    }
}
