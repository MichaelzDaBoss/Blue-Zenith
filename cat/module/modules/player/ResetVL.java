package cat.module.modules.player;

import cat.events.impl.*;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.ModeValue;
import cat.util.MillisTimer;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import com.google.common.eventbus.Subscribe;
import javafx.scene.transform.Scale;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.optifine.util.FontUtils;

public class ResetVL extends Module {

    public final ModeValue mode = new ModeValue("Mode", "Automatic", true, null, "Automatic", "Manual");

    MillisTimer autoTimer = new MillisTimer().reset();

    boolean doReset;
    boolean stopWhenGround;

    public ResetVL() {
        super("ResetVL", "", ModuleCategory.PLAYER, "antiflag");
    }

    @Override
    public String getTag() {
        return mode.get();
    }

    @Override
    public void onEnable() {

        if(mode.is("Manual")) {
            doReset = true;
        }

    }

    @Subscribe
    public void motion(UpdatePlayerEvent e) {

        if(doReset) {
            mc.timer.timerSpeed = 0.5F;
            if(mc.thePlayer.onGround) {
                if(stopWhenGround) {
                    doReset = false;
                    mc.timer.timerSpeed = 1F;
                } else {
                    mc.thePlayer.jump();
                    stopWhenGround = true;
                }
            }
        } else {
            if(mode.is("Manual")) {
                setState(false);
            }
        }

    }

    @Subscribe
    public void move(MoveEvent e) {

        if(doReset) {
            e.x = 0;
            e.z = 0;
        }

    }

    @Subscribe
    public void packet(PacketEvent e) {
        if(e.packet instanceof S08PacketPlayerPosLook) {
            autoTimer.reset();
        }
    }

    @Subscribe
    public void render(Render2DEvent e) {
        if(mode.is("Automatic")) {
            if(!autoTimer.hasTimeReached(1000)) {
                TFontRenderer fr = FontUtil.fontOpenSansRegular36;
                fr.drawStringWithShadow("Press SHIFT to Reset VL!",
                        new ScaledResolution(mc).getScaledWidth()/2f - fr.getStringWidth("Press SHIFT to Reset VL!")/2f,
                new ScaledResolution(mc).getScaledHeight()/2f+20, -1);
            }
        }
    }

    @Subscribe
    public void moveButton(MoveButtonEvent e) {
        if(mode.is("Automatic")) {
            if (!autoTimer.hasTimeReached(1000)) {
                if(e.isSneak()) {
                    doReset = true;
                    e.setSneak(false);
                }
            }
        }
    }

}
