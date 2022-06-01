package cat.module.modules.render;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.IntegerValue;

public class Chams extends Module {
    public final IntegerValue r = new IntegerValue("R", 0, 0, 255, 0, true, null);
    public final IntegerValue g = new IntegerValue("G", 0, 0, 255, 0, true, null);
    public final IntegerValue b = new IntegerValue("B", 0, 0, 255, 0, true, null);
    public final IntegerValue a = new IntegerValue("A", 0, 0, 255, 0, true, null);
    public final IntegerValue r2 = new IntegerValue("R2", 0, -1, 255, 0, true, null);
    public final IntegerValue g2 = new IntegerValue("G2", 0, 0, 255, 0, true, null);
    public final IntegerValue b2 = new IntegerValue("B2", 0, 0, 255, 0, true, null);
    public final IntegerValue a2 = new IntegerValue("A2", 0, 0, 255, 0, true, null);
    public Chams(){
        super("Chams", "", ModuleCategory.RENDER);
    }
}
