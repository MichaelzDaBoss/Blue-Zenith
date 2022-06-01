package cat.module.modules.movement;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.UpdateEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.util.BypassUtil;
import cat.util.MovementUtil;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class CustomFly extends Module {

    public final ModeValue method = new ModeValue("Method", "Motion", true, null, "Motion", "BoundingBox");

    public final BooleanValue speedSettings = new BooleanValue("Speed Settings", false, true, null);

    public final BooleanValue variatingSpeed = new BooleanValue("Variating Speed", false, true, __ -> speedSettings());

    public final FloatValue nVSpeed = new FloatValue("Speed", 3, 0.1F, 10, 0.1F, true, __ -> !variatingSpeed.get() && speedSettings());

    public final IntegerValue vTicks = new IntegerValue("Variation Ticks", 5, 1, 20, 1, true, __ -> variatingSpeed.get() &&  speedSettings());

    public final BooleanValue vSmooth = new BooleanValue("Smooth Variation", true, true, __ -> variatingSpeed.get() && speedSettings());
    public final FloatValue vSmoothness = new FloatValue("Smoothness", 0.5F, 0.01F, 1F, 0.01F, true, __ -> vSmooth.get() && speedSettings());

    public final FloatValue vSpeed1 = new FloatValue("Speed 1", 3, 0.1F, 10, 0.1F, true, __ -> variatingSpeed.get() && speedSettings());
    public final FloatValue vSpeed2 = new FloatValue("Speed 2", 3, 0.1F, 10, 0.1F, true, __ -> variatingSpeed.get() && speedSettings());

    public final BooleanValue bypassSettings = new BooleanValue("Bypass Settings", false, true, null);

    public final ModeValue groundSpoof = new ModeValue("Ground Spoof", "None", true, __ -> bypassSettings(), "None", "True", "False");
    public final FloatValue motionY = new FloatValue("Y Motion", 0, -1, 1, 0.05F, true, __ -> bypassSettings() && method.is("Motion"));
    public final BooleanValue vertical = new BooleanValue("Vertical", false, true, __ -> bypassSettings() && method.is("Motion"));

    public final BooleanValue damage = new BooleanValue("Damage", false, true, __ -> bypassSettings());

    public final ModeValue damagemode = new ModeValue("Damage", "Verus", true, __ -> bypassSettings() && damage.get(), "Verus", "OldWD");

    int startingTick;
    boolean altSpeed;
    double speed;

    boolean speedSettings() {
        return speedSettings.get();
    }

    boolean bypassSettings() {
        return bypassSettings.get();
    }

    public CustomFly() {
        super("CustomFly", "insane bypass", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)
            return;

        startingTick = mc.thePlayer.ticksExisted % vTicks.get();
        altSpeed = false;
        speed = 0;

        if(damage.get()) {

            switch(damagemode.get()) {

                case "Verus":
                    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.01, mc.thePlayer.posZ, false));
                    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    break;

                case "OldWD":
                    BypassUtil.damageOldWD();
                    break;

            }

        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {

        if(variatingSpeed.get()) {
            if((mc.thePlayer.ticksExisted - startingTick) % vTicks.get() == 0) {
                altSpeed = !altSpeed;
            }

            if(vSmooth.get()) {
                if(altSpeed) {
                    speed = animate(vSpeed2.get(), speed, vSmoothness.get());
                } else {
                    speed = animate(vSpeed1.get(), speed, vSmoothness.get());
                }
            } else {
                if(altSpeed) {
                    speed = vSpeed2.get();
                } else {
                    speed = vSpeed1.get();
                }
            }
        } else {
            if(vSmooth.get()) {
                speed = animate(nVSpeed.get(), speed, 0.5F);
            } else {
                speed = nVSpeed.get();
            }
        }

    }

    @Subscribe
    public void onBlockBB(BlockBBEvent e){
        if(mc.thePlayer == null)
            return;

        if(method.is("BoundingBox")) {
            if(e.pos.getY() < mc.thePlayer.posY){
                e.blockBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(e.pos.getX(), e.pos.getY(), e.pos.getZ());
                if(speed != 0){
                    MovementUtil.setSpeed((float) speed);
                }
            }
        }
    }

    @Subscribe
    public void onPlayerUpdate(UpdatePlayerEvent e) {

        if(groundSpoof.is("True")) {
            e.onGround = true;
        } else if(groundSpoof.is("False")) {
            e.onGround = false;
        }

        if(method.is("Motion")) {
            if (MovementUtil.areMovementKeysPressed()) {
                MovementUtil.setSpeed((float) speed);
            } else {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }

            if(vertical.get()) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = speed / 2f;
                } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -speed / 2f;
                } else {
                    mc.thePlayer.motionY = motionY.get();
                }
            } else {
                mc.thePlayer.motionY = motionY.get();
            }
        }

    }

    public double animate(double target, double current, double speed) {
        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }

        return current;
    }

}
