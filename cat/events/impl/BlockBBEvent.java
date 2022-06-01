package cat.events.impl;

import cat.events.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockBBEvent extends Event {
    public BlockPos pos;
    public Block block;
    public AxisAlignedBB blockBB;
    public BlockBBEvent(BlockPos pos, Block block, AxisAlignedBB blockBB){
        this.pos = pos;
        this.block = block;
        this.blockBB = blockBB;
    }
}
