package cat.ui.clickgui.component.impl;

import cat.module.modules.render.ClickGUI;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.ModeValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Visible;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ModeComponent extends Component implements Visible {

    public ModeValue mode;

    public ModeComponent(ModeValue mode) {
        this.mode = mode;
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(mode.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(mode.get(), (float) (x + width - FontUtil.fontOpenSansRegular36.getStringWidth(mode.get()) - 2),
                (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(mode.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(mode.get(), (float) (x + width - FontUtil.fontOpenSansRegular36.getStringWidth(mode.get()) - 2), (
                float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 200).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                mode.next();
            }

            if(button == 1) {
                mode.previous();
            }
        }

    }

    @Override
    public void mouseReleased(double x, double y, int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseMoved(double x, double y, int mouseX, int mouseY) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public boolean isVisible() {
        return mode.isVisible();
    }
}
