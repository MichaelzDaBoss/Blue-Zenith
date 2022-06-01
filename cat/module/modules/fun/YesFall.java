package cat.module.modules.fun;

import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

@SuppressWarnings("unused")
public class YesFall extends Module {
    // lmao thx levzzz for the idea
    public YesFall(){
        super("YesFall", "", ModuleCategory.FUN);
    }

    //private final ListValue choices = new ListValue("test", true, "sex1", "sex2", "sex3");

    @Subscribe
    public void onMove(UpdatePlayerEvent e){
        if(Math.round(mc.thePlayer.fallDistance) % 3 == 0){
            // rip i couldn't make it :((( :cry:
            //i am gaming :sunglasses:
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.fallDistance, mc.thePlayer.posZ, false));
        }
    }

    /*@Override
    public void onEnable() {
        choices.getAllChoices().forEach(ClientUtils::fancyMessage);
        ClientUtils.fancyMessage("all choices ^");
        ClientUtils.fancyMessage(" ");
        choices.getSelectedChoices().forEach(ClientUtils::fancyMessage);
        ClientUtils.fancyMessage("selected choices ^");
        ClientUtils.fancyMessage(" ");
        choices.toggleChoice(choices.getAllChoices().get(0));
        choices.getSelectedChoices().forEach(ClientUtils::fancyMessage);
        ClientUtils.fancyMessage("selected choices ^");
    }*/
}
