package cat.ui.clickgui.component.impl;

import cat.module.modules.render.ClickGUI;
import cat.module.value.types.IntegerValue;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Visible;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class IntSliderComponent extends Component implements Visible {

    public IntegerValue value;

    public IntSliderComponent(IntegerValue value) {
        this.value = value;
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).brighter().getRGB());
        GlStateManager.resetColor();

        Gui.drawRect(x, y, x + (width*multi()), y + height, ClickGui.reAlpha(ClickGUI.main_color, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))) - 1,
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.get().toString(), (float) (x + width - FontUtil.fontOpenSansRegular36.getStringWidth(value.get().toString()) - 2) - 1,
                (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).brighter().getRGB());
        GlStateManager.resetColor();

        Gui.drawRect(x, y, x + (width*multi()), y + height, ClickGui.reAlpha(ClickGUI.main_color, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))) - 1,
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.get().toString(), (float) (x + width - FontUtil.fontOpenSansRegular36.getStringWidth(value.get().toString()) - 2) - 1,
                (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            double inc = makePrecise(((double) mouseX - x) / ((x + width) - x), 4);
            double off = makePrecise(value.min + ((value.max - value.min) * inc), 3);
            value.setValuePrecise((int) makePrecise(off, 3));
        }

    }

    @Override
    public void mouseReleased(double x, double y, int mouseX, int mouseY, int button) {

    }

    @Override
    public void mouseMoved(double x, double y, int mouseX, int mouseY) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            double inc = makePrecise(((double) mouseX - x) / ((x + width) - x), 4);
            double off = makePrecise(value.min + ((value.max - value.min) * inc), 3);
            value.setValuePrecise((int) makePrecise(off, 3));
        }

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    public static double makePrecise(double value, int precision) {
        double pow = Math.pow(10, precision);
        long powValue = Math.round(pow * value);
        return powValue / pow;
    }

    double multi() {
        return ((double) value.get() - (double) value.min) / ((double) value.max - (double) value.min);
    }

    @Override
    public boolean isVisible() {
        return value.isVisible();
    }
}
