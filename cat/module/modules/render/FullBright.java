package cat.module.modules.render;

import cat.module.Module;
import cat.module.ModuleCategory;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class FullBright extends Module {
    public FullBright() {
        super("FullBright", "", ModuleCategory.RENDER, Keyboard.KEY_V);
    }
    private float prevBrightness = 0;
    public void onEnable(){
        prevBrightness = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 10000;
    }
    public void onDisable(){
        mc.gameSettings.gammaSetting = prevBrightness;
    }
}
