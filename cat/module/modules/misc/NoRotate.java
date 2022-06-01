package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("NoRotate", "Vanilla", ModuleCategory.MISC, "norot");
    }

    @Subscribe
    public void packet(PacketEvent e) {
        if(e.packet instanceof S08PacketPlayerPosLook) {
            ((S08PacketPlayerPosLook) e.packet).pitch = mc.thePlayer.rotationPitch;
            ((S08PacketPlayerPosLook) e.packet).yaw = mc.thePlayer.rotationYaw;
        }
    }

}
