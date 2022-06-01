package cat.module.modules.misc;

import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.IntegerValue;
import com.google.common.eventbus.Subscribe;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

@SuppressWarnings("all")
public class FPSLimiter extends Module {

    private final IntegerValue limit = new IntegerValue("Limit", 10, 1, 60, 1, true, null);
    public FPSLimiter() {
        super("FPSLimiter", "", ModuleCategory.MISC);
        this.displayName = "FPS Limiter";
        this.hidden = true;
    }
    private int fps = 0;
    private boolean wasActive = true;

    @Subscribe
    public void aa(UpdateEvent e) throws LWJGLException {
        if(!Display.isActive()) {
            Display.sync(limit.get());
        }
    }
}
