package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S02PacketChat;

public class ChatBypass extends Module {

    public BooleanValue pulsiveBot = new BooleanValue("Pulsive Botting", false, true, null);

    public ChatBypass() {
        super("ChatBypass", "Normal", ModuleCategory.MISC, "chatfilter");
    }

    @Subscribe
    public void packet(PacketEvent event) {
        if(event.packet instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.packet;

            if(packet.getChatComponent().getUnformattedText().isEmpty() || packet.getChatComponent().getUnformattedText().equals(" ")) {
                event.cancel();
            }

            if(pulsiveBot.get()) {
                if(packet.getChatComponent().getUnformattedText().contains("KILL!") || packet.getChatComponent().getUnformattedText().contains("ASSIST!")) {
                    event.cancel();
                }
            }
        }
    }

}
