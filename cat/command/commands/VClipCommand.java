package cat.command.commands;

import cat.command.Command;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class VClipCommand extends Command {
    public VClipCommand() {
        super("VClip", "Clips you vertically.", ".vclip <value>");
    }
    @Override
    public void execute(String[] args){
        if(args.length > 1){
            if(!Pattern.matches("[a-zA-Z]+", args[1])){
                float value = Float.parseFloat(args[1]);
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + value, mc.thePlayer.posZ);
                chat("Clipped "+value+" blocks.");
            }else{
                chat("Cannot convert "+args[1]+" to number!");
            }
        }else{
            chat("Syntax: "+syntax);
        }
    }
}
