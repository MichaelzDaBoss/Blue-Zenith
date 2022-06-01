package cat.ui.clickgui.component.impl;

import cat.module.modules.render.ClickGUI;
import cat.module.value.types.BooleanValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Visible;
import cat.util.EntityManager;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class TargetComponent extends Component implements Visible {

    public EntityManager.Targets action;

    public TargetComponent(EntityManager.Targets action) {
        this.action = action;
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.displayName, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.I_testFont2.drawString(action.on? "F" : "E", (float) (x + width - FontUtil.I_testFont2.getStringWidth("E") - 2), (float) (y + (height/2f-(FontUtil.I_testFont2.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(action.on? ClickGUI.main_color : Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.displayName, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.I_testFont2.drawString(action.on? "F" : "E", (float) (x + width - FontUtil.I_testFont2.getStringWidth("E") - 2), (float) (y + (height/2f-(FontUtil.I_testFont2.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(action.on? ClickGUI.main_color : Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                action.on = !action.on;
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
        return true;
    }
}
