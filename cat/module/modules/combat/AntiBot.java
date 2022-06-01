package cat.module.modules.combat;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.ModeValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {

    public final ModeValue mode = new ModeValue("Mode", "BlocksMC", true, null, "BlocksMC");

    public AntiBot() {
        super("AntiBot", "", ModuleCategory.COMBAT);
    }

    public boolean isBot(Entity entity) {

        if(mode.is("BlocksMC")) {
            if(entity.getName().equalsIgnoreCase("SHOP") || entity.getName().equalsIgnoreCase("UPGRADES")) {
                return true;
            }
        }

        return false;
    }

}
