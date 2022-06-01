package cat.util;

import cat.events.impl.UpdatePlayerEvent;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.*;

import java.util.List;

public class PlayerUtil extends MinecraftInstance {
    private static final MillisTimer verus_timer = new MillisTimer();

    public static Block getBlockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + offsetX, mc.thePlayer.posY + offsetY, mc.thePlayer.posZ + offsetZ)).getBlock();
    }

    public static boolean isInsideBlock() {
        if (mc.thePlayer.ticksExisted < 5)
            return false;

        final EntityPlayerSP player = PlayerUtil.mc.thePlayer;
        final WorldClient world = PlayerUtil.mc.theWorld;
        final AxisAlignedBB bb = player.getEntityBoundingBox();
        for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
                    final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir) &&
                            (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z))))
                                    != null && player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isMouseOver(final float yaw, final float pitch, final Entity target, final float range) {
        final float partialTicks = mc.timer.renderPartialTicks;
        final Entity entity = mc.getRenderViewEntity();
        MovingObjectPosition objectMouseOver;
        Entity mcPointedEntity = null;

        if (entity != null && mc.theWorld != null) {

            mc.mcProfiler.startSection("pick");
            final double d0 = mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);
            final boolean flag = d0 > (double) range;

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = mc.thePlayer.getVectorForRotation(pitch, yaw);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0,
                    vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double) range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }

            mc.mcProfiler.endSection();

            assert objectMouseOver != null;
            return mcPointedEntity == target;
        }

        return false;
    }

    public static boolean isHoldingSword() {
        return mc.thePlayer.ticksExisted > 3 && PlayerUtil.mc.thePlayer.getCurrentEquippedItem() != null && PlayerUtil.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public static MovingObjectPosition getMouseOver(final float yaw, final float pitch, final float range) {
        final float partialTicks = mc.timer.renderPartialTicks;
        final Entity entity = mc.getRenderViewEntity();
        MovingObjectPosition objectMouseOver;
        Entity mcPointedEntity = null;

        if (entity != null && mc.theWorld != null) {

            mc.mcProfiler.startSection("pick");
            final double d0 = mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);
            final boolean flag = d0 > (double) range;

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = mc.thePlayer.getVectorForRotation(pitch, yaw);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0,
                    vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double) range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }

            mc.mcProfiler.endSection();

            assert objectMouseOver != null;
            return objectMouseOver;
        }

        return null;
    }

    public static void damageNormal(float dist) {
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + dist, mc.thePlayer.posZ, false));
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static int damageEdit(UpdatePlayerEvent e, float dist, int progress) {
        switch (progress) {
            case 0:
                e.onGround = false;
                e.y = mc.thePlayer.posY + dist;
                return progress + 1;
            case 1:
                e.onGround = false;
                return progress + 1;
            case 2:
                e.onGround = true;
                return progress + 1;
        }
        return progress;
    }

    public static boolean isOnSameTeam(final EntityLivingBase entity) {
        if (entity.getTeam() != null && PlayerUtil.mc.thePlayer.getTeam() != null) {
            final char c1 = entity.getDisplayName().getFormattedText().charAt(1);
            final char c2 = PlayerUtil.mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }

    public static boolean damageVerus(UpdatePlayerEvent e, int jumps, int[] progress) {
        if (progress[0] <= jumps) {
            e.onGround = false;
            if (verus_timer.hasTicksPassed(12)) {
                if (progress[0] < jumps) {
                    mc.thePlayer.jump();
                }
                progress[0] += 1;
                verus_timer.reset();
            }
        }
        return progress[0] > jumps;
    }
}
