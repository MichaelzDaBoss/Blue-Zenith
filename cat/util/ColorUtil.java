package cat.util;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern COLOR_PATT = Pattern.compile("(?i)§[0-9A-FK-OR]");
    public static Color rainbow(float g, float m){
        double delay = (Math.abs(System.currentTimeMillis() / 20L) / 100.0 + 6.0F * ((g * m) + 2.55) / 60) % 1;
        final double n3 = 1.0 - delay;
        return Color.getHSBColor((float) n3, 0.6f, 1);
    }
    public static Color getMainColor(){
        return new Color(11,120,252);
    }
    public static Color getBackgroundColor(){
        return new Color(0,0,69);
    }
    public static Color getEpicColor(int secs){
        Color color = getBackgroundColor();
        Color color2 = getMainColor();
        double delay = Math.abs(System.currentTimeMillis() / 20L) / 100.0 + 6.0F * ((secs * 2) + 2.55) / 60;
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }

    public static Color getEpicColorAlt(int secs){
        Color color = getBackgroundColor();
        Color color2 = getMainColor();
        double delay = (Math.abs(System.currentTimeMillis() / 20L) / 100.0 + 6.0F * (2.55) / 60) / secs;
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }

    public static String removeGay(String text){
        return text.replaceAll(COLOR_PATT.pattern(), "");
    }
    public static String getLastColor(String text){
        Matcher m = COLOR_PATT.matcher(text);
        return m.find() ? m.group(m.groupCount()) : "§r";
    }
    public static String getFirstColor(String text){
        Matcher m = COLOR_PATT.matcher(text);
        return m.find() ? m.group(1) : "§r";
    }
}
