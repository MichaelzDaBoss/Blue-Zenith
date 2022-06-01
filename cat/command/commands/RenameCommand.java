package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;
import cat.module.Module;
import cat.ui.notifications.NotificationType;

import static cat.ui.notifications.NotificationManager.publish;
import static cat.ui.notifications.NotificationType.*;

@SuppressWarnings("unused")
public class RenameCommand extends Command {

    public RenameCommand() {
        super("Rename", "Rename a module in the arraylist.",".rename <module> <new name>", "rn");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 1) {
            publish("Rename", "Usage: " + this.syntax, NotificationType.INFO, 3000);
            return;
        }
        if(args.length == 2) {
            if(args[1].equalsIgnoreCase("reset")) {
                BlueZenith.moduleManager.getModules().forEach(a -> a.displayName = a.getName());
                publish("Rename", "Reset all custom names!", SUCCESS, 2500);
            }
        } else {
            Module mod = BlueZenith.moduleManager.getModule(args[1]);
            if (mod == null) {
                publish("Rename", "Couldn't find that module.", ERROR, 2500);
                return;
            }
            String newName = args[2];
            if (newName.equalsIgnoreCase("reset")) {
                mod.displayName = mod.getName();
                publish("Rename", "Reset name for " + mod.getName(), INFO, 2500);
            } else {
                mod.displayName = args[2].replaceAll("_", " ").replaceAll("&", "§");
                publish("Rename", "Renamed " + mod.getName() + " to " + mod.displayName, INFO, 2800);
            }
        }
    }
}
