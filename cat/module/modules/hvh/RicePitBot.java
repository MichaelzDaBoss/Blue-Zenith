package cat.module.modules.hvh;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S1BPacketEntityAttach;

import java.util.LinkedList;

public class RicePitBot extends Module {

    public RicePitBot() {
        super("RicePitBot", "", ModuleCategory.HVH, "ricepitbotter");
    }

    LinkedList<C03PacketPlayer> positions = new LinkedList<>();

    @Subscribe
    public void packet(PacketEvent e) {

        if(e.packet instanceof S1BPacketEntityAttach) {
            if(((S1BPacketEntityAttach) e.packet).getVehicleEntityId() <= 0) {
                e.cancel();
            }
        }

    }

}
