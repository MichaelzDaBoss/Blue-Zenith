package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;
import cat.module.Module;
import cat.module.modules.render.ClickGUI;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.Locale;
@SuppressWarnings("unused")
public class BindCommand extends Command {
    public BindCommand() {
        super("Bind", "Bind a module.",".bind module key | .bind key module", "b", "binds");
    }
    @Override
    public void execute(String[] args){

        if(args[0].equalsIgnoreCase("binds")) {
            chat("List of all set binds:");
            BlueZenith.moduleManager.getModules().forEach(mod -> {
                if(mod.keyBind > 0)
                    chat(mod.getName() + ": " + Keyboard.getKeyName(mod.keyBind).toLowerCase());
            });
            return;
        }
        if(args.length == 2) {
            switch(args[1].toLowerCase()) {
                case "list":
                    chat("List of all set binds:");
                    BlueZenith.moduleManager.getModules().stream().sorted(Comparator.comparing(mod -> Keyboard.getKeyName(mod.keyBind)))
                            .filter(mod -> mod.keyBind > 0)
                            .forEach(mod -> chat(mod.getName() + ": " + Keyboard.getKeyName(mod.keyBind).toLowerCase()));
                break;

                case "reset":
                    BlueZenith.moduleManager.getModules().forEach(mod -> {
                       if(mod instanceof ClickGUI) return;
                       mod.keyBind = 0;
                    });
                    NotificationManager.publish("Bind", "Reset binds for all modules", NotificationType.INFO, 2000);
                break;
            }
            return;
        }

        if(args.length > 2){
            Module m = BlueZenith.moduleManager.getModule(args[1]);
            if(m == null){
                Module m1 = BlueZenith.moduleManager.getModule(args[2]);
                checkKey(args[1], args[2]);
                if(m1 == null) {
                    checkKey(args[2], args[1]);
                } else {
                    int key = Keyboard.getKeyIndex(args[1].toUpperCase());
                    if(key == 0) {
                        NotificationManager.publish("Bind", "Invalid key specified: §7" + args[1], NotificationType.ERROR, 2000);
                        //chat("Invalid key specified: " + args[1]);
                        bind(0, m1);
                        return;
                    }
                    bind(key, m1);
                }
                return;
            }
            int k = Keyboard.getKeyIndex(args[2].toUpperCase(Locale.ROOT));
            bind(k, m);
        }else {
            chat("Syntax: .bind <module> <key>");
        }
    }
    private void checkKey(String key, String modName) {
        if(Keyboard.getKeyIndex(key.toUpperCase()) == 0) {
            NotificationManager.publish("Bind", "Couldn't find module §7" + modName, NotificationType.ERROR, 2000);
            //chat("Failed to find module " + modName);
        }
    }

    private void bind(int key, Module mod) {
        mod.keyBind = key;
        NotificationManager.publish("Bind", "Bound " + mod.getName() + " to " + Keyboard.getKeyName(key), NotificationType.INFO, 2000);
        //chat("Bound " + mod.getName() + " to " + Keyboard.getKeyName(key));
    }
}
