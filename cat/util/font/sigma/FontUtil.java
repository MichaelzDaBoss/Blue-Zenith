package cat.util.font.sigma;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class FontUtil {
    public static ArrayList<FontRenderer> fonts = new ArrayList<>();
    public static FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    public static TFontRenderer fontJetBrainsLight36 = new TFontRenderer(getEpicFont("jet-brains-mono-light", 32));
    public static TFontRenderer fontJetBrainsLight68 = new TFontRenderer(getEpicFont("jet-brains-mono-light", 68));
    public static TFontRenderer fontSegoeUI28 = new TFontRenderer(getEpicFont("segoe-ui", 28));
    public static TFontRenderer fontComicSans42 = new TFontRenderer(getEpicFont("comic-sans-ms", 42));
    public static TFontRenderer fontSFLight35 = new TFontRenderer(getEpicFont("sf-ui-display-light", 35));
    public static TFontRenderer fontSFLight42 = new TFontRenderer(getEpicFont("sf-ui-display-light", 42));
    public static TFontRenderer fontSFLight36 = new TFontRenderer(getEpicFont("sf-ui-display-light", 36));
    public static TFontRenderer fontSFLight62 = new TFontRenderer(getEpicFont("sf-ui-display-light", 62));
    public static TFontRenderer I_icon = new TFontRenderer(getEpicFont("icons", 80));
    public static TFontRenderer I_testFont = new TFontRenderer(getEpicFont("test-font", 20));
    public static TFontRenderer I_testFont2 = new TFontRenderer(getEpicFont("test-font", 28));
	public static TFontRenderer fontOpenSansRegular36 = new TFontRenderer(getEpicFont("opensans-regular", 36));

    static {
        for (Field i : FontUtil.class.getDeclaredFields()) {
            i.setAccessible(true);
            Object o = null;
            try {
                o = i.get(FontUtil.class);
            } catch (IllegalAccessException ignored) {
            }
            if (o instanceof FontRenderer && !i.getName().startsWith("I_")) {
                fonts.add((FontRenderer) o);
            }
        }
    }

    private static Font getEpicFont(String str, float size) {
        Font font = new Font("default", Font.PLAIN, (int) (size / 2));
        try {
            InputStream is = FontUtil.class.getResourceAsStream("/assets/minecraft/ttf/" + str + ".ttf");
            if (is == null) {
                throw new Exception("Font path does not exist. " + str);
            }
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(Font.PLAIN, size / 2f);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
        }
        return font;
    }
}
