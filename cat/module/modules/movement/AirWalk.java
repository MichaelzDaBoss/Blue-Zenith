package cat.module.modules.movement;

import cat.events.impl.BlockBBEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.FloatValue;
import cat.util.MovementUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("unused")
public class AirWalk extends Module {
    public AirWalk() {
        super("AirWalk", "", ModuleCategory.MOVEMENT, "AirWalk");
    }
    // this is an excuse to have 2 fly
    private final FloatValue speed = new FloatValue("Speed", 0.28f, 0.1f, 2f, 0f, true, null);
    @Subscribe
    public void onBlockBB(BlockBBEvent e){
        if(mc.thePlayer == null) return;
        if(e.pos.getY() < mc.thePlayer.posY){
            e.blockBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(e.pos.getX(), e.pos.getY(), e.pos.getZ());
            if(speed.get() != 0){
                MovementUtil.setSpeed(speed.get());
            }
        }
    }
}
