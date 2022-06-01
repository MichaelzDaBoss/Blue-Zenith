package cat.ui.clickgui.component.impl;

import cat.module.modules.render.ClickGUI;
import cat.module.value.types.ActionValue;
import cat.module.value.types.BooleanValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Visible;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class CheckboxComponent extends Component implements Visible {

    public BooleanValue action;
    final boolean evenLighter;

    public CheckboxComponent(BooleanValue action) {
        this.action = action;
        this.evenLighter = false;
    }

    public CheckboxComponent(BooleanValue action, boolean evenLighter) {
        this.action = action;
        this.evenLighter = evenLighter;
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, lighter(ClickGui.reAlpha(ClickGUI.backgroundColor, 100)).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.I_testFont2.drawString(action.get()? "F" : "E", (float) (x + width - FontUtil.I_testFont2.getStringWidth("E") - 2), (float) (y + (height/2f-(FontUtil.I_testFont2.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(action.get()? ClickGUI.main_color : Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, lighter(ClickGui.reAlpha(ClickGUI.backgroundColor, 200)).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.I_testFont2.drawString(action.get()? "F" : "E", (float) (x + width - FontUtil.I_testFont2.getStringWidth("E") - 2), (float) (y + (height/2f-(FontUtil.I_testFont2.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(action.get()? ClickGUI.main_color : Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();
    }

    Color lighter(Color color) {
        if(evenLighter) {
            return color.brighter();
        }
        return color;
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                action.set(!action.get());
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
        return action.isVisible();
    }
}
