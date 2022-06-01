package cat.command.commands;

import cat.BlueZenith;
import cat.command.Command;

import java.util.Arrays;
@SuppressWarnings("unused")
public final class HelpCommand extends Command {

    public HelpCommand() {
        super("Help", "Figure it out", "help (command name)");
    }

    @Override
    public void execute(String[] args) {
        if(args.length > 1) {
            for(Command c : BlueZenith.commandManager.commands) {
                if(c.name.equalsIgnoreCase(args[1])) {
                    chat("");
                    chat(c.name);
                    chat(c.description);
                    chat(c.syntax);
                    chat("Aliases: " + Arrays.toString(c.pref));
                    return;
                }
                for(String a : c.pref) {
                    if(a.equalsIgnoreCase(args[1])) {
                        chat("");
                        chat(c.name);
                        chat(c.description);
                        chat(c.syntax);
                        chat("Aliases: " + Arrays.toString(c.pref));
                        return;
                    }
                }
            }
            chat("Couldn't find that command!");
        } else
        BlueZenith.commandManager.commands.forEach(a -> { if(a.description.contains("Auto-generated")) return; chat(" ");chat(a.name + " : " + a.description); chat(a.syntax); });
    }
}
