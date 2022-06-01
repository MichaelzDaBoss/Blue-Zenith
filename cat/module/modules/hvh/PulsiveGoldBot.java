package cat.module.modules.hvh;

import cat.FriendManager;
import cat.events.impl.PacketEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.ModuleManager;
import cat.module.modules.combat.Aura;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class PulsiveGoldBot extends Module {

    public PulsiveGoldBot() {
        super("PulsiveGoldBot", "Angles Visual Skid Edition", ModuleCategory.HVH);
    }

    String masterAcc = "Chong76";
    String bountyAcc;
    boolean isMaster;
    boolean isBotting;

    Vec3 bottingPos;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null) {
            setState(false);
            return;
        }

        isBotting = false;
        bottingPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @Subscribe
    public void packet(PacketEvent e) {
        if(!FriendManager.is(masterAcc)) {
            FriendManager.add(masterAcc);
        }

        if(bountyAcc != null) {
            if (!FriendManager.is(bountyAcc)) {
                FriendManager.add(bountyAcc);
            }
        }

        if(e.packet instanceof S06PacketUpdateHealth) {
            if(isMaster) {
                if (((S06PacketUpdateHealth) e.packet).getHealth() == 20 || ((S06PacketUpdateHealth) e.packet).getHealth() == 40) {
               //     mc.thePlayer.sendChatMessage("Claimed!");
                } else {
                    if(isMaster) {
                        ModuleManager.getModuleClass(Aura.class).setState(false);
                        ModuleManager.getModuleClass(PulsiveBot.class).setState(true);
                    }

                    if(bountyAcc != null) {
                        FriendManager.remove(bountyAcc);
                    }

                    isBotting = false;
                }
            }
        }

        if(e.packet instanceof S02PacketChat) {
            S02PacketChat chat = (S02PacketChat) e.packet;
            String msg = chat.getChatComponent().getUnformattedText();

            if(msg.contains(masterAcc)) {
                if (msg.contains("-start:")) {
               //     System.out.println("DEBUG START PRE");
                    String target = (msg.split(" ")[msg.split(" ").length - 1]).split(":")[1];
                    isMaster = Objects.equals(target, mc.thePlayer.getName());

                    if(bountyAcc != null) {
                        FriendManager.remove(bountyAcc);
                    }

                    bountyAcc = target;
                    isBotting = true;

                    if(!FriendManager.is(bountyAcc)) {
                        FriendManager.add(bountyAcc);
                    }

                    if(isMaster) {
                        ModuleManager.getModuleClass(Aura.class).setState(true);
                        ModuleManager.getModuleClass(PulsiveBot.class).setState(false);
                    }
                } else if (msg.contains("-stop")) {
                    if(isMaster) {
                        ModuleManager.getModuleClass(Aura.class).setState(false);
                        ModuleManager.getModuleClass(PulsiveBot.class).setState(true);
                        ((PulsiveBot) ModuleManager.getModuleClass(PulsiveBot.class)).respawnValues();
                    }

                    if(bountyAcc != null) {
                        FriendManager.remove(bountyAcc);
                    }

                    isBotting = false;
                } else {

                }
            }

        }

    }

}
