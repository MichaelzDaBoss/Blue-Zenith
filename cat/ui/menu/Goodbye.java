package cat.ui.menu;

import cat.util.ColorUtil;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Goodbye extends GuiScreen {

    public static final TFontRenderer fr = FontUtil.fontSFLight62;

    boolean dreamLucky;
    private final String message;

    int ticks = 1;

    public Goodbye() {
        dreamLucky = new Random().nextInt(10000) <= 10;
        message = dreamLucky? "You're dream-lucky today. Hope you didn't set your volume too high." : messages[new Random().nextInt(messages.length)];
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ticks++;

        ScaledResolution sr = new ScaledResolution(mc);

        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
        GlStateManager.resetColor();
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColorAlt(3).getRGB());
        GlStateManager.resetColor();

        fr.drawString(message, sr.getScaledWidth()/2f - (fr.getStringWidth(message)/2f), 110, 0x70FFFFFF, true);
        GlStateManager.resetColor();

        BlurUtil.blurAll(8);

        fr.drawString(message, sr.getScaledWidth()/2f - (fr.getStringWidth(message)/2f), 110, 0x80FFFFFF, true);
        GlStateManager.resetColor();

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0,0, 0, Math.min(ticks, 255)).getRGB());
        GlStateManager.resetColor();

        if(ticks >= 255)
            mc.shutdown();

    }

    private final static String[] messages = {
            "See you later.",
            "We laughed until we had to cry, we loved right down to our last goodbye, we were the best.",
            "A farewell is necessary before we can meet again, and meeting again, after moments or a lifetime, is certain for those who are friends.",
            "Great is the art of beginning, but greater is the art of ending.",
            "The two hardest things to say in life is hello for the first time and goodbye for the last.",
            "We started with a simple hello, but ended with a complicated goodbye.",
            "You have been my friend. That in itself is a tremendous thing.",
            "No distance of place or lapse of time can lessen the friendship of those who are thoroughly persuaded of each other’s worth.",
            "The pain of parting is nothing to the joy of meeting again.",
            "Good friends never say goodbye. They simply say ‘See you soon.’",
            "This is not a goodbye, my darling, this is a thank you."
    };

}