package cat.module.modules.player;

import cat.events.impl.PacketEvent;
import cat.events.impl.SilentPacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.ModeValue;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;

import java.util.ArrayList;

public class Blink extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Bidirectional", true, null, "Bidirectional", "Outgoing", "Incoming");

    public final ModeValue bidirPrio = new ModeValue("Priority", "Outgoing", true, __ -> mode.is("Bidirectional"), "Outgoing", "Incoming");

    public final BooleanValue allPackets = new BooleanValue("All Packets", true, true, __ -> !mode.is("Incoming"));

    public final BooleanValue movement = new BooleanValue("Blink Movement", true, true, __ -> allPackets.get() && !mode.is("Incoming"));
    public final BooleanValue attack = new BooleanValue("Blink Attack", true, true, __ -> allPackets.get() && !mode.is("Incoming"));

    boolean blink(Packet packet) {
        if(allPackets.get())
            return true;

        if(attack.get() && (packet instanceof C0APacketAnimation || packet instanceof C02PacketUseEntity))
            return true;

        return movement.get() && (packet instanceof C0CPacketInput || packet instanceof C03PacketPlayer);
    }

    public Blink() {
        super("Blink", "", ModuleCategory.PLAYER);
    }

    ArrayList<Packet<INetHandlerPlayClient>> incPackets = new ArrayList<>();
    ArrayList<Packet<INetHandlerPlayServer>> outPackets = new ArrayList<>();

    @Override
    public void onDisable() {

        if(mode.is("Bidirectional")) {

            if(bidirPrio.is("Incoming")) {

                for(Packet<INetHandlerPlayClient> packet : incPackets) {
                    packet.processPacket(mc.getNetHandler());
                }

                incPackets.clear();

                for(Packet<INetHandlerPlayServer> packet : outPackets) {
                    PacketUtil.sendSilent(packet);
                }

                outPackets.clear();

            } else {

                for(Packet<INetHandlerPlayServer> packet : outPackets) {
                    PacketUtil.sendSilent(packet);
                }

                outPackets.clear();

                for(Packet<INetHandlerPlayClient> packet : incPackets) {
                    packet.processPacket(mc.getNetHandler());
                }

                incPackets.clear();

            }

        } else {

            for(Packet<INetHandlerPlayClient> packet : incPackets) {
                packet.processPacket(mc.getNetHandler());
            }

            incPackets.clear();

            for(Packet<INetHandlerPlayServer> packet : outPackets) {
                PacketUtil.sendSilent(packet);
            }

            outPackets.clear();

        }

    }

    @Subscribe
    public void onPacket(PacketEvent packet) {

        if(packet.direction == EnumPacketDirection.CLIENTBOUND) {

            if(packet.packet.whyAreYouGay() == INetHandlerPlayClient.class) {

                if ((mode.is("Bidirectional") || mode.is("Outgoing")) && blink(packet.packet)) {

                    incPackets.add((Packet<INetHandlerPlayClient>) packet.packet);
                    packet.cancel();

                }

            }

        } else {

            if(packet.packet.whyAreYouGay() == INetHandlerPlayServer.class) {

                if (mode.is("Bidirectional") || mode.is("Incoming")) {

                    outPackets.add((Packet<INetHandlerPlayServer>) packet.packet);
                    packet.cancel();

                }

            }

        }

    }

    @Subscribe
    public void onSilentPacket(SilentPacketEvent packet) {

        if(packet.direction == EnumPacketDirection.CLIENTBOUND) {

            if(packet.packet.whyAreYouGay() == INetHandlerPlayClient.class) {

                if ((mode.is("Bidirectional") || mode.is("Outgoing")) && blink(packet.packet)) {

                    incPackets.add((Packet<INetHandlerPlayClient>) packet.packet);
                    packet.cancel();

                }

            }

        } else {

            if(packet.packet.whyAreYouGay() == INetHandlerPlayServer.class) {

                if (mode.is("Bidirectional") || mode.is("Incoming")) {

                    outPackets.add((Packet<INetHandlerPlayServer>) packet.packet);
                    packet.cancel();

                }

            }

        }

    }

}
