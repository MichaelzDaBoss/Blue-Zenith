package cat.module.modules.movement;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.MoveEvent;
import cat.events.impl.PacketEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.BypassUtil;
import cat.util.MillisTimer;
import cat.util.MovementUtil;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Timer;

public class Fly extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Vanilla", true, null, "Vanilla", "Vulcan", "AC.GAY", "Minemora" /*, "Ghostly" */);

    public final FloatValue speed = new FloatValue("Speed", 5, 0.1F, 10, 0.1F, true, __ -> isSpeedVisible());

    public final BooleanValue vulcanFast = new BooleanValue("Alternative", false, true, __ -> mode.is("Vulcan"));

    public final FloatValue timerSpeed = new FloatValue("Timer Speed", 1.5F, 1, 5, 0.1F, true, __ -> mode.is("AC.GAY"));
    public final IntegerValue timerTime = new IntegerValue("Timer Time", 1190, 100, 3000, 5, true, __ -> mode.is("AC.GAY"));

    int wdStage;
    int wdY;
    int wdTicks;
    double wdMoveSpeed;
    double wdLastDist;

    boolean vulcanWaitFlag = false;
    boolean vulcanCanGlide = false;
    int vulcanTicks = 0;

    MillisTimer wdTimer = new MillisTimer();

    public Fly() {
        super("Fly", "", ModuleCategory.MOVEMENT);
    }

    @Override
    public String getTag() {
        return mode.get();
    }

    @Override
    public void onDisable() {
        EntityPlayerSP player = mc.thePlayer;
        mc.timer.timerSpeed = 1.0F;
        player.stepHeight = 0.625F;
        player.motionX = 0.0D;
        player.motionZ = 0.0D;

        if (mode.is("AC.GAY")) {
            player.setPosition(player.posX, player.posY + wdY, player.posZ);
        }
    }

    @Override
    public void onEnable() {
        wdTimer.reset();
        wdMoveSpeed = 0.0D;
        wdStage = 0;
        wdY = 0;
        wdTicks = 0;
        wdLastDist = 0.0D;

        if(mode.is("Ghostly")) {
            if(mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42;
            }
        }

        if(mode.is("Vulcan")) {
            if (mc.thePlayer.onGround) {
                MovementUtil.clip(0f, -0.1f);
                vulcanWaitFlag = true;
                vulcanCanGlide = false;
                vulcanTicks = 0;
                mc.timer.timerSpeed = 0.1f;
            } else {
                vulcanWaitFlag = false;
                vulcanCanGlide = true;

                NotificationManager.addNoti("Fly", "You need to be on the ground!", NotificationType.WARNING, 1500L);
            }
        }

        if(mode.is("Minemora")) {
            mc.timer.timerSpeed = 0.85f;
            NotificationManager.publish("Fly", "You can fly!", NotificationType.INFO, 15000L);
        }
    }

    @Subscribe
    public void collide(BlockBBEvent event) {
   //     if(mode.is("Ghostly")) {
   //         if (event.getBlock() instanceof BlockAir) {
   //             if (mc.thePlayer.isSneaking())
   //                 return;
   //             double x = event.getX();
   //             double y = event.getY();
   //             double z = event.getZ();
   //             if (y < mc.thePlayer.posY) {
   //                 event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
   //             }
   //         }
   //     }
    }

    @Subscribe
    public void onMove(MoveEvent move) {
        EntityPlayerSP player = mc.thePlayer;
        GameSettings gameSettings = mc.gameSettings;

        if(mode.is("Ghostly")) {
            setMotion(move, speed.get());
        }

        if(mode.is("Minemora")) {
            double multi = 0.9D + (Math.random() * 0.2D);

            player.motionX *= multi;
            player.motionZ *= multi;
        }

        if(mode.is("AC.GAY")) {
            if (player.isMoving()) {
                switch(wdStage) {
                    case 0:
                        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                            BypassUtil.damageOldWD();
                            wdMoveSpeed = 0.5D * speed.get();
                        }
                        break;
                    case 1:
                        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                            move.y = player.motionY = MovementUtil.getJumpBoostModifier(0.39999994D);
                        }

                        wdMoveSpeed *= 2.149D;
                        break;
                    case 2:
                        wdMoveSpeed = 1.3D * speed.get();
                        break;
                    default:
                        wdMoveSpeed = wdLastDist - wdLastDist / 159.0D;
                }

                setSpeed(move, Math.max(wdMoveSpeed, MovementUtil.getBaseMoveSpeed()));
                ++wdStage;
            }
        }

    }

    @Subscribe
    public void onPacket(PacketEvent e) {

        if(mode.is("AC.GAY")) {
            if(wdStage == 0) {
                e.cancel();
            }
        }

        if(mode.is("Vulcan")) {
            if(e.packet instanceof S08PacketPlayerPosLook && vulcanWaitFlag) {
                vulcanWaitFlag = false;
                mc.thePlayer.setPosition(((S08PacketPlayerPosLook) e.packet).getX(), ((S08PacketPlayerPosLook) e.packet).getY(), ((S08PacketPlayerPosLook) e.packet).getZ());
                PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                e.cancel();

                if(vulcanFast.get()) {
                    MovementUtil.clip(0f, 0.127318f);
                    MovementUtil.clip(3.7f, 3.425559f);
                    MovementUtil.clip(3.54f, 3.14285f);
                    MovementUtil.clip(3.4f, 2.88522f);
                } else {
                    MovementUtil.clip(0.127318f, 0f);
                    MovementUtil.clip(3.425559f, 3.7f);
                    MovementUtil.clip(3.14285f, 3.54f);
                    MovementUtil.clip(2.88522f, 3.4f);
                }

                vulcanCanGlide = true;
            }
        }

    }

    @Subscribe
    public void onUpdatePlayer(UpdatePlayerEvent event) {
        EntityPlayerSP player = mc.thePlayer;
        Timer timer = mc.timer;
        GameSettings gameSettings = mc.gameSettings;
        double offset;

        if(mode.is("Minemora")) {
            if(mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionY = -(1/64D)*4;
            } else {
                mc.thePlayer.motionY = (1/64D)*3;
            }
        }

        if(mode.is("Vulcan")) {
            if(event.isPre()) {
                mc.timer.timerSpeed = 1f;
                mc.thePlayer.motionY = -(vulcanTicks % 2 == 0? 0.17 : 0.10);

                if(vulcanTicks == 0) {
                    mc.thePlayer.motionY = -0.07;
                }

                vulcanTicks++;
            }
        }

        if(mode.is("AC.GAY")) {
            if (!wdTimer.hasTimeReached(timerTime.get())) {
                mc.timer.timerSpeed = timerSpeed.get();
            } else {
                mc.timer.timerSpeed = 1.0F;
            }


            if (event.isPre()) {
                if (wdStage > 2) {
                    player.motionY = 0.0D;
                }

                if (wdStage > 2) {
                    player.setPosition(player.posX, player.posY - 0.003D, player.posZ);
                    ++wdTicks;
                    switch(wdTicks) {
                        case 1:
                            wdY *= -0.949999988079071D;
                            break;
                        case 2:
                        case 3:
                        case 4:
                            wdY += 3.25E-4D;
                            break;
                        case 5:
                            wdY += 5.0E-4D;
                            wdTicks = 0;
                    }

                    event.y = (player.posY + wdY);
                }
            } else if (wdStage > 2) {
                player.setPosition(player.posX, player.posY + 0.003D, player.posZ);
            }
        }

        if(mode.is("Vanilla")) {
            if (MovementUtil.areMovementKeysPressed()) {
                MovementUtil.setSpeed(speed.get());
            } else {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY = speed.get()/2f;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.thePlayer.motionY = -speed.get()/2f;
            } else {
                mc.thePlayer.motionY = 0;
            }
        }

        if (event.isPre()) {
            offset = player.posX - player.prevPosX;
            double zDif = player.posZ - player.prevPosZ;
            wdLastDist = Math.sqrt(offset * offset + zDif * zDif);
        }

    }

    private boolean isSpeedVisible() {
        return mode.is("Vanilla") || mode.is("Ghostly");
    }

    public void setSpeed(MoveEvent moveEvent, double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, (double)mc.thePlayer.movementInput.moveStrafe, (double)mc.thePlayer.movementInput.moveForward);
    }

    public void setSpeed(MoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0.0D) {
            if (pseudoStrafe > 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? -45 : 45);
            } else if (pseudoStrafe < 0.0D) {
                yaw = pseudoYaw + (float)(pseudoForward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0.0D) {
                forward = 1.0D;
            } else if (pseudoForward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        double mz = Math.sin(Math.toRadians(yaw + 90.0F));
        moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public void setMotion(MoveEvent event, double moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;

        double rotationYaw = mc.thePlayer.rotationYaw;

        if (moveForward == 0.0D && moveStrafe == 0.0D) {
            event.x = (0);
            event.z = (0);
        } else {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += moveForward > 0.0D ? -45 : 45;
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += moveForward > 0.0D ? 45 : -45;
                }
                moveStrafe = 0.0D;
                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            event.x = (moveForward * moveSpeed * cos
                    + moveStrafe * moveSpeed * sin);
            event.z = (moveForward * moveSpeed * sin
                    - moveStrafe * moveSpeed * cos);
        }
    }

}
