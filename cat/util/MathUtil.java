package cat.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

public class MathUtil {

    public static final SecureRandom RANDOM = new SecureRandom();

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double round(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }

    public static boolean contains(float x, float y, float minX, float minY, float maxX, float maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public static int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static boolean contains(double x, double y, double minX, double minY, double maxX, double maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

    public static int inRange(int value, int min, int max){
        return Math.max(Math.min(value, max), min);
    }

    public static float inRange(float value, float min, float max){
        return Math.max(Math.min(value, max), min);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }
}
