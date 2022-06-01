package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.events.impl.RespawnEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.module.value.types.ModeValue;
import cat.util.*;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.LinkedList;

public class PingSpoof extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Timeout", true, null, "Timeout", "Verus", "BlocksMC", "All", "Watchdog", "BMC New");
    public final BooleanValue debug = new BooleanValue("Debug", false, true, null);

    public final IntegerValue verusQueue = new IntegerValue("Queue", 50, 10, 1000, 1, true, __ -> mode.is("Verus") || mode.is("Timeout") || mode.is("BlocksMC"));
    public final IntegerValue verusTicks = new IntegerValue("Ticks", 20, 10, 100, 1, true, __ -> mode.is("Verus"));
    public final BooleanValue funi = new BooleanValue("Funi", false, true, __ -> mode.is("BlocksMC"));

    private final LinkedList<Packet> packets = new LinkedList<>();

    LinkedList<C0FPacketConfirmTransaction> bmcpackets = new LinkedList<>();
    LinkedList<C00PacketKeepAlive> bmckeepalives = new LinkedList<>();

    LinkedList<C0FPacketConfirmTransaction> wdtransactions = new LinkedList<>();
    LinkedList<C00PacketKeepAlive> wdkeepalives = new LinkedList<>();
    MillisTimer timer2 = new MillisTimer();
    int lastuid;
    AngleUtility angleUtility = new AngleUtility(110, 120, 30, 40);
    Angle lastAngle;
    float yawDiff;

    MillisTimer timer = new MillisTimer();

    public PingSpoof() {
        super("PingSpoof", "", ModuleCategory.MISC);
    }

    @Override
    public void onEnable() {
        bmcpackets.clear();
        bmckeepalives.clear();
        timer.reset();
    }

    @Override
    public void onDisable() {
        for(Packet packet : bmcpackets) {
            PacketUtil.sendSilent(packet);
        }
        for(Packet packet : bmckeepalives) {
            PacketUtil.sendSilent(packet);
        }
    }

    @Override
    public String getTag() {
        if(!mode.is("BlocksMC")) {
            return mode.get() + (verusQueue.isVisible()? " " + packets.size() : "");
        } else {
            return mode.get() + " " + bmcpackets.size() + " " + bmckeepalives.size();
        }
    }

    @Subscribe
    public void respawn(RespawnEvent e) {

        if(mode.is("Watchdog")) {
            wdkeepalives.clear();
            wdtransactions.clear();
            lastuid = 0;
        }

    }

    @Subscribe
    public void onUpdate(UpdatePlayerEvent e) {

        try {

            if (mode.is("Verus")) {

                if (mc.thePlayer.ticksExisted % verusTicks.get() == 0) {
                    for (Packet p : packets) {
                        PacketUtil.sendSilent(p);
                    }
                    packets.clear();
                }

            }

            if (mode.is("BlocksMC")) {
                if (funi.get()) {
                    if (timer.hasTimeReached(490)) {
                        if (!bmcpackets.isEmpty()) {
                            PacketUtil.sendSilent(bmcpackets.poll());
                        }
                        timer.reset();
                    }

                    if (timer.hasTimeReached(400)) {
                        if (!bmckeepalives.isEmpty()) {
                            PacketUtil.sendSilent(bmckeepalives.poll());
                        }
                    }
                } else {
                    for (Packet packet : bmcpackets) {
                        PacketUtil.sendSilent(packet);
                    }
                    for (Packet packet : bmckeepalives) {
                        PacketUtil.sendSilent(packet);
                    }
                    bmcpackets.clear();
                    bmckeepalives.clear();
                }
            }

            if (mode.is("BMC New")) {
                if (timer.hasTimeReached(5000)) {
                    if (!bmcpackets.isEmpty()) {
                        PacketUtil.sendSilent(bmcpackets.poll());
                    }
                    timer.reset();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Subscribe
    public void onPacket(PacketEvent e) {

        if(mode.is("Verus")) {
            if(e.packet instanceof C0FPacketConfirmTransaction) {

                packets.add(e.packet);
                e.cancel();

                if(debug.get()) {
                    ClientUtils.fancyMessage("Verus Queue: " + packets.size());
                }

                while(packets.size() > verusQueue.get()) {
                    PacketUtil.sendSilent(packets.poll());
                }

            }
        }

        if(mode.is("Timeout")) {
            if(e.packet instanceof C0FPacketConfirmTransaction || e.packet instanceof C00PacketKeepAlive) {

                packets.add(e.packet);
                e.cancel();

                if(debug.get()) {
                    ClientUtils.fancyMessage("Timeout Queue: " + packets.size());
                }

                while(packets.size() > verusQueue.get()) {
                    PacketUtil.sendSilent(packets.poll());
                }

            }
        }

        if(mode.is("BlocksMC")) {
            if(BypassUtil.shouldNotRun()) {
                this.timer.reset();
                this.bmcpackets.clear();
                this.bmckeepalives.clear();
            } else {
                if (e.packet instanceof C0FPacketConfirmTransaction) {
                    if (funi.get()) {
                        ((C0FPacketConfirmTransaction) e.packet).accepted = !((C0FPacketConfirmTransaction) e.packet).accepted;
                    }
                    this.bmcpackets.add((C0FPacketConfirmTransaction) e.packet);
                    e.cancel();
                    if(funi.get()) {
                        if (this.bmcpackets.size() > verusQueue.get()) {
                            PacketUtil.sendSilent(bmcpackets.poll());
                        }
                    } else {
                        if (this.bmcpackets.size() > verusQueue.get()) {
                            for(Packet packet : bmcpackets) {
                                PacketUtil.sendSilent(packet);
                            }
                        }
                    }
                }

                if(e.packet instanceof C00PacketKeepAlive) {
                    ((C00PacketKeepAlive) e.packet).key -= 30;
                    bmckeepalives.add((C00PacketKeepAlive) e.packet);
                    e.cancel();
                }
            }
        }

        if(mode.is("BMC New")) {
            if(BypassUtil.shouldNotRun()) {
                this.timer.reset();
                this.bmcpackets.clear();
            } else {
                if (e.packet instanceof C0FPacketConfirmTransaction) {
                    this.bmcpackets.add((C0FPacketConfirmTransaction) e.packet);
                    e.cancel();

                    if (this.bmcpackets.size() > 250) {
                        for(Packet packet : bmcpackets) {
                            PacketUtil.sendSilent(packet);
                        }
                    }
                }
            }
        }

    }

}


