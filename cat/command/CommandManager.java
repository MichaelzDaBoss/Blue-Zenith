package cat.command;

import cat.BlueZenith;
import cat.command.commands.*;
import cat.events.impl.SentMessageEvent;
import cat.module.ModuleCommand;
import cat.util.ClientUtils;

import java.util.ArrayList;

import static cat.util.MinecraftInstance.mc;

public class CommandManager {
    public String commandPrefix = ".";
    public ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {

        commands.add(new BasedCommand());
        commands.add(new BindCommand());
        commands.add(new ClientnameCommand());
        commands.add(new ConfigCommand());
        commands.add(new ForCommand());
        commands.add(new HelpCommand());
        commands.add(new HideCommand());
        commands.add(new PanicCommand());
        commands.add(new RenameCommand());
        commands.add(new ToggleCommand());
        commands.add(new VClipCommand());
        commands.add(new FriendCommand());
        commands.add(new TPCommand());

        BlueZenith.moduleManager.getModules().forEach(mod -> commands.add(new ModuleCommand(mod, mod.getName())));

    }

    public void dispatch(SentMessageEvent event) {
        if (event.message.startsWith(commandPrefix)) {
            if(event.sendToChat){
                mc.ingameGUI.getChatGUI().addToSentMessages(event.message);
            }
            event.cancel();
            String[] args = event.message.substring(commandPrefix.length()).split(" ");
            for (Command command : commands) {
                if (command.name.equalsIgnoreCase(args[0])) {
                    command.execute(args);
                    return;
                }
                for (String alias : command.pref) {
                    if (alias.equalsIgnoreCase(args[0])) {
                        command.execute(args);
                        return;
                    }
                }

            }
            ClientUtils.fancyMessage("Couldn't find that command.");
        }
    }
}
