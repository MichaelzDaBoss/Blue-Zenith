package cat.events.impl;

import cat.events.Event;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    public Packet<?> packet;
    public EnumPacketDirection direction;
    public PacketEvent(Packet<?> packet, EnumPacketDirection direction){
        this.packet = packet;
        this.direction = direction;
    }
}
