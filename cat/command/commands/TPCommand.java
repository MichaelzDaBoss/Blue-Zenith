package cat.command.commands;

import cat.command.Command;
import cat.util.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class TPCommand extends Command {
    public TPCommand() {
        super("TP", "Teleports you.", ".tp <x> <y> <z>");
    }

    @Override
    public void execute(String[] args){
        double[] pos = new double[3];
        if(args.length > 4){
            for(int i = 0; i < args.length-2; i++) {
                if (!Pattern.matches("[a-zA-Z]+", args[i+1])) {
                    pos[i] = Double.parseDouble(args[i+1]);
                } else {
                    chat("Cannot convert " + args[i+1] + " to numbers!");
                    return;
                }
            }
            chat("Set position successfully.");
            mc.thePlayer.setPositionAndUpdate(pos[0], pos[1], pos[2]);
            for(int i = 0; i < Integer.parseInt(args[3]); i++) {
                PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(pos[0], pos[1], pos[2], mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
            }
        }else{
            chat("Syntax: "+syntax);
        }
    }
}
