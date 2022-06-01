package cat.ui.clickgui.component.impl;

import cat.client.ConfigManager;
import cat.module.modules.render.ClickGUI;
import cat.module.value.types.ActionValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.window.ConfigWindow;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class ConfigComponent extends Component {

    public String name;
    public Runnable load;

    public ConfigComponent(String name) {
        this.name = name;
        load = () -> ConfigManager.load(name, ConfigWindow.ignoreBinds.get(), ConfigWindow.ignoreRender.get());
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(ClickGUI.main_color, 200).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            if(button == 0) {
                if(ConfigWindow.current == this) {
                    load.run();
                } else {
                    ConfigWindow.current = this;
                }
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

}
