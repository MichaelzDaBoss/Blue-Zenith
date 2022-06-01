package cat.ui.clickgui;

import cat.BlueZenith;
import cat.module.ModuleCategory;
import cat.module.modules.render.ClickGUI;
import cat.ui.clickgui.window.CategoryWindow;
import cat.ui.clickgui.window.ConfigWindow;
import cat.ui.clickgui.window.TargetsWindow;
import cat.ui.clickgui.window.Window;
import cat.util.render.blur.KawaseBlur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {

    public ArrayList<Window> windows = new ArrayList<>();

    public static Window dragWindow;
    public static double dragX;
    public static double dragY;

    public ClickGui() {

        int counter = 0;
        for(ModuleCategory category : ModuleCategory.values()) {
            windows.add(new CategoryWindow(category, 5, 4 + counter * 16));
            counter++;
        }
        windows.add(new TargetsWindow(5, 4 + counter * 16));
        counter++;
        windows.add(new ConfigWindow(5, 4 + counter * 16));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drag(mouseX, mouseY);

        { // BEFORE

            Gui.drawRect(0, 0, this.width, this.height, 0x20000000);

            KawaseBlur.renderBlur(2, 6);

            this.drawGradientRect(0, 0, this.width, this.height-30, 0x60000000, reAlpha(ClickGUI.main_color, 155).getRGB());
            this.drawGradientRect(0, this.height-30, this.width, this.height, reAlpha(ClickGUI.main_color, 155).getRGB(), reAlpha(ClickGUI.main_color.darker(), 155).getRGB());

        }

        KawaseBlur.renderBlur(4, 4);

        { // PRE

            for(Window window : windows) {
                window.drawPre(mouseX, mouseY);
            }

        }

        KawaseBlur.renderBlur(4, 4);

        { // POST

            for(Window window : windows) {
                window.drawPost(mouseX, mouseY);
            }

        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        for(Window window : windows) {
            window.mouseClicked(mouseX, mouseY, mouseButton);
        }

    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for(Window window : windows) {
            window.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragWindow = null;

        for(Window window : windows) {
            window.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void onGuiClosed() {
        BlueZenith.moduleManager.getModule(ClickGUI.class).setState(false);
    }

    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drag(int mouseX, int mouseY) {
        if(dragWindow != null) {
            dragWindow.x = (int) (mouseX - dragX);
            dragWindow.y = (int) (mouseY - dragY);
        }
    }
}
