package cat.module.modules.movement;

import cat.events.impl.MoveEvent;
import cat.events.impl.PacketEvent;
import cat.events.impl.UpdateEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.ModuleManager;
import cat.module.modules.misc.Disabler;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.util.MovementUtil;
import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class Speed extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Vanilla", true, null, "Vanilla", "Vulcan Timer", "Ground Strafe", "BlocksMC", "Hypixel");

    private final FloatValue speed = new FloatValue("Speed", 0.31f, 0f, 5f, 0.1f, true, __ -> mode.is("Vulcan Timer") || mode.is("Vanilla"));

    public final BooleanValue reverseBoost = new BooleanValue("Reverse Boost", false, true, __ -> mode.is("Vulcan Timer"));

    private final FloatValue hypBoost = new FloatValue("Timer Boost", 1f, 1f, 2f, 0.1f, true, __ -> mode.is("Hypixel"));

    private final FloatValue boost = new FloatValue("Timer Boost", 2f, 0.1f, 5f, 0.1f, true, __ -> mode.is("BlocksMC"));

    public final BooleanValue moveMulti = new BooleanValue("Motion Boost", false, true, __ -> mode.is("BlocksMC"));
    private final FloatValue moveBoost = new FloatValue("Motion", 0.15f, 0.1f, 2f, 0.05f, true, __ -> mode.is("BlocksMC") && moveMulti.get());

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        groundTicks = 0;
        ((Disabler) ModuleManager.getModuleClass(Disabler.class)).targetTimer = 1.0F;
    }

    public Speed() {
        super("Speed", "", ModuleCategory.MOVEMENT, Keyboard.KEY_ADD,"bhop");
    }

    int groundTicks;

    @Subscribe
    public void packet(PacketEvent e) {

    }

    @Subscribe
    public void onUpdate(UpdateEvent e){

        if(mc.thePlayer.onGround) {
            groundTicks++;
        } else {
            groundTicks = 0;
        }

        if(mode.is("Vanilla")) { // @author orangecat
            if (MovementUtil.areMovementKeysPressed()) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                } else {
                    MovementUtil.setSpeed(speed.get());
                }
            } else {
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
            }
        }

        if(mode.is("Ground Strafe")) {
            if (MovementUtil.areMovementKeysPressed()) {
                if (mc.thePlayer.onGround) {
                    MovementUtil.setSpeed((float) (MovementUtil.getBaseMoveSpeed()*speed.get()));
                    mc.thePlayer.jump();
                }
            }
        }

        if(mode.is("Vulcan Timer")) {

            if (MovementUtil.areMovementKeysPressed()) {
                if (mc.thePlayer.onGround) {
                    MovementUtil.setSpeed((float) (MovementUtil.getBaseMoveSpeed()*speed.get()));
                    mc.thePlayer.jump();
                }
            }

            { // timer
                if(mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 0.6F;
                } else {
                    if (mc.thePlayer.motionY < 0) {
                        mc.timer.timerSpeed = reverseBoost.get()? 0.6F : 2F;
                    } else {
                        mc.timer.timerSpeed = reverseBoost.get()? 2F : 0.6F;
                    }
                }
            }
        }

    }

    @Subscribe
    public void motion(UpdatePlayerEvent e) {

        if(mode.is("BlocksMC")) {
            if (MovementUtil.areMovementKeysPressed()) {
                MovementUtil.setSpeed((float) ((MovementUtil.getBaseMoveSpeed() - 0.01) - (Math.random()/2000)), mc.thePlayer.rotationYaw, 0.5D);

                if(mc.thePlayer.onGround && groundTicks != 0)
                    mc.thePlayer.jump();
            }

            mc.timer.timerSpeed = boost.get();
        }

        if(mode.is("Hypixel")) {
            if (MovementUtil.areMovementKeysPressed()) {
                if(mc.thePlayer.onGround && groundTicks != 0)
                    mc.thePlayer.jump();
            }

            ((Disabler) ModuleManager.getModuleClass(Disabler.class)).targetTimer = hypBoost.get();
        }

    }

    @Subscribe
    public void move(MoveEvent e) {

        if(mode.is("BlocksMC")) {

            if(moveMulti.get()) {
                e.x *= moveBoost.get();
                e.z *= moveBoost.get();
            }

        }

    }
}
