package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;

public class PanicCommand extends Command {

    public PanicCommand() {
        super("Panic", "Disable all modules.", "panic (norender)");
    }

    @Override
    public void execute(String[] args) {
        boolean norender = false;
        if(args.length >= 2) {
            if(args[1].equalsIgnoreCase("norender"))
                norender = true;
        }
        for(Module m : BlueZenith.moduleManager.getModules()) {
            if(m.getCategory() != ModuleCategory.RENDER || !norender) {
                m.setState(false);
            }
        }
        NotificationManager.publish("Panic", "Disabled all §4modules!", NotificationType.SUCCESS, 3000);
    }
}
