package cat.module.modules.render;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.ui.clickgui.ClickGui;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGUI extends Module {
    public static ClickGui clickGui = null;
    public static Color main_color = Color.WHITE;
    public static Color backgroundColor = Color.BLACK;

    public static BooleanValue closePrevious = new BooleanValue("Close previous", true, true, null);
    public static BooleanValue blur = new BooleanValue("Blur", false, true, null);
    public static BooleanValue animate = new BooleanValue("Animate", true, true, null);
    public static IntegerValue r = new IntegerValue("Red", 205, 0, 255, 1, true, (p1, p2) -> {updateMainColor(); return p2;}, null);
    public static IntegerValue g = new IntegerValue("Green", 205, 0, 255, 1, true, (p1, p2) -> {updateMainColor(); return p2;}, null);
    public static IntegerValue b = new IntegerValue("Blue", 205, 0, 255, 1, true, (p1, p2) -> {updateMainColor(); return p2;}, null);
    public static IntegerValue bb = new IntegerValue("Background Brightness", 35, 0, 255, 1,true, (p1, p2) -> {updateBackgroundColor(); return p2;}, null);
    //public IntegerValue rg = new IntegerValue("BackgroundR", 35, 0, 255, 1,true, (p1, p2) -> {updateBackgroundColor(); return p2;}, null);
    //public IntegerValue gg = new IntegerValue("BackgroundG", 35, 0, 255, 1, true, (p1, p2) -> {updateBackgroundColor(); return p2;}, null);
    //public IntegerValue bg = new IntegerValue("BackgroundB", 35, 0, 255, 1, true, (p1, p2) -> {updateBackgroundColor(); return p2;}, null);
    public static IntegerValue ba = new IntegerValue("Background Alpha", 255, 0, 255, 1, true, (p1, p2) -> {updateBackgroundColor(); return p2;}, null);
    static {
        updateMainColor();
        updateBackgroundColor();
    }
    private static void updateMainColor(){
        main_color = new Color(r.get(), g.get(), b.get());
    }
    private static void updateBackgroundColor(){
        backgroundColor = new Color(bb.get(), bb.get(), bb.get(), ba.get());
    }
    public ClickGUI() {
        super("ClickGUI", "", ModuleCategory.RENDER, Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable(){
        if(mc.thePlayer != null){
            mc.displayGuiScreen(clickGui);
        }
        super.setState(false);
    }
}
