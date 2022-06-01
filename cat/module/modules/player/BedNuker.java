package cat.module.modules.player;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;
import cat.util.ClientUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public class BedNuker extends Module {
    private final FloatValue range = new FloatValue("Range", 3f, 1f, 5f, 0.5f, true, null);
    public BedNuker() {
        super("BedNuker", "", ModuleCategory.PLAYER);
    }
    BlockPos pos = null;
    boolean novolean = false;

    @Subscribe
    public void onUpdatePlayer(UpdatePlayerEvent e) {
        final float r = range.get();

        for (double x = (mc.thePlayer.posX - r); x < (mc.thePlayer.posX + r); x++) {
            for (double y = (mc.thePlayer.posY - r); y < (mc.thePlayer.posY + r); y++) {
                for (double z = (mc.thePlayer.posZ - r); z < (mc.thePlayer.posZ + r); z++) {
                    pos = new BlockPos(x, y, z);
                    final Block block = mc.theWorld.getBlockState(pos).getBlock();
                    if (block instanceof BlockBed) {
                        if (mc.thePlayer.swingProgress == 0f) {
                            mc.thePlayer.swingItem();
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                                break;
                            // mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                        }
                    }
                }
            }
        }
    }
    //sorry for breaking it :(
    //@Subscriber
    public void onBlockBB(BlockBBEvent e){
        if(mc.thePlayer.getDistanceSq(e.pos) <= 3 && mc.theWorld.getBlockState(e.pos).equals(Blocks.bed.getDefaultState()) && pos == null){
            pos = e.pos;
            novolean = true;
            ClientUtils.fancyMessage("detected funny block");
        }else if(e.pos == pos){
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
            pos = null;
        }
    }
}
