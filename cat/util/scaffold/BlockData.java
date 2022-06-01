package cat.util.scaffold;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockData {
    public BlockPos position;
    public EnumFacing face;
    public Vec3 hitVec;

    public BlockData(BlockPos position, EnumFacing face) {
        this.position = position;
        this.face = face;
        this.hitVec = getVec3(position, face);
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing facing) {
        Vec3 vector = new Vec3(pos);
        double random = 0;

        if (facing == EnumFacing.NORTH) {
            vector.xCoord = Minecraft.getMinecraft().thePlayer.posX + random * 0.01;
        } else if (facing == EnumFacing.SOUTH) {
            vector.xCoord = Minecraft.getMinecraft().thePlayer.posX + random * 0.01;
            vector.zCoord += 1.0;
        } else if (facing == EnumFacing.WEST) {
            vector.zCoord = Minecraft.getMinecraft().thePlayer.posZ + random * 0.01;
        } else if (facing == EnumFacing.EAST) {
            vector.zCoord = Minecraft.getMinecraft().thePlayer.posZ + random * 0.01;
            vector.xCoord += 1.0;
        }

        if (facing == EnumFacing.UP) {
            vector.xCoord += random;
            vector.zCoord += random;
            vector.yCoord += 1.0;
        } else {
            vector.yCoord += random;
        }

        return vector;
    }
}
