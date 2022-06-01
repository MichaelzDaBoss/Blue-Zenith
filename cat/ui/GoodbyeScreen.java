package cat.ui;

import cat.BlueZenith;
import cat.util.ColorUtil;
import cat.util.MillisTimer;
import cat.util.font.sigma.FontUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GoodbyeScreen extends GuiScreen {
    private int faggots = 1;
    private String a = nigger[new Random().nextInt(nigger.length)];
    private final boolean isDreamLuck;
    private final MillisTimer dreamluckTimer = new MillisTimer();

    public GoodbyeScreen() {
        isDreamLuck = new Random().nextInt(100) <= 10;

    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(mc);
        FontRenderer font = FontUtil.fontSFLight42;
        if(isDreamLuck) {
            a = "You're dream-lucky today. Hope you didn't set your volume too high.";
            drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
            drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
            if(dreamluckTimer.hasTimeReached(500)) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("hi")));
                dreamluckTimer.reset();
            }
            font.drawStringWithShadow(a, res.getScaledWidth()/2f - font.getStringWidth(a)/2f, res.getScaledHeight()/2f, new Color(255, 255, 255, 255 - faggots).getRGB());
            return;
        }
        faggots += 2;
        if(faggots >= 255)
            faggots = 255;
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, faggots).getRGB());
        font.drawStringWithShadow(a, res.getScaledWidth()/2f - font.getStringWidth(a)/2f, res.getScaledHeight()/2f, new Color(255, 255, 255, 255 - faggots).getRGB());
    }

    @Override
    public void initGui() {
        if(!isDreamLuck)
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("shutdown")));
        BlueZenith.scheduledExecutorService.schedule(() -> mc.shutdown(), 4000, TimeUnit.MILLISECONDS);
    }

    private final static String[] nigger = {
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