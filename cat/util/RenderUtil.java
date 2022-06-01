package cat.util;

import cat.module.modules.render.ClickGUI;
import cat.util.render.blur.KawaseBlur;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ConcurrentModificationException;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil extends MinecraftInstance {
    private static final ResourceLocation shader = new ResourceLocation("cat/blur.json");
    public static int delta = 0;
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static Framebuffer buffer;
    private static ShaderGroup blurShader;

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height, float alpha) {
        GlStateManager.pushMatrix();
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1.0F, 1.0F, 1.0F, alpha);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.popMatrix();
    }

    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        speed = range(speed * 10 / delta, 0, 1);
        float dif = Math.max(target, current) - Math.min(target, current);
        float factor = dif * speed;
        if (factor < 0.001f)
            factor = 0.001f;
        if (larger) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
    }

    public static float range(float v, float min, float max) {
        return Math.max(Math.min(v, max), min);
    }

    public static void rect(final float x, final float y, final float x2, final float y2, final int color) {
        Gui.drawRect(x, y, x2, y2, color);
        GlStateManager.resetColor();
    }

    public static void rect(final float x, final float y, final float x2, final float y2, final Color color) {
        Gui.drawRect(x, y, x2, y2, color.getRGB());
        GlStateManager.resetColor();
    }

    public static void rect(final double x, final double y, final double x2, final double y2, final Color color) {
        Gui.drawRect(x, y, x2, y2, color.getRGB());
        GlStateManager.resetColor();
    }

    public static void crop(final float x, final float y, final float x2, final float y2) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int factor = scaledResolution.getScaleFactor();
        glScissor((int) (x * factor), (int) ((scaledResolution.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            buffer = blurShader.mainFramebuffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void blur(float x, float y, float x2, float y2, ScaledResolution sc) {
        int factor = sc.getScaleFactor();
        int factor2 = sc.getScaledWidth();
        int factor3 = sc.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null
                || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        crop(x, y, x2, y2);
        buffer.framebufferHeight = mc.displayHeight;
        buffer.framebufferWidth = mc.displayWidth;
        GlStateManager.resetColor();
        blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
        buffer.bindFramebuffer(true);
        mc.getFramebuffer().bindFramebuffer(true);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void blur(float x, float y, float x2, float y2) {
        GlStateManager.disableAlpha();
        blur(x, y, x2, y2, new ScaledResolution(mc));
        GlStateManager.enableAlpha();
    }

    public static void blurKawase(float x, float y, float x2, float y2, int iterations, int offset) {
        GlStateManager.disableAlpha();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        crop(x, y, x2, y2);
        KawaseBlur.renderBlur(iterations, offset);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableAlpha();
    }

    public static void drawScaledFont(FontRenderer f, String text, float x, float y, int color, boolean shadow, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        f.drawString(text, 0, 0, color, shadow);
        GlStateManager.popMatrix();
        f.getStringWidthF(text);
    }

    public static void glColor(final Color color) {
        final float red = color.getRed() / 255F;
        final float green = color.getGreen() / 255F;
        final float blue = color.getBlue() / 255F;
        final float alpha = color.getAlpha() / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    private static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255F;
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void renderBreadCrumb(final Vec3 vec3) {

        GlStateManager.disableDepth();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        try {

            final double x = vec3.xCoord - (mc.getRenderManager()).renderPosX;
            final double y = vec3.yCoord - (mc.getRenderManager()).renderPosY;
            final double z = vec3.zCoord - (mc.getRenderManager()).renderPosZ;

            final double distanceFromPlayer = mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord - 1, vec3.zCoord);
            int quality = (int) (distanceFromPlayer * 4 + 10);

            if (quality > 350)
                quality = 350;

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            final float scale = 0.04f;
            GL11.glScalef(-scale, -scale, -scale);

            GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
            GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

            final Color c = ClickGUI.main_color;

            RenderUtil.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

            if (distanceFromPlayer < 4)
                RenderUtil.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

            if (distanceFromPlayer < 20)
                RenderUtil.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);


            GL11.glScalef(0.8f, 0.8f, 0.8f);

            GL11.glPopMatrix();


        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.enableDepth();

        GL11.glColor3d(255, 255, 255);
    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

}
