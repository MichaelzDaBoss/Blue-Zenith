package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.util.ColorUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;

@SuppressWarnings("unused")
public class AutoRegister extends Module {
    public AutoRegister(){
        super("AutoRegister", "", ModuleCategory.MISC);
    }
    String password = "sigmaclient.info";
    @Subscribe
    public void onPacket(PacketEvent e){
        Packet<?> packet = e.packet;
        if(packet instanceof S45PacketTitle){
            final S45PacketTitle p = (S45PacketTitle) packet;
            if((p.getType().equals(S45PacketTitle.Type.TITLE) || p.getType().equals(S45PacketTitle.Type.SUBTITLE))){
                final String str = p.getMessage().getUnformattedText();
                handleString(ColorUtil.removeGay(str));
            }
        }
        if(packet instanceof S02PacketChat){
            final S02PacketChat p = (S02PacketChat) packet;
            handleString(ColorUtil.removeGay(p.getChatComponent().getUnformattedText()));
        }
    }
    private void sendChatMessage(String str){
        mc.thePlayer.sendChatMessage(str.replace("$pass", password));
    }
    private void handleString(String str){
        if(str.matches("\\/(l|L)ogin <.*>") || str.contains("/login")){
            sendChatMessage("/login $pass");
        }else if(str.matches("\\/(r|R)egister <.*> <.*>") || str.contains("/register")){
            sendChatMessage("/register $pass $pass");
        }
    }
}
