package cat.ui.clickgui.window;

import cat.BlueZenith;
import cat.module.ModuleCategory;
import cat.module.modules.render.ClickGUI;
import cat.ui.clickgui.ClickGui;
import cat.ui.clickgui.component.Component;
import cat.ui.clickgui.component.Expandable;
import cat.ui.clickgui.component.Visible;
import cat.ui.clickgui.component.impl.ModuleComponent;
import cat.ui.clickgui.component.impl.TargetComponent;
import cat.util.EntityManager;
import cat.util.font.sigma.FontUtil;
import fr.lavache.anime.Animate;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class TargetsWindow extends Window {

    public ArrayList<Component> components = new ArrayList<>();

    public TargetsWindow(int x, int y) {
        super(x, y);

        for(EntityManager.Targets target : EntityManager.Targets.values()) {
            components.add(new TargetComponent(target));
        }
    }

    @Override
    public void drawPre(int mouseX, int mouseY) {

        Gui.drawRect(x - 2, y, x + width + 2, y + height, ClickGui.reAlpha(ClickGUI.main_color, 100).getRGB());
        GlStateManager.resetColor();
        FontUtil.fontOpenSansRegular36.drawString("Targets", (float) (x + (width/2-(FontUtil.fontOpenSansRegular36.getStringWidth("Targets")/2))),
                (float) (y + (height/2-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2))), ClickGui.reAlpha(Color.WHITE, 100).getRGB(), false);
        GlStateManager.resetColor();

        if(!expanded) {
            return;
        }

        double currentHeight = height;

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.drawPre(x, y + currentHeight, mouseX, mouseY);
            currentHeight += Component.height;

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.drawPre(x, y + currentHeight, mouseX, mouseY);
                        currentHeight += Component.height;
                    }
                }
            }
        }

    }

    @Override
    public void drawPost(int mouseX, int mouseY) {

        Gui.drawRect(x - 2, y, x + width + 2, y + height, ClickGui.reAlpha(ClickGUI.main_color, 200).getRGB());
        GlStateManager.resetColor();
        FontUtil.fontOpenSansRegular36.drawString("Targets", (float) (x + (width/2-(FontUtil.fontOpenSansRegular36.getStringWidth("Targets")/2))),
                (float) (y + (height/2-(FontUtil.fontOpenSansRegular36.FONT_HEIGHT/2))), ClickGui.reAlpha(Color.WHITE, 200).getRGB(), false);
        GlStateManager.resetColor();

        if(!expanded) {
            return;
        }

        double currentHeight = height;

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.drawPost(x, y + currentHeight, mouseX, mouseY);
            currentHeight += Component.height;

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.drawPost(x, y + currentHeight, mouseX, mouseY);
                        currentHeight += Component.height;
                    }
                }
            }
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {

            if(button == 0) {
                ClickGui.dragWindow = this;
                ClickGui.dragX = mouseX - x;
                ClickGui.dragY = mouseY - y;
            }

            if(button == 1) {
                expanded = !expanded;
            }

        }

        if(!expanded) {
            return;
        }

        double currentHeight = height;

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.mouseClicked(x, y + currentHeight, mouseX, mouseY, button);
            currentHeight += Component.height;

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.mouseClicked(x, y + currentHeight, mouseX, mouseY, button);
                        currentHeight += Component.height;
                    }
                }
            }
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {

        if(!expanded) {
            return;
        }

        double currentHeight = height;

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.mouseReleased(x, y + currentHeight, mouseX, mouseY, button);
            currentHeight += Component.height;

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.mouseReleased(x, y + currentHeight, mouseX, mouseY, button);
                        currentHeight += Component.height;
                    }
                }
            }
        }

    }

    @Override
    public void mouseMoved(int mouseX, int mouseY) {

        if(!expanded) {
            return;
        }

        double currentHeight = height;

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.mouseMoved(x, y + currentHeight, mouseX, mouseY);
            currentHeight += Component.height;

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.mouseMoved(x, y + currentHeight, mouseX, mouseY);
                        currentHeight += Component.height;
                    }
                }
            }
        }

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

        if(!expanded) {
            return;
        }

        for(Component component : components) {
            if(component instanceof Visible) {
                if(!((Visible) component).isVisible()) {
                    continue;
                }
            }

            component.keyTyped(typedChar, keyCode);

            if(component instanceof Expandable) {
                if(((Expandable) component).isExpanded()) {
                    for(Component subComponent : ((Expandable) component).getComponents()) {
                        if(subComponent instanceof Visible) {
                            if(!((Visible) subComponent).isVisible()) {
                                continue;
                            }
                        }

                        subComponent.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }

    }
}
