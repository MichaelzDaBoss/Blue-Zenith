package cat.module.modules.combat;

import cat.events.impl.AttackEvent;
import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.ModuleManager;
import cat.module.modules.movement.Speed;
import cat.module.value.types.ModeValue;
import cat.util.MillisTimer;
import cat.util.PacketUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Packet1", true, null, "Packet1", "Packet2", "PacketVerus", "PacketNew", "Minemora");

    public Criticals() {
        super("Criticals", "", ModuleCategory.COMBAT);
    }

    MillisTimer timer = new MillisTimer();
    private int ticks;
    int groundTicks, stage, count;
    double y;
    private boolean attacked;

    @Override
    public String getTag() {
        return mode.get();
    }

    @Subscribe
    public void onAttack(AttackEvent event) {

        stage = 0;

        if(mode.is("Packet1")) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2f, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.11f, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.02f, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        if(mode.is("Packet2")) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1232225, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0554E-9, mc.thePlayer.posZ, false));
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }

        if(mode.is("PacketVerus")) {
            if(timer.hasTimeReached(100)) {
                timer.reset();

                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0554E-19, mc.thePlayer.posZ, false));
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.010554E-9, mc.thePlayer.posZ, true));
            }
        }

        if(mode.is("PacketNew")) {
            if (!mc.thePlayer.movementInput.jump && (timer.hasTimeReached(150)) && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) && !mc.thePlayer.isOnLadder() &&
                    (!ModuleManager.getModuleClass(Speed.class).getState())) {
                if (mc.thePlayer.onGround) {
                    final double[] values = {0.0625, 0.001 - (Math.random() / 10000)}; // CARPET VALUE
                    for (final double d : values)
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
                }
            }
        }

        timer.reset();

    }

    @Subscribe
    public void motion(UpdatePlayerEvent event) {
        if(isOnGround(0.001)){
            groundTicks ++;
        }else if(!mc.thePlayer.onGround){
            groundTicks = 0;
        }

        if(event.isPre() && mode.is("Minemora")){
            mc.thePlayer.lastReportedPosY = 0;
            double ypos = mc.thePlayer.posY;

            if(isOnGround(0.001)){
                event.setOnGround(false);
                if(stage == 0){
                    y = ypos + 1E-8;
                    event.setOnGround(true);
                }else if(stage == 1)
                    y-= 5E-15;
                else
                    y-= 4E-15;

                if(y <= mc.thePlayer.posY){
                    stage = 0;
                    y = mc.thePlayer.posY;
                    event.setOnGround(true);
                }
                event.setY(y);
                stage ++;
            }else
                stage = 0;

        }
    }

    @Override
    public void onDisable() {
        attacked = false;
        timer.reset();
        ticks = 0;
    }

    @Override
    public void onEnable() {
        stage = 0;
        count = 0;
    }

    public boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }
}
