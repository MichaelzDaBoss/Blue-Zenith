package cat.module.modules.player;

import cat.events.impl.BlockBBEvent;
import cat.events.impl.Render2DEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.ModeValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class NoFall extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Edit", true, null, "Edit", "Collision", "Packet");
    private final ModeValue collideMode = new ModeValue("Collision", "BB", true, __ -> mode.get().equals("Collision"), "BB", "Tick", "Frame");
    private final BooleanValue framePacket = new BooleanValue("Packet Ground", true, true, __ -> mode.get().equals("Collision") && (collideMode.get().equals("Tick")));

    public NoFall(){
        super("NoFall", "", ModuleCategory.PLAYER, "NoFall");
    }

    @Override
    public String getTag(){
        return this.mode.get().equals("Collision")? "Collision " + this.collideMode.get(): this.mode.get();
    }

    @Subscribe
    public void onBlockBB(BlockBBEvent e){
        if(mode.get().equals("Collision") && collideMode.get().equals("BB")){
            if(mc.thePlayer.fallDistance >= 2.5 && e.pos.getY() < mc.thePlayer.posY && e.pos.getY() > mc.thePlayer.posY - 1){
                e.blockBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 0, 5).offset(e.pos.getX(), e.pos.getY(), e.pos.getZ());
            }
        }
    }

    @Subscribe
    public void onPacket(UpdatePlayerEvent e){
        if (mode.get().equals("Edit")) {
            if(mc.thePlayer.fallDistance >= 3){
                e.onGround = true;
            }
        }

        if(mode.is("Packet")) {
            if(mc.thePlayer.fallDistance >= 2.5){
                e.onGround = true;
                mc.thePlayer.fallDistance = 0;
            }
        }

        if(mode.get().equals("Collision") && collideMode.get().equals("Tick")) {
            if(mc.thePlayer.fallDistance >= 2.5){
                mc.thePlayer.fallDistance = 0;
                mc.thePlayer.motionY = 0;

                if(framePacket.get()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                } else {
                    e.onGround = true;
                }
            }
        }
    }

    @Subscribe
    public void onRender2D(Render2DEvent e) {
        if(mode.get().equals("Collision") && collideMode.get().equals("Frame")) {
            if(mc.thePlayer.fallDistance >= 2.5){
                mc.thePlayer.fallDistance = 0;
                mc.thePlayer.motionY = 0;

                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            }
        }
    }
}
