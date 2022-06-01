package cat.module.modules.render;

import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.*;

public class Animations extends Module {
    public final ModeValue anim = new ModeValue("Animation", "1.7", true, null, "1.7", "Exhibition");
    public final FloatValue itemPosX = new FloatValue( "ItemPosX",0, -100, 100, 0, true, null);
    public final FloatValue itemPosY = new FloatValue( "ItemPosY",0, -100, 100, 0, true, null);
    public final FloatValue itemPosZ = new FloatValue( "ItemPosZ",0, -100, 100, 0, true, null);
    public final FloatValue itemScale = new FloatValue( "ItemScale",1, 0.1f, 5, 0, true, null);
    public final FloatValue translatePosX = new FloatValue( "TranslatePosX",0, -100, 100, 0, true, null);
    public final FloatValue translatePosY = new FloatValue( "TranslatePosY",0, -100, 100, 0, true, null);
    public final FloatValue translatePosZ = new FloatValue( "TranslatePosZ",0, -100, 100, 0, true, null);
    public final FloatValue translateScale = new FloatValue( "TranslateScale",1, 0.1f, 5, 0, true, null);
    public final BooleanValue slowSwing = new BooleanValue("SlowSwing", false, true);
    public final IntegerValue slowSwingValue = new IntegerValue("SlowSwingValue", 50, 0,100,0,true, __ -> slowSwing.get());
    public final BooleanValue betterSwing = new BooleanValue("BetterSwing", false, true);
    @SuppressWarnings("unused")
    private final ActionValue reset = new ActionValue("Reset",() -> {
        this.anim.set("1.7");
        this.itemPosX.set(0f);
        this.itemPosY.set(0f);
        this.itemPosZ.set(0f);
        this.itemScale.set(1F);
        this.translatePosX.set(0f);
        this.translatePosY.set(0f);
        this.translatePosZ.set(0f);
        this.translateScale.set(1f);
        this.slowSwing.set(false);
        this.slowSwingValue.set(0);
    });
    public Animations() {
        super("Animations", "", ModuleCategory.RENDER, "animations", "anim");
    }
    @Override
    public String getTag(){
        return anim.get();
    }
}
