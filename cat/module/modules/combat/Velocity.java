package cat.module.modules.combat;

import cat.events.impl.PacketEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.ModeValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", "", ModuleCategory.COMBAT);
    }

    public final ModeValue mode = new ModeValue("Mode", "Simple", true, null, "Simple", "Cancel", "Funny");

    private final FloatValue horizontal = new FloatValue("Horizontal", 100F, 0F, 500F, 1F, true, __ -> mode.is("Simple"));
    private final FloatValue vertical = new FloatValue("Vertical", 100F, 0F, 100F, 1F, true, __ -> mode.is("Simple"));

    private final BooleanValue explosions = new BooleanValue("Explosions", true, true, __ -> mode.is("Cancel"));

    boolean funny = false;

    @Subscribe
    public void onPacket(PacketEvent e){
        Packet<?> packet = e.packet;

        if(mode.is("Simple")) {
            if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity s = (S12PacketEntityVelocity) packet;
                if (s.getEntityID() == mc.thePlayer.getEntityId()) {
                    s.motionX *= (horizontal.get() / 100);
                    s.motionZ *= (horizontal.get() / 100);
                    s.motionY *= (vertical.get() / 100);
                }
            }
        }

        if(mode.is("Funny")) {
            if (packet instanceof S12PacketEntityVelocity) {
                funny = true;
            }
        }

        if(mode.is("Cancel")) {
            if ((packet instanceof S27PacketExplosion && explosions.get()) || packet instanceof S12PacketEntityVelocity) {
                e.cancel();
            }
        }
    }

    @Subscribe
    public void motion(UpdatePlayerEvent e) {

        if(mode.is("Funny")) {
            if (funny && e.isPre()) {
                funny = false;
                mc.thePlayer.motionX *= 0.1;
                mc.thePlayer.motionZ *= 0.1;
                mc.thePlayer.motionY *= 0.1;
            }
        }

    }

    @Override
    public String getTag() {
        return mode.get();
    }
}
