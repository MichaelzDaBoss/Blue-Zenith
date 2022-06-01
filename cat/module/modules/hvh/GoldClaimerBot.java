package cat.module.modules.hvh;

import cat.events.impl.PacketEvent;
import cat.events.impl.PostPacketEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;

import java.util.ArrayList;

public class GoldClaimerBot extends Module {

    public BooleanValue restart = new BooleanValue("Restart", false, true, null);

    public GoldClaimerBot() {
        super("GoldClaimerBot", "Why", ModuleCategory.HVH);
    }

    MillisTimer antispawn = new MillisTimer().reset();
    EntityPlayer pl;

    @Subscribe
    public void update(UpdateEvent e) {
        ArrayList<EntityPlayer> players = new ArrayList<>();

        for(EntityPlayer p : mc.theWorld.playerEntities) {
            if(mc.thePlayer.getDistanceToEntity(p) < 4 && p != mc.thePlayer) {
                players.add(p);
            }
        }

        for(EntityPlayer p : players) {
            if(p.getDisplayName().getUnformattedText().contains("4750g") && antispawn.hasTimeReached(1000)) {
                mc.thePlayer.sendChatMessage("-stop");
                PacketUtil.send(new C02PacketUseEntity(p, C02PacketUseEntity.Action.ATTACK));

                pl = p;

                antispawn.reset();
                return;
            }
        }
    }

    @Subscribe
    public void packet(PostPacketEvent e) {
        if(e.packet instanceof S02PacketChat) {
            if(((S02PacketChat) e.packet).getChatComponent().getUnformattedText().contains("BOUNTY CLAIMED!")) {
                if(restart.get()) {
                    if(pl != null) {
                        mc.thePlayer.sendChatMessage("-start:" + pl.getName());
                        pl = null;
                    }
                }
            }
        }
    }

}
