package cat.command.commands;

import cat.BlueZenith;
import cat.FriendManager;
import cat.command.Command;
import cat.module.modules.render.HUD;
import cat.module.value.types.StringValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "Adds and removes friends!", "friend <add|remove> <name>", "f");
    }

    @Override
    public void execute(String[] args) {
        if(args.length != 3) {
            NotificationManager.publish("Friend", "friend <add|remove> <name>", NotificationType.ERROR, 2500);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add":
                FriendManager.add(args[2].toLowerCase());
                break;
            case "remove":
                FriendManager.remove(args[2].toLowerCase());
                break;
            default:
                NotificationManager.publish("Friend", "friend <add|remove> <name>", NotificationType.ERROR, 2500);
                break;
        }
    }
}
