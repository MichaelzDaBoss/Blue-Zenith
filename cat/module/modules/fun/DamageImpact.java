package cat.module.modules.fun;

import cat.events.impl.Render2DEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.util.RenderUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class DamageImpact extends Module {
    // this is a test module lmao
    public DamageImpact() {
        super("DamageImpact", "", ModuleCategory.FUN);
    }
    int alpha = 0;

    @Subscribe
    public void onUpdate(UpdateEvent e){
        if(mc.thePlayer.hurtTime == 9){
            alpha = 255;
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("hi")));
        }
    }
    @Subscribe
    public void onRender2D(Render2DEvent e){
        if(alpha > 1){
            RenderUtil.drawImage(new ResourceLocation("cat/fun/the.png"), 0, 0, e.resolution.getScaledWidth(), e.resolution.getScaledHeight(), alpha / 255f);
        }
        if(alpha > 2){
            alpha -= 0.3 * RenderUtil.delta;
        }
    }
}
