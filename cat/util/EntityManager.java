package cat.util;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityManager extends MinecraftInstance{
    public enum Targets{
        MOBS("Mobs", false),
        PLAYERS("Players", true),
        INVISIBLE("Invisible", true),
        DEAD("Dead", false),
        TEAMS("Teams", true)
        ;
        public String displayName;
        public boolean on;
        Targets(String displayName, boolean on){
            this.displayName = displayName;
            this.on = on;
        }
    }
    public static boolean isTarget(Entity ent, boolean antibot){
        if(antibot) {
            if(isEntityBot(ent)) {
                return false;
            }
        }

        if(ent == mc.thePlayer || ent instanceof EntityArmorStand)
            return false;
        if(ent instanceof EntityLivingBase && ((EntityLivingBase) ent).getHealth() <= 0 && !Targets.DEAD.on)
            return false;
        if(ent instanceof EntityLivingBase){
            Team lol = ((EntityLivingBase) ent).getTeam();
            Team lel = mc.thePlayer.getTeam();
            if(lol != null && lel != null && lol.isSameTeam(lel)){
                return Targets.TEAMS.on;
            }
        }
        if(isMob(ent) || isAnimal(ent))
            return Targets.MOBS.on;
        if(ent instanceof EntityPlayer)
            return Targets.PLAYERS.on;
        if(ent.isInvisible())
            return Targets.INVISIBLE.on;



        return false;
    }
    public static boolean isAnimal(Entity ent){
        return ent instanceof EntitySheep || ent instanceof EntityCow || ent instanceof EntityPig
                || ent instanceof EntityChicken || ent instanceof EntityRabbit || ent instanceof EntityHorse
                || ent instanceof EntityBat;
    }
    public static boolean isMob(Entity ent){
        return ent instanceof EntityZombie || ent instanceof EntitySkeleton
                || ent instanceof EntityVillager || ent instanceof EntitySlime
                || ent instanceof EntityCreeper || ent instanceof EntityEnderman
                || ent instanceof EntityEndermite || ent instanceof EntitySpider
                || ent instanceof EntityWitch || ent instanceof EntityWither || ent instanceof EntityBlaze;
    }

    private static boolean isEntityBot(Entity entity) {
        return entity.getDisplayName().getUnformattedTextForChat().toLowerCase().equals("§8npc §8| §8shop") || entity.getDisplayName().getUnformattedText().toLowerCase().equals("§8npc §8| §8upgrades");
    }

    private static boolean isOnTab(Entity entity) {
        Iterator var2 = mc.getNetHandler().getPlayerInfoMap().iterator();

        NetworkPlayerInfo info;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            info = (NetworkPlayerInfo)var2.next();
        } while(!info.getGameProfile().getName().equals(entity.getDisplayName().getFormattedText()));

        return true;
    }

    public static class FriendManager {

        public static ArrayList<String> friends = new ArrayList<>();

        public static void addFriend(String name) {
            friends.add(name);
        }

        public static void removeFriend(String name) {
            friends.remove(name);
        }

        public static boolean isFriend(String name) {
            return friends.contains(name);
        }

        public static boolean isFriend(Entity entity) {
            return isFriend(entity.getName());
        }

    }

    public static class TargetManager {

        public static ArrayList<String> targets = new ArrayList<>();

        public static void addTarget(String name) {
            targets.add(name);
        }

        public static void removeTarget(String name) {
            targets.remove(name);
        }

        public static boolean isTarget(String name) {
            return targets.contains(name);
        }

        public static boolean isTarget(Entity entity) {
            return isTarget(entity.getName());
        }

    }
}
