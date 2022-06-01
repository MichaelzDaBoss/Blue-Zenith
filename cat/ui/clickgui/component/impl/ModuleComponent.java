package cat.ui.clickgui.component.impl;

import cat.module.Module;
import cat.module.modules.render.ClickGUI;
import cat.module.value.Value;
import cat.module.value.types.*;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Expandable;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class ModuleComponent extends Component implements Expandable {

    public Module module;
    private boolean expanded;
    boolean arrow;

    ArrayList<Component> components = new ArrayList<>();

    public ModuleComponent(Module module) {
        this.module = module;

        for(Value<?> value : module.getValues()) {

            if(value instanceof ActionValue) {
                components.add(new ActionComponent((ActionValue) value));
            }

            if(value instanceof BooleanValue) {
                components.add(new CheckboxComponent((BooleanValue) value));
            }

            if(value instanceof ModeValue) {
                components.add(new ModeComponent((ModeValue) value));
            }

            if(value instanceof IntegerValue) {
                components.add(new IntSliderComponent((IntegerValue) value));
            }

            if(value instanceof FloatValue) {
                components.add(new FloatSliderComponent((FloatValue) value));
            }

            if(value instanceof FontValue) {
                components.add(new FontComponent((FontValue) value));
            }

            if(value instanceof ListValue) {
                components.add(new ListComponent((ListValue) value));
            }

        }

        arrow = !components.isEmpty();
    }

    @Override
    public void drawPre(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 100).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(module.displayName, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(module.getState()? ClickGUI.main_color : Color.WHITE, 100).getRGB());
        GlStateManager.resetColor();

        if(arrow) {
            FontUtil.I_testFont2.drawString(expanded ? "B" : "C", (float) (x + width - FontUtil.I_testFont2.getStringWidth("C") - 2), (float) (y + (height / 2f - (FontUtil.I_testFont2.FONT_HEIGHT / 2f))),
                    ClickGui.reAlpha(Color.WHITE, 100).getRGB());
            GlStateManager.resetColor();
        }
    }

    @Override
    public void drawPost(double x, double y, int mouseX, int mouseY) {
        Gui.drawRect((int) x, (int) y, (int) x + width, (int) y + height, ClickGui.reAlpha(ClickGUI.backgroundColor, 200).getRGB());
        GlStateManager.resetColor();

        FontUtil.fontOpenSansRegular36.drawString(module.displayName, (float) x + 2, (float) (y + (height/2f-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2f))),
                ClickGui.reAlpha(module.getState()? ClickGUI.main_color : Color.WHITE, 200).getRGB());
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
            if(button == 0) {
                module.toggle();
            }

            if(button == 1) {
                expanded = !expanded;
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
}
