package cat.module.modules.movement;

import cat.events.impl.MoveEvent;
import cat.events.impl.PacketEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import static cat.util.MovementUtil.stopMoving;

@SuppressWarnings("SpellCheckingInspection")
public class LongJump extends Module {
    final double[] jumpValues = new double[]{0.42F, 0.33319999363422365, 0.24813599859094576, 0.16477328182606651, 0.08307781780646721, 0.0030162615090425808};

    public ModeValue mode = new ModeValue("Mode", "OldVerus", true, null, "OldVerus", "Vulcan", "Hypixel");
    public ModeValue bypassMode = new ModeValue("DamageMode", "Old", false, i -> mode.get().equals("OldVerus"), "None", "Old", "New", "Hypixel");

    public FloatValue timer = new FloatValue("Timer", 1, 1, 2, 0.1f, true, __ -> mode.is("Hypixel"));

    public LongJump(){
        super("LongJump", "", ModuleCategory.MOVEMENT);
    }

    private int ticks, offGroundTicks;
    private boolean beingDmged, receivedDamage, boosted;

    private boolean jumped, reset, glide;
    private int stage, i;
    private float oPositionY;
    private double moveSpeed, lastDist;

    @Override
    public String getTag() {
        return mode.get();
    }

    public final int[] b = new int[]{0};
    public final int jumps = 4;
    private int c = 0;
    private long f = System.currentTimeMillis();
    private boolean maccacokkk = false; // thx levzzz

    @Override
    public void onEnable() {
        receivedDamage = boosted = false;
        ticks = offGroundTicks = 0;

        oPositionY = (float) mc.thePlayer.posY;

        if(mode.is("Vulcan")) {
            if(!mc.thePlayer.onGround)
                NotificationManager.addNoti("LongJump", "Can't damage mid air!", NotificationType.WARNING, 1000L);
        }

        if(mode.is("OldVerus")) {
            maccacokkk = false;
            if (this.mc.thePlayer != null && !bypassMode.get().equals("None")) {
                if (bypassMode.get().equals("Old")) {
                    PlayerUtil.damageNormal((float) Math.PI);
                    b[0] = -1;
                } else {
                    b[0] = 0;
                }
                c = 0;
            }
        }

    }

    @Subscribe
    public void onMove(MoveEvent e){

        if(mode.is("OldVerus")) {
            if(b[0] <= jumps && bypassMode.get().equals("New")){
                e.x = 0;
                e.z = 0;
            }
        }

        if(mode.is("Vulcan")) {
            e.x = 0;
            e.z = 0;
        }

        if(mode.is("Hypixel")) {
            switch (offGroundTicks) {
                case 0:
                    moveSpeed = MovementUtil.getBaseMoveSpeed() * 1.7;
                    e.y = (mc.thePlayer.motionY = 0.424F);
                    break;

                case 1:
                    mc.thePlayer.motionY += 0.02;
                    break;

                case 2:
                case 3:
                    moveSpeed = lastDist * 0.98;
                    break;

                default:
                    moveSpeed = lastDist * (mc.thePlayer.fallDistance < 0.25 ? 0.95 : 0.91);

                    if (!(PlayerUtil.getBlockRelativeToPlayer(0, -0.1, 0) instanceof BlockAir))
                        moveSpeed = lastDist * 0.5;
                    break;
            }

            if (mc.thePlayer.fallDistance > 0) {
                if (mc.thePlayer.fallDistance < 0.25)
                    mc.thePlayer.motionY = -0.0025;
                else
                    mc.thePlayer.motionY += 0.02;
            }

            mc.timer.timerSpeed = (float) (timer.get() == 1 ? 1 : timer.get() - (Math.random() / 100));

            MovementUtil.setMoveEventSpeed(e, moveSpeed - (Math.random() / 500));
        }

    }

    @Subscribe
    public void packet(PacketEvent e){

        if(mode.is("Vulcan")) {
            if (!receivedDamage && e.packet instanceof C03PacketPlayer)
                e.cancel();
        }

    }

    public boolean canFly(){
        return bypassMode.get().equals("None") || bypassMode.get().equals("Old") || (bypassMode.get().equals("New") && b[0] > jumps);
    }

    @Subscribe
    public void onPlayerUpdate(UpdatePlayerEvent e){
        if(e.isPre())
            ++ticks;

        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }


        if(mode.is("Vulcan")) {
            if (mc.thePlayer.onGround && ticks == 5 && !beingDmged) {
                for (int i = 0; i < 6; i++) {
                    double position = mc.thePlayer.posY;
                    for (final double value : jumpValues) {
                        position += value;

                        PacketUtil.sendSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, position, mc.thePlayer.posZ, false));
                    }
                }
                PacketUtil.sendSilent(new C03PacketPlayer(true));
                e.onGround = true;

                beingDmged = true;
            }

            if (mc.thePlayer.hurtTime > 0)
                receivedDamage = true;

            if (receivedDamage) {
                mc.timer.timerSpeed = 1;

                if (!boosted) {
                    float motion = 0.6F;

                    if (PlayerUtil.getBlockRelativeToPlayer(0, -0.5, 0).getUnlocalizedName().contains("bed"))
                        motion = 1.5F;

                    mc.thePlayer.motionY = motion - (Math.random() / 100);
                    MovementUtil.setSpeed((float) (9.5 - (Math.random() / 500)));
                    boosted = true;
                } else if (offGroundTicks == 1)
                    MovementUtil.setSpeed((float) (0.5 - (Math.random() / 500)));

                if (mc.thePlayer.fallDistance > 0)
                    mc.thePlayer.motionY += 0.02 - (Math.random() / 10000);
            }
        }

        if(mode.is("OldVerus")) {
            if (bypassMode.get().equals("New") && !PlayerUtil.damageVerus(e, jumps, b)) {
                return;
            }
            if (mc.thePlayer.hurtTime == 9) {
                c = 0;
                maccacokkk = true;
            }
            if (!maccacokkk || !canFly()) return;
            if (c <= 6) {
                mc.thePlayer.jump();
                c++;
                if (c == 6) {
                    f = System.currentTimeMillis();
                }
            }
            long boostTime = 700;
            float lol = (boostTime - MathUtil.inRange((System.currentTimeMillis() - f), 0, boostTime)) / boostTime;
            if (lol > 0.1) {
                MovementUtil.setSpeed(lol / BypassUtil.bypass_value);
            }
            if (mc.thePlayer.onGround && lol == 0 && c > 3 && maccacokkk) {
                setState(false);
            }
        }

    }

    @Override
    public void onDisable() {
        stopMoving();
        mc.thePlayer.speedInAir = 0.02f;
        mc.thePlayer.jumpMovementFactor = 0.02F;
        moveSpeed = lastDist = 0;
        mc.timer.timerSpeed = 1;
        jumped = false;
        beingDmged = false;
    }
}
