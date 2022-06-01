package cat.module.modules.misc;

import cat.events.impl.UpdatePlayerEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.util.MillisTimer;
import com.google.common.eventbus.Subscribe;

public class MemoryFix extends Module {

    public final BooleanValue autogc = new BooleanValue("Auto GC", false, true, null);
    public final IntegerValue gcdelay = new IntegerValue("GC Delay", 300, 5, 600, 1, true, __ -> autogc.get());

    public MemoryFix() {
        super("MemoryFix", "", ModuleCategory.MISC, "memoryfix");
    }

    MillisTimer timer = new MillisTimer();

    @Subscribe
    public void onUpdatePlayer(UpdatePlayerEvent e) {
        if(timer.hasTimeReached(gcdelay.get() * 1000)) {
            if(autogc.get()) {
                System.gc();
            }
            timer.reset();
        }
    }
}
