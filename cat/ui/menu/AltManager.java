package cat.ui.menu;

import cat.client.ConfigManager;
import cat.ui.alt.AltLoginThread;
import cat.util.ColorUtil;
import cat.util.MathUtil;
import cat.util.MousePos;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class AltManager extends GuiScreen {

    public TFontRenderer frBig = FontUtil.fontSFLight62;

    public TextField mailField;
    public TextField passwordField;
    public TextField randomOfflineNameField;

    public AltLoginThread thread;

    public Button loginRandom;

    public EnumAltManagerTab tab;

    @Override
    public void initGui() {

        boolean boo;
        boo = randomOfflineNameField != null;
        if(boo) {
            ConfigManager.saveAlts(randomOfflineNameField.getText());
        }

        tab = EnumAltManagerTab.ALTS_LIST;

        loginRandom = new Button("Random Offline", this, 115, 135, 215, 150) {
            @Override
            public void click() {
                thread = new AltLoginThread(getRandomOfflineName());
            }
        };

        ScaledResolution sr = new ScaledResolution(mc);

        randomOfflineNameField = new TextField(7271, mc.fontRendererObj, 115, 115, 215, 130);
        randomOfflineNameField.setMaxStringLength(9);
        randomOfflineNameField.setText(boo? ConfigManager.loadAlts() : "BZR");
    }

    public String getRandomOfflineName() {
        return randomOfflineNameField.getText() + "_" + getRandomElement(CHARS) + getRandomElement(CHARS) + getRandomElement(CHARS) + getRandomElement(CHARS) + getRandomElement(CHARS) + getRandomElement(CHARS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution sr = new ScaledResolution(mc);

        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
        GlStateManager.resetColor();
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColorAlt(3).getRGB());
        GlStateManager.resetColor();

        loginRandom.click(mouseX, mouseY);

        drawPre(sr, new MousePos(mouseX, mouseY, partialTicks));

        if(tab == EnumAltManagerTab.DIRECT_LOGIN) {
            randomOfflineNameField.pre();
            randomOfflineNameField.drawTextBox();
            loginRandom.drawPre();
        }

        BlurUtil.blurAll(8);

        drawPost(sr, new MousePos(mouseX, mouseY, partialTicks));

        if(tab == EnumAltManagerTab.DIRECT_LOGIN) {
            randomOfflineNameField.post();
            randomOfflineNameField.drawTextBox();
            loginRandom.drawPost();
        }

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if(tab == EnumAltManagerTab.DIRECT_LOGIN) {
            randomOfflineNameField.textboxKeyTyped(typedChar, keyCode);
        }

    }



    public void drawPre(ScaledResolution sr, MousePos pos) {

        rect(100, 70, sr.getScaledWidth()-100, 100, 0x20000000);
        rect(100, 100, sr.getScaledWidth()-100, sr.getScaledHeight()-70, 0x70000000);

        // Alt List
        rect(100, 70, (sr.getScaledWidth()-200)/3f+100, 100, tab == EnumAltManagerTab.ALTS_LIST? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Alts", 100, 70, (sr.getScaledWidth()-200)/3f+100, 100, 0x69FFFFFF, true);

        // Add Alt
        rect((sr.getScaledWidth()-200)/3f+100, 70, ((sr.getScaledWidth()-200)/3f)*2f+100, 100, tab == EnumAltManagerTab.ADD_ALT? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Add Alt", (sr.getScaledWidth()-200)/3f+100, 70, ((sr.getScaledWidth()-200)/3f)*2f+100, 100, 0x69FFFFFF, true);

        // Direct Login
        rect(((sr.getScaledWidth()-200)/3f)*2f+100, 70, sr.getScaledWidth()-100, 100, tab == EnumAltManagerTab.DIRECT_LOGIN? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Login", ((sr.getScaledWidth()-200)/3f)*2f+100, 70, sr.getScaledWidth()-100, 100, 0x69FFFFFF, true);

        switch(tab) {

            case ALTS_LIST:

                drawCenteredString(frBig, "There will be alts here at some point!", 100, 100, sr.getScaledWidth()-100, sr.getScaledHeight()-70, 0x69FFFFFF, true);

                break;

        }

    }

    public void drawPost(ScaledResolution sr, MousePos pos) {

        rect(100, 70, sr.getScaledWidth()-100, 100, 0x50000000);
        rect(100, 100, sr.getScaledWidth()-100, sr.getScaledHeight()-70, 0x70000000);

        // Alt List
        rect(100, 70, (sr.getScaledWidth()-200)/3f+100, 100, tab == EnumAltManagerTab.ALTS_LIST? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Alts", 100, 70, (sr.getScaledWidth()-200)/3f+100, 100, 0x99FFFFFF, true);

        // Add Alt
        rect((sr.getScaledWidth()-200)/3f+100, 70, ((sr.getScaledWidth()-200)/3f)*2f+100, 100, tab == EnumAltManagerTab.ADD_ALT? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Add Alt", (sr.getScaledWidth()-200)/3f+100, 70, ((sr.getScaledWidth()-200)/3f)*2f+100, 100, 0x99FFFFFF, true);

        // Direct Login
        rect(((sr.getScaledWidth()-200)/3f)*2f+100, 70, sr.getScaledWidth()-100, 100, tab == EnumAltManagerTab.DIRECT_LOGIN? 0x30000000 : 0x10000000);
        drawCenteredString(frBig, "Login", ((sr.getScaledWidth()-200)/3f)*2f+100, 70, sr.getScaledWidth()-100, 100, 0x99FFFFFF, true);

        switch(tab) {

            case ALTS_LIST:
                drawCenteredString(frBig, "There will be alts here at some point!", 100, 100, sr.getScaledWidth()-100, sr.getScaledHeight()-70, 0x99FFFFFF, true);
                break;

            case DIRECT_LOGIN:

                break;

        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        ScaledResolution sr = new ScaledResolution(mc);

        if(MathUtil.contains(mouseX, mouseY, 100, 70, (sr.getScaledWidth()-200)/3f+100, 100)) {
            tab = EnumAltManagerTab.ALTS_LIST;
        }

        if(MathUtil.contains(mouseX, mouseY, (sr.getScaledWidth()-200)/3f+100, 70, ((sr.getScaledWidth()-200)/3f)*2f+100, 100)) {
            tab = EnumAltManagerTab.ADD_ALT;
        }

        if(MathUtil.contains(mouseX, mouseY, ((sr.getScaledWidth()-200)/3f)*2f+100, 70, sr.getScaledWidth()-100, 100)) {
            tab = EnumAltManagerTab.DIRECT_LOGIN;
        }

        if(tab == EnumAltManagerTab.DIRECT_LOGIN) {
            randomOfflineNameField.mouseClicked(mouseX, mouseY, mouseButton);
            loginRandom.click(mouseX, mouseY);
        }


    }

    public void rect(double x, double y, double x2, double y2, Color color) {
        Gui.drawRect(x, y, x2, y2, color.getRGB());
        GlStateManager.resetColor();
    }

    public void rect(double x, double y, double x2, double y2, int color) {
        Gui.drawRect(x, y, x2, y2, color);
        GlStateManager.resetColor();
    }

    public void drawCenteredString(TFontRenderer font, String string, float x1, float y1, float x2, float y2, Color color, boolean shadow) {
        font.drawString(string, x1 + ((x2 - x1)/2f - (font.getStringWidth(string)/2f)), y1 + ((y2 - y1)/2f - (font.getHeight(string)/2f)), color.getRGB(), shadow);
        GlStateManager.resetColor();
    }

    public void drawCenteredString(TFontRenderer font, String string, float x1, float y1, float x2, float y2, int color, boolean shadow) {
        font.drawString(string, x1 + ((x2 - x1)/2f - (font.getStringWidth(string)/2f)), y1 + ((y2 - y1)/2f - (font.getHeight(string)/2f)), color, shadow);
        GlStateManager.resetColor();
    }

    public static String getRandomElement(String[] arr){
        return arr[ThreadLocalRandom.current().nextInt(arr.length)];
    }



    public class Button {

        public double x1, y1, x2, y2;
        public String title;

        public final int BUTTON_TEXT_PRE = 0x70FFFFFF;
        public final int BUTTON_TEXT_POST = 0x80FFFFFF;
        public final TFontRenderer FONT_RENDERER = FontUtil.fontSFLight36;

        public GuiScreen parent;

        public int hover = 0;

        public Button(String title, GuiScreen parent, double x1, double y1, double x2, double y2) {
            this.title = title;
            this.parent = parent;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public void drawPre() {
            Gui.drawRect(x1, y1, x2, y2, new Color(0, 0, 0, 40 + hover*3).getRGB());
            GlStateManager.resetColor();
            FONT_RENDERER.drawString(title, (float) (x1 + (((x2-x1)/2)-FONT_RENDERER.getStringWidth(title)/2)), (float) (y1 + (((y2-y1)/2)-FONT_RENDERER.getHeight(title)/2)), BUTTON_TEXT_PRE, true);
            GlStateManager.resetColor();
        }

        public void drawPost() {
            Gui.drawRect(x1, y1, x2, y2, new Color(0, 0, 0, 60 + hover*3).getRGB());
            GlStateManager.resetColor();
            FONT_RENDERER.drawString(title, (float) (x1 + (((x2-x1)/2)-FONT_RENDERER.getStringWidth(title)/2)), (float) (y1 + (((y2-y1)/2)-FONT_RENDERER.getHeight(title)/2)), BUTTON_TEXT_POST, true);
            GlStateManager.resetColor();
            FONT_RENDERER.drawString(title, (float) (x1 + (((x2-x1)/2)-FONT_RENDERER.getStringWidth(title)/2)), (float) (y1 + (((y2-y1)/2)-FONT_RENDERER.getHeight(title)/2)), BUTTON_TEXT_POST, true);
            GlStateManager.resetColor();
            FONT_RENDERER.drawString(title, (float) (x1 + (((x2-x1)/2)-FONT_RENDERER.getStringWidth(title)/2)), (float) (y1 + (((y2-y1)/2)-FONT_RENDERER.getHeight(title)/2)), BUTTON_TEXT_POST, true);
            GlStateManager.resetColor();
        }

        public void click(int x, int y) {
            if(MathUtil.contains(x, y, x1, y1, x2, y2)) {
                click();
            }
        }

        public void click() {

        }

        public void tick(int x, int y) {
            tick(MathUtil.contains(x, y, x1, y1, x2, y2));
        }

        public void tick(boolean hovered) {
            if(hovered) {
                if(hover < 20) {
                    hover++;
                } else {
                    hover = 20;
                }
            } else {
                if(hover > 0) {
                    hover--;
                } else {
                    hover = 0;
                }
            }
        }
    }

    public static final String[] CHARS = {
            "a", "A", "b", "B", "c", "C",
            "d", "D", "e", "E", "f", "F",
            "g", "G", "h", "H", "i", "I",
            "j", "J", "k", "K", "l", "L",
            "m", "M", "n", "N", "o", "P",
            "q", "Q", "r", "R", "s", "S",
            "t", "T", "u", "U", "v", "V",
            "w", "W", "x", "X", "y", "Y",
            "z", "Z", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "0"
    };

}

enum EnumAltManagerTab {
    ALTS_LIST,
    DIRECT_LOGIN,
    ADD_ALT;
}
