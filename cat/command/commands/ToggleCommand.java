package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;
import cat.module.Module;

import static cat.ui.notifications.NotificationManager.publish;
import static cat.ui.notifications.NotificationType.ERROR;
import static cat.ui.notifications.NotificationType.INFO;

@SuppressWarnings("unused")
public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("Toggle", "Toggle a module.",".t module","t", "enable");
    }
    @Override
    public void execute(String[] args){
        if(args.length > 1){
            Module m = BlueZenith.moduleManager.getModule(args[1]);
            if(m == null){
                publish("Toggle", "Couldn't find that module.", ERROR, 2500);
                return;
            }
            m.toggle();
            publish("Toggle", m.getState() ? "Enabled "+m.getName() : "Disabled "+m.getName()+"", INFO, 2500);
        }else{
            publish("Toggle", "Usage: " + this.syntax, INFO, 3000);
        }
    }
}
