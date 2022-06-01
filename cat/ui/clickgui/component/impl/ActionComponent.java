package cat.ui.clickgui.component.impl;

import cat.module.modules.render.ClickGUI;
import cat.module.value.types.ActionValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Visible;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ActionComponent extends Component implements Visible {

    public ActionValue action;

    public ActionComponent(ActionValue action) {
        this.action = action;
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).brighter().getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(action.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 200).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                action.get().run();
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
