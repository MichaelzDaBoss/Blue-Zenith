package cat.module.modules.fun;

import cat.events.impl.Render2DEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.util.RenderUtil;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
public class hampter extends Module {
    public hampter() {
        super("hampter", "hampter", ModuleCategory.FUN, "hi");
    }
    @Subscribe
    public void vbucksfreedownload2021(Render2DEvent e){
        GlStateManager.pushMatrix();
        RenderUtil.drawImage(new ResourceLocation("cat/fun/hampter.jpg"), 0, e.resolution.getScaledHeight() - 120, 120, 120, 1);
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
}
