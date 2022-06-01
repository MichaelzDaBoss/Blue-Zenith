package cat.ui.clickgui.component.impl;

import cat.module.Module;
import cat.module.modules.render.ClickGUI;
import cat.module.value.Value;
import cat.module.value.types.*;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Expandable;
import cat.ui.clickgui.component.Visible;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class ListComponent extends Component implements Expandable, Visible {

    public ListValue value;
    private boolean expanded;
    boolean arrow;

    ArrayList<Component> components = new ArrayList<>();

    public ListComponent(ListValue value) {
        this.value = value;

        for(BooleanValue b : value.choices) {
            components.add(new CheckboxComponent(b, true));
        }

        arrow = !components.isEmpty();
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor.brighter(), 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        if(arrow) {
            FontUtil.I_testFont2.drawString(expanded ? "B" : "C", (float) (x + width - FontUtil.I_testFont2.getStringWidth("C") - 2), (float) (y + (height / 2f - (FontUtil.I_testFont2.FONT_HEIGHT / 2f))),
                    ClickGui.reAlpha(Color.WHITE, 100).getRGB());
            GlStateManager.resetColor();
        }
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor.brighter(), 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(value.name, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(Color.WHITE, 200).getRGB());
        GlStateManager.resetColor();

        if(arrow) {
            FontUtil.I_testFont2.drawString(expanded ? "B" : "C", (float) (x + width - FontUtil.I_testFont2.getStringWidth("C") - 2), (float) (y + (height / 2f - (FontUtil.I_testFont2.FONT_HEIGHT / 2f))),
                    ClickGui.reAlpha(Color.WHITE, 200).getRGB());
            GlStateManager.resetColor();
        }
    }

    @Override
    public void mouseClicked(double x, double y, int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            expanded = !expanded;
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
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public ArrayList<Component> getComponents() {
        return components;
    }

    @Override
    public boolean isVisible() {
        return value.isVisible();
    }
}
