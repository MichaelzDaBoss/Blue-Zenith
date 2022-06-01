package cat.module.modules.player;

import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.FloatValue;
import com.google.common.eventbus.Subscribe;

public class Timer extends Module {

    public FloatValue speed = new FloatValue("Speed", 2.0F, 0.1F, 10.0F, 0.1F, true, null);

    public Timer() {
        super("Timer", "", ModuleCategory.PLAYER, "gamespeed");
    }

    @Subscribe
    public void onUpdate(UpdatePlayerEvent event) {
        mc.timer.timerSpeed = this.speed.get();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
    }
}
