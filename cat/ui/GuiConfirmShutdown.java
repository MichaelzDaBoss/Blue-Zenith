package cat.ui;

import cat.BlueZenith;
import cat.util.ColorUtil;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class GuiConfirmShutdown extends GuiScreen {

    private final GuiScreen parentScreen;
    public GuiConfirmShutdown(GuiScreen parent) {
        parentScreen = parent;
    }
    @Override
    public void initGui() {
        BlueZenith.updateRPC("Shutting down the game...", "");
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width/2 - 155, this.height/2, 150, 20, "Yes"));
        this.buttonList.add(new GuiButton(1, this.width/2, this.height/2, 150, 20, "No, take me back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String a = "Are you sure you want to quit?";
        ScaledResolution sc = new ScaledResolution(mc);
        FontRenderer font = FontUtil.fontSFLight42;
        //RenderUtil.drawImage(bg, 0, 0, this.width, this.height, 1);
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(),ColorUtil.getMainColor().getRGB());
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        font.drawStringWithShadow(a, sc.getScaledWidth()/2f - font.getStringWidth(a)/2f, sc.getScaledHeight()/3f, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case 1:
                mc.displayGuiScreen(parentScreen);
            break;

            case 0:
                if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                mc.displayGuiScreen(new GoodbyeScreen());
                else mc.shutdown();
            break;
        }
    }
}
