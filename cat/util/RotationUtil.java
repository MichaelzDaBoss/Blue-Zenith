package cat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class RotationUtil extends MinecraftInstance {
    //TODO: fix this shid
    public static boolean isFacingPlayer(float yaw, float pitch) {
        return false;
    }

    public static float[] getRotations(final BlockPos pos, final EnumFacing facing) {
        return getRotations(pos.getX(), pos.getY(), pos.getZ(), facing);
    }

    public static boolean isVisibleFOV(final Entity e, final float fov) {
        return ((Math.abs(getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f) : (Math.abs(getRotations(e)[0] - mc.thePlayer.rotationYaw) % 360.0f)) <= fov;
    }

    public static float[] getRotations(final double x, final double y, final double z, final EnumFacing facing) {
        final EntityPig temp = new EntityPig(mc.theWorld);
        temp.posX = x + 0.5;
        temp.posY = y + 0.5;
        temp.posZ = z + 0.5;

        temp.posX += facing.getDirectionVec().getX() * 0.5;
        temp.posY += facing.getDirectionVec().getY() * 0.5;
        temp.posZ += facing.getDirectionVec().getZ() * 0.5;

        return getRotations(temp);
    }

    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - mc.thePlayer.posX;
        final double diffZ = entity.posZ - mc.thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + (elb.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static boolean isValidRangeForNCP(final Entity entity, double range) {
        if (entity == null || mc.thePlayer == null)
            return false;

        final Location dRef = new Location(entity.posX, entity.posY, entity.posZ);
        final Location pLoc = new Location(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        final double height = entity instanceof EntityLivingBase ? entity.getEyeHeight() : 1.75;

        // Refine y position.

        final double pY = pLoc.getY() + mc.thePlayer.getEyeHeight();
        final double dY = dRef.getY();
        if (pY <= dY) ; // Keep the foot level y.
        else if (pY >= dY + height) dRef.setY(dY + height); // Highest ref y.
        else dRef.setY(pY); // Level with damaged.

        Vec3 temp = new Vec3(pLoc.getX(), pY, pLoc.getZ());
        final Vec3 pRel = dRef.toVector().subtract(temp); //

        // Distance is calculated from eye location to center of targeted. If the player is further away from their target
        // than allowed, the difference will be assigned to "distance".
        final double lenpRel = pRel.lengthVector();

        double violation = lenpRel - range;

        if (violation > 0) {
            return false;
        }

        return true;
    }

    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean predict) {

        VecRotation vecRotation = null;

        for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);

                    final VecRotation currentVec = new VecRotation(vec3, rotation);

                    if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                        vecRotation = currentVec;
                }
            }
        }

        return vecRotation;
    }

    public static Rotation toRotation(final Vec3 vec, final boolean predict) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY +
                mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        if (predict)
            eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    private static float getAngleDifference(float a, float b) {
        return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
    }

    public static double getRotationDifference(Rotation a, Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), (a.getPitch() - b.getPitch()));
    }

    public static Rotation serverRotation = new Rotation(0, 0);

    public static double getRotationDifference(Rotation rotation) {
        return getRotationDifference(rotation, serverRotation);
    }

    public static class VecRotation {
        Vec3 vec;
        Rotation rotation;

        public VecRotation(Vec3 vec, Rotation rotation) {
            this.vec = vec;
            this.rotation = rotation;
        }

        public Rotation getRotation() {
            return this.rotation;
        }

        public Vec3 getVec() {
            return vec;
        }
    }

    public static class Rotation {
        float yaw, pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getYaw() {
            return this.yaw;
        }

        public float getPitch() {
            return this.pitch;
        }

        public void toPlayer(EntityPlayer player) {
            if (Float.isNaN(yaw) || Float.isNaN(pitch))
                return;

            fixedSensitivity(mc.gameSettings.mouseSensitivity);

            player.rotationYaw = yaw;
            player.rotationPitch = pitch;
        }

        public void fixedSensitivity(Float sensitivity) {
            float f = sensitivity * 0.6F + 0.2F;
            float gcd = f * f * f * 1.2F;

            yaw -= yaw % gcd;
            pitch -= pitch % gcd;
        }

    }

    public static float[] getRotation(Entity a1) {
        double v1 = a1.posX - mc.thePlayer.posX;
        double v3 = a1.posY + (double) a1.getEyeHeight() * 0.9 - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double v5 = a1.posZ - mc.thePlayer.posZ;

        double v7 = MathHelper.ceiling_float_int((float) (v1 * v1 + v5 * v5));
        float v9 = (float) (Math.atan2(v5, v1) * 180.0 / 3.141592653589793) - 90.0f;
        float v10 = (float) (-(Math.atan2(v3, v7) * 180.0 / 3.141592653589793));
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(v9 - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(v10 - mc.thePlayer.rotationPitch)};
    }

    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        return (SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0F) * 0.15F;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    /**
     * Smooths the current rotation using the last for it to make aura harder to flag.
     *
     * @param rotations     Current rotations.
     * @param lastRotations Last rotations.
     * @return Current rotation smoothed according to last.
     */
    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }
}
