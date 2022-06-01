package cat.module.modules.render;

import cat.BlueZenith;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.ListValue;
import cat.module.value.types.StringValue;
import cat.util.ClientUtils;

public class Rotations extends Module {

    public Rotations() {
        super("Rotations", "", ModuleCategory.RENDER, "rotations", "rot");
    }
    public float yaw = 0;
    public float pitch = 0;
    public float prevYaw = 0;
    public float prevPitch = 0;

}
