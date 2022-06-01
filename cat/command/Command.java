package cat.command;

import cat.util.ClientUtils;
import cat.util.MinecraftInstance;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class Command extends MinecraftInstance {
    public String name;
    public String[] pref;
    public String description;
    public String syntax;
    public Command(String name, String description, String syntax, String... pref){
        this.name = name;
        this.pref = pref;
        this.description = description;
        this.syntax = syntax;
    }
    public void changedSound(){
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("mob.cat.purreow")));
    }
    public void execute(String[] args){

    }
    public void chat(String f){
        ClientUtils.displayChatMessage("§3§l[§r§bBlue Zenith§3§l] §r§9"+f);
    }
}
