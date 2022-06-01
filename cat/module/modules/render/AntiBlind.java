package cat.module.modules.render;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;

public class AntiBlind extends Module {
    public BooleanValue noPumpkin = new BooleanValue("Pumpkins", false, true, null);
    public BooleanValue noBlindnessFog = new BooleanValue("Blindness", false, true, null);
    public BooleanValue noLavaFog = new BooleanValue("Lava fog", false, true, null);
    public BooleanValue noWaterFog = new BooleanValue("Water fog", false, true, null);
    public AntiBlind() {
        super("AntiBlind", "", ModuleCategory.RENDER);
    }
}
