package cat.util;

import net.minecraft.network.Packet;

public final class PacketUtil extends MinecraftInstance {

    public static void send(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void sendSilent(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }

    public static void delay(Packet<?> packet, long ms, boolean event) {
        new DelayPacketThread(packet, ms, event).start();
    }

    public static void receive(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().receiveNoEvent(packet);
    }

    public static class DelayPacketThread extends Thread {

        private final Packet<?> packet;
        private final long ms;
        private final boolean event;

        public DelayPacketThread(Packet<?> packet, long ms, boolean event) {
            this.ms = ms;
            this.packet = packet;
            this.event = event;
        }

        @Override
        public void run() {
            try {
                this.wait(ms);
                if(event) {
                    PacketUtil.send(packet);
                } else {
                    PacketUtil.sendSilent(packet);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Packet Errored: " + packet.getClass().getSimpleName());
            }
        }
    }
}
