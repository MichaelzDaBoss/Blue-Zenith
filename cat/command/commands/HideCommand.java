package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;
import cat.module.Module;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;

@SuppressWarnings("unused")
public class HideCommand extends Command {

    public HideCommand() {
        super("Hide", "Hide a module from the ArrayList.", ".hide modulename");
    }

    @Override
    public void execute(String[] args) {
        if(args.length < 2) {
            chat("Usage: " + this.syntax);
            return;
        }
        Module mod = BlueZenith.moduleManager.getModule(args[1]);
        if(mod == null) {
            NotificationManager.publish("Hide", "Couldn't find that module.", NotificationType.ERROR, 2500);
            return;
        }
        mod.hidden = !mod.hidden;
        NotificationManager.publish("Hide", mod.getName() + (mod.hidden ? " is now hidden." : " is now shown."), NotificationType.INFO, 2500);
    }
}
