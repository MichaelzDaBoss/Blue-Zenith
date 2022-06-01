package cat.module.modules.movement;

import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import com.google.common.eventbus.Subscribe;

public class AirJump extends Module {

    public AirJump() {
        super("AirJump", "", ModuleCategory.MOVEMENT);
    }

    int ticks;

    @Subscribe
    public void move(UpdatePlayerEvent event) {
        if(event.isPre()) {
            ticks++;
        }

        if(mc.gameSettings.keyBindJump.isPressed()) {
            if(ticks>5) {
                mc.thePlayer.jump();
                ticks = 0;
            }
        }
    }
}
