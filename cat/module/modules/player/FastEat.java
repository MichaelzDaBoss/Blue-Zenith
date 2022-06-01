package cat.module.modules.player;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Packet", true, null, "Packet", "Timer");
    private final BooleanValue groundCheck = new BooleanValue("Ground Check", true, true, null);
    private final FloatValue timer = new FloatValue("Timer", 2f, 1.1f, 10f, 0.5f, true, __ -> mode.is("Timer"));
    private final IntegerValue packet = new IntegerValue("Packet amount", 5, 1, 30, 1, true, __ -> mode.is("Packet"));

    public FastEat() {
        super("FastEat", "", ModuleCategory.PLAYER, "fastuse");
    }

    private boolean sentPackets = false;
    private boolean usedTimer = false;

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if(!mc.thePlayer.isEating()) {
            sentPackets = false;
            if(usedTimer) {
                mc.timer.timerSpeed = 1F;
                usedTimer = false;
            }
        }
        else if(!groundCheck.get() || mc.thePlayer.onGround) switch (mode.get()) {
            case "Packet":
                if (mc.thePlayer.isEating() && (mc.thePlayer.getItemInUse().getItem()instanceof ItemFood) && !sentPackets) {
                    for (int i = 0; i < packet.get(); i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                    }
                    sentPackets = true;
                }
                break;
            case "Timer":
                if (mc.thePlayer.isEating() && (mc.thePlayer.getItemInUse().getItem()instanceof ItemFood))  {
                    mc.timer.timerSpeed = timer.get();
                    usedTimer = true;
                }
                break;
        }
    }

    @Override
    public String getTagName() {
        if(mode.get().equals("Packet")) {
            if(packet.get() > 25) return this.displayName + " §7Instant";
            else return this.displayName + " §7Packet " + packet.get();
        }
        else return this.displayName + " §7Timer";
    }
}



