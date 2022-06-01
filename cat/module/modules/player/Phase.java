package cat.module.modules.player;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.MoveButtonEvent;
import cat.events.impl.PacketEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.util.MovementUtil;
import cat.util.PacketUtil;
import cat.util.PlayerUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Phase extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Normal", true, null, "Vanilla", "Normal", "Forward", "Vertical", "Sand", "Clip");
    public final ModeValue direction = new ModeValue("Direction", "Up", true, __ -> mode.is("Clip"), "Up", "Down");
    public final BooleanValue sneakOnly = new BooleanValue("Sneak Only", false, true, __ -> !mode.is("Vanilla") && !mode.is("Sand") && !mode.is("Clip"));
    public final FloatValue flySpeed = new FloatValue("Fly Speed", 2F, 0.1F, 10F, 0.1F, true, __ -> mode.is("Vanilla") || mode.is("Sand"));

    public Phase() {
        super("Phase", "", ModuleCategory.PLAYER, "noclip", "clip");
    }

    private int onGroundTicks;
    private boolean canClip;

    @Subscribe
    public void onMotion(UpdatePlayerEvent event) {
        if(event.isPre()) {
            final double rotation = Math.toRadians(mc.thePlayer.rotationYaw);
            final double x;
            final double z;

            if (mc.thePlayer.onGround)
                onGroundTicks++;
            else
                onGroundTicks = 0;

            mc.thePlayer.noClip = !mode.is("Hypixel");

            switch (mode.get()) {
                case "Normal":
                    x = Math.sin(rotation) * 0.005;
                    z = Math.cos(rotation) * 0.005;

                    if (!sneakOnly.get() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                        if (mc.thePlayer.isCollidedHorizontally) {
                            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z, true));
                            mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        }

                        if (PlayerUtil.isInsideBlock()) {
                            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + 1.5 * Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)), mc.thePlayer.posY, mc.thePlayer.posZ + 1.5 * Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F)), true));
                            MovementUtil.setSpeed((float) (MovementUtil.getBaseMoveSpeed() / 2));
                        }
                    }
                    break;

                case "Forward":
                    x = Math.sin(rotation) * 0.5;
                    z = Math.cos(rotation) * 0.5;

                    if (!sneakOnly.get() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                        if (mc.thePlayer.isCollidedHorizontally)
                            mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                    }
                    break;

                case "Vertical":
                    x = Math.sin(rotation) * 0.5;
                    z = Math.cos(rotation) * 0.5;

                    if (!sneakOnly.get() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                        if (mc.thePlayer.isCollidedHorizontally) {
                            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z, true));
                            PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 11, mc.thePlayer.posZ, false));
                            mc.thePlayer.setPosition(mc.thePlayer.posX - x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        }

                        if (PlayerUtil.isInsideBlock()) {
                            MovementUtil.setSpeed((float) (MovementUtil.getBaseMoveSpeed() / 2));
                        }
                    }
                    break;

                case "Sand":
                case "Vanilla":
                    if (PlayerUtil.isInsideBlock()) {
                        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? flySpeed.get() : mc.gameSettings.keyBindSneak.isKeyDown() ? -flySpeed.get() : 0;

                        if (mc.thePlayer.isMoving())
                            MovementUtil.setSpeed(flySpeed.get());
                        else
                            MovementUtil.setSpeed(0);
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onBlockCollide(final BlockBBEvent event) {
        if (PlayerUtil.isInsideBlock() && (mode.is("Vanilla") || mode.is("Sand")))
            event.blockBB = (null);
    }

    @Subscribe
    public void onMoveButton(final MoveButtonEvent event) {
        if (mode.is("Vanilla") || mode.is("Sand") || ((sneakOnly.get() && sneakOnly.isVisible())))
            event.setSneak(false);
    }

    @Override
    public void onEnable() {
        switch (mode.get()) {
            case "Clip":
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (direction.is("Up") ? 3 : -3), mc.thePlayer.posZ);
                toggle();
                break;

            case "Vanilla":
                PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + Double.POSITIVE_INFINITY,
                        mc.thePlayer.posY + Double.POSITIVE_INFINITY, mc.thePlayer.posZ + Double.POSITIVE_INFINITY, true));
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ);
                break;
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.noClip = false;
        onGroundTicks = 0;
    }

}
