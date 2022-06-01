package cat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;

public class BypassUtil extends MinecraftInstance {

    public static float bypass_value = 0.1536f;

    public static void NCPDisabler(){
        mc.getFramebuffer().unbindFramebuffer();
        mc.thePlayer = null;
        mc.fontRendererObj = null;
    }

    public static boolean shouldNotRun() {
        if (mc.thePlayer == null) {
            return true;
        }
        if(mc.isSingleplayer()) {
            return true;
        }
        if (mc.thePlayer.inventory == null) {
            return true;
        }
        if (mc.thePlayer.inventory.hasItem(Items.compass)) {
            return true;
        }
        return mc.thePlayer == null || mc.thePlayer.ticksExisted <= 5;
    }

    public static void damageOldWD() {
        double offset = 0.060100000351667404D;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        EntityPlayerSP player = mc.thePlayer;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        for(int i = 0; (double)i < (double)getMaxFallDist() / 0.05510000046342611D + 1.0D; ++i) {
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.060100000351667404D, z, false));
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 5.000000237487257E-4D, z, false));
            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.004999999888241291D + 6.01000003516674E-8D, z, false));
        }

        PacketUtil.sendSilent(new C03PacketPlayer(true));
    }

    public static float getMaxFallDist() {
        PotionEffect potioneffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        int f = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        return (float)(mc.thePlayer.getMaxFallHeight() + f);
    }

    public static Minecraft mc = Minecraft.getMinecraft();

    public static List<EntityLivingBase> getLivingEntities() {
        return Arrays.asList(
                ((List<Entity>) mc.theWorld.loadedEntityList).stream()
                        .filter(entity -> entity instanceof EntityLivingBase)
                        .filter(entity -> entity != mc.thePlayer)
                        .map(entity -> (EntityLivingBase) entity)
                        .toArray(EntityLivingBase[]::new)
        );
    }

    public static void pulsiveDeath() {
        PacketUtil.sendSilent(new C03PacketPlayer(false));
        PacketUtil.sendSilent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, -99, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
    }

}
