package cat.client;

import cat.BlueZenith;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.modules.render.ClickGUI;
import cat.module.value.Value;
import cat.module.value.types.ActionValue;
import cat.module.value.types.ListValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;
import cat.util.FileUtil;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class ConfigManager {

    private static int configsLoaded = 0;
    public final static Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
    public static String currentConfig = "";

    public static void save(String name) {
        JsonObject config = new JsonObject();
        BlueZenith.moduleManager.getModules().forEach(mod -> {
            JsonObject module = new JsonObject();
            module.add("toggled", new JsonPrimitive(mod.getState()));
            module.add("displayName", new JsonPrimitive(mod.displayName));
            module.add("keybind", new JsonPrimitive(mod.keyBind));
            mod.getValues().forEach(val -> {
                if (!(val instanceof ActionValue)) //ignore the action value to prevent funny
                    if (val instanceof ListValue) { //i might have autism
                        final JsonObject options = new JsonObject();
                        ListValue value = (ListValue) val;
                        value.choices.forEach(choice -> options.add(choice.name, new JsonPrimitive(choice.get())));
                        module.add(val.name, options);
                    } else
                        module.add(val.name, val.getPrimitive());
            });
            config.add(mod.getName(), module);
        });
        try {
            BufferedWriter writer = FileUtil.getWriter(false, FileUtil.configFolder + File.separator + name + ".json");
            if (writer == null) {
                ClientUtils.fancyMessage("Something went wrong. Is this supposed to happen?");
                return;
            }
            writer.write(gson.toJson(config));
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        NotificationManager.publish("Saved config!", "Saved config §7" + name, NotificationType.SUCCESS, 2500);
        //ClientUtils.fancyMessage("Saved config " + name);
    }

    public static void load(String name, boolean changeBinds, boolean ignoreRender) {
        if (!FileUtil.exists(false, FileUtil.configFolder, name + ".json")) {
            NotificationManager.publish("Couldn't find config.", "Could not find config %name!".replace("%name", name), NotificationType.ERROR, 2500);
            //ClientUtils.fancyMessage("Couldn't find that config.");
            return;
        }
        BufferedReader reader = FileUtil.getReader(false, FileUtil.configFolder + File.separator + name + ".json");
        if (reader == null) {
            ClientUtils.fancyMessage("Something went wrong. Is this supposed to happen?");
            return;
        }
        currentConfig = name;
        JsonObject config = new JsonParser().parse(reader).getAsJsonObject();
        config.entrySet().forEach(entry -> {
            Module module = BlueZenith.moduleManager.getModule(entry.getKey());
            if (module == null) return;
            entry.getValue().getAsJsonObject().entrySet().forEach(settings -> {
                if (ignoreRender && module.getCategory().equals(ModuleCategory.RENDER)) {
                    return;
                }
                switch (settings.getKey()) {
                    case "toggled":
                        module.setState(settings.getValue().getAsBoolean());
                        break;

                    case "displayName":
                        module.displayName = settings.getValue().getAsString();
                        break;

                    case "keybind":
                        if (changeBinds)
                            module.keyBind = settings.getValue().getAsInt();
                        break;

                    default:
                        Value<?> val = module.getValue(settings.getKey());
                        if (val == null) return;
                        if (val instanceof ListValue && entry.getValue().isJsonObject()) {
                            ListValue value = (ListValue) val;
                            value.fromObject(entry.getValue().getAsJsonObject().get(val.name).getAsJsonObject());
                        } else
                            val.fromPrimitive(settings.getValue().getAsJsonPrimitive());
                        break;
                }
            });
        });
        if (configsLoaded > 0)
            NotificationManager.publish("Loaded config %name!".replace("%name", name), "Loaded " + (changeBinds? "without changing binds" : "and changed binds"), NotificationType.SUCCESS, 3000);
        configsLoaded++;
        try {
            reader.close();
        } catch (IOException ignored) {

        }
    }

    public static void saveBinds() {
        try {
            BufferedWriter writer = FileUtil.getWriter(true, File.separator + "binds.json");
            JsonObject binds = new JsonObject();
            BlueZenith.moduleManager.getModules().forEach(mod -> binds.add(mod.getName(), new JsonPrimitive(mod.keyBind)));
            assert writer != null;
            writer.write(gson.toJson(binds));
            writer.close();
        } catch (Exception ex) {
            ClientUtils.getLogger().error("Failed to save keybinds!");
            ex.printStackTrace();
        }
    }

    public static void loadBinds() {
        try {
            BufferedReader reader = FileUtil.getReader(true, File.separator + "binds.json");
            assert reader != null;
            JsonObject config = new JsonParser().parse(reader).getAsJsonObject();
            config.entrySet().forEach(entry -> Objects.requireNonNull(BlueZenith.moduleManager.getModule(entry.getKey())).keyBind = entry.getValue().getAsInt());
            reader.close();
        } catch (Exception ex) {
            ClientUtils.getLogger().error("Failed to load keybinds!");
        }
    }

    public static void saveClickGUIPanels() {
    //    try {
    //        final BufferedWriter writer = FileUtil.getWriter(true, File.separator + "clickgui.json");
    //        assert writer != null;
    //        JsonObject panels = new JsonObject();
    //        ClickGUI.clickGui.getPanels().forEach(panel -> {
    //            JsonObject panelInfo = new JsonObject();
    //            panelInfo.add("x", new JsonPrimitive(panel.x));
    //            panelInfo.add("y", new JsonPrimitive(panel.y));
    //            panelInfo.add("shown", new JsonPrimitive(panel.showContent));
    //            panels.add(panel.id, panelInfo);
    //        });
    //        writer.write(gson.toJson(panels));
    //        writer.close();
    //    } catch (Exception ex) {
            ClientUtils.getLogger().error("Failed to save ClickGUI positions!");
    //    }
    }

    public static void loadClickGUIPanels() {
    //    try {
    //        final BufferedReader reader = FileUtil.getReader(true, File.separator + "clickgui.json");
    //        assert reader != null;
    //        JsonObject panels = new JsonParser().parse(reader).getAsJsonObject();
    //        panels.entrySet().forEach(entry -> {
    //            Panel panel = ClickGUI.clickGui.getPanel(entry.getKey());
    //            if (panel == null) return;
    //            entry.getValue().getAsJsonObject().entrySet().forEach(pos -> {
    //                switch (pos.getKey()) {
    //                    case "x":
    //                        panel.x = pos.getValue().getAsFloat();
    //                        break;
//
    //                    case "y":
    //                        panel.y = pos.getValue().getAsFloat();
    //                        break;
//
    //                    case "shown":
    //                        panel.showContent = pos.getValue().getAsBoolean();
    //                        break;
    //                }
    //            });
    //        });
    //        reader.close();
    //    } catch (Exception exception) {
            ClientUtils.getLogger().error("Failed to load ClickGUI positions!");
    //    }
    }

    public static void saveAlts(String rndOff) {
        try {
            final BufferedWriter writer = FileUtil.getWriter(true, File.separator + "alts.json");
            assert writer != null;
            JsonObject panels = new JsonObject();
            JsonObject altManager = new JsonObject();
            altManager.addProperty("random-offline", rndOff);
  //          ClickGUI.clickGui.getPanels().forEach(panel -> {
  //              JsonObject panelInfo = new JsonObject();
  //              panelInfo.add("x", new JsonPrimitive(panel.x));
  //              panelInfo.add("y", new JsonPrimitive(panel.y));
  //              panelInfo.add("shown", new JsonPrimitive(panel.showContent));
  //              panels.add(panel.id, panelInfo);
  //          });
            panels.add("alt-manager", altManager);
            writer.write(gson.toJson(panels));
            writer.close();
        } catch (Exception ex) {
            ClientUtils.getLogger().error("Failed to save alts!");
        }
    }

    public static String loadAlts() {
        String rndOff = "BZR";
        try {
            final BufferedReader reader = FileUtil.getReader(true, File.separator + "clickgui.json");
            assert reader != null;
            JsonObject panels = new JsonParser().parse(reader).getAsJsonObject();
            rndOff = panels.get("alt-manager").getAsJsonObject().get("random-offline").getAsString();
            reader.close();
        } catch (Exception exception) {
            ClientUtils.getLogger().error("Failed to load alts!");
        }
        return rndOff;
    }
}