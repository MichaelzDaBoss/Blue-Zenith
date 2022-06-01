package cat.util;

import cat.events.impl.MoveEvent;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class MovementUtil extends MinecraftInstance {

    public static void setMoveEventSpeed(final MoveEvent moveEvent, final double moveSpeed) {
        setMoveEvent(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public static void setMoveEvent(final MoveEvent moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        final double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        final double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.x = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.z = (forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static float currentSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static boolean areMovementKeysPressed(){
        return mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F;
    }

    public static void setSpeed(float f){
        if(mc.thePlayer == null){
            return;
        }

        if(f == 0){
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            return;
        }

        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (mc.thePlayer.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.thePlayer.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        float yaw = (float) Math.toRadians(rotationYaw);
        mc.thePlayer.motionX = -Math.sin(yaw) * f;
        mc.thePlayer.motionZ = Math.cos(yaw) * f;
    }

    public static void setSpeed(float f, float yaw, double strafe) {
        if(mc.thePlayer == null){
            return;
        }

        if(f == 0){
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            return;
        }

        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.moveForward < 0.0F)
            rotationYaw += (180.0F);
        float forward = 1.0F;
        if (mc.thePlayer.moveForward < 0.0F) {
            forward = (float) (-0.5F*strafe);
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = (float) (0.5F*strafe);
        }
        if (mc.thePlayer.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.thePlayer.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        float yawA = (float) Math.toRadians(rotationYaw);
        mc.thePlayer.motionX = -Math.sin(yawA) * f;
        mc.thePlayer.motionZ = Math.cos(yawA) * f;
    }

    public static void setSpeed(float f, float yaw){
        if(mc.thePlayer == null || !(mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F)){
            return;
        }

        float rotationYaw = yaw;

        if (mc.thePlayer.moveForward < 0.0F)
            rotationYaw += 180.0F;

        float forward = 1.0F;

        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;

        if (mc.thePlayer.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        float yawR = (float) Math.toRadians(rotationYaw);
        mc.thePlayer.motionX = -Math.sin(yawR) * f;
        mc.thePlayer.motionZ = Math.cos(yawR) * f;
    }

    public static double getNormalSpeed() {
        double speed = 0.2875D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            speed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return speed;
    }

    public static void stopMoving() {
        mc.thePlayer.motionX *= 0D;
        mc.thePlayer.motionZ *= 0D;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float)(amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double)(mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static void clip(float dist, float y) {
        float yaw = (float) Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * dist;
        double z = Math.cos(yaw) * dist;
        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
    }

}
