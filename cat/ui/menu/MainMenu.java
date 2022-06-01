package cat.ui.menu;

import cat.ui.GuiConfirmShutdown;
import cat.ui.alt.GuiAltLogin;
import cat.util.ColorUtil;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainMenu extends GuiScreen {

    public static final int BUTTON_PRE = 0x40000000, BUTTON_POST = 0x60000000, BUTTON_TEXT_PRE = 0x70FFFFFF, BUTTON_TEXT_POST = 0x80FFFFFF;
    public static final TFontRenderer FONT_RENDERER = FontUtil.fontSFLight36;

    public ArrayList<Button> buttons = new ArrayList<>();

    public MainMenu() {}

    @Override
    public void initGui() {

        ScaledResolution sr = new ScaledResolution(mc);

        buttons.clear();

        buttons.add(new Button("Singleplayer", this, sr.getScaledWidth()/2f - 50, sr.getScaledHeight()/2f - 30, sr.getScaledWidth()/2f + 50, sr.getScaledHeight()/2f - 10) {
            @Override
            public void click() {
                mc.displayGuiScreen(new GuiSelectWorld(parent));
            }
        });

        buttons.add(new Button("Multiplayer", this, sr.getScaledWidth()/2f - 50, sr.getScaledHeight()/2f - 5, sr.getScaledWidth()/2f + 50, sr.getScaledHeight()/2f + 15) {
            @Override
            public void click() {
                mc.displayGuiScreen(new GuiMultiplayer(parent));
            }
        });

        buttons.add(new Button("Alt Manager", this, sr.getScaledWidth()/2f - 50, sr.getScaledHeight()/2f + 20, sr.getScaledWidth()/2f + 50, sr.getScaledHeight()/2f + 40) {
            @Override
            public void click() {
                mc.displayGuiScreen(new AltManager());
            }
        });

        buttons.add(new Button("Options", this, sr.getScaledWidth()/2f - 50, sr.getScaledHeight()/2f + 45, sr.getScaledWidth()/2f + 50, sr.getScaledHeight()/2f + 65) {
            @Override
            public void click() {
                mc.displayGuiScreen(new GuiOptions(parent, mc.gameSettings));
            }
        });

        buttons.add(new Button("Exit", this, sr.getScaledWidth()/2f - 50, sr.getScaledHeight()/2f + 70, sr.getScaledWidth()/2f + 50, sr.getScaledHeight()/2f + 90) {
            @Override
            public void click() {
                mc.displayGuiScreen(new ConfirmShutdown(parent));
            }
        });

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution sr = new ScaledResolution(mc);

        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
        GlStateManager.resetColor();
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColorAlt(3).getRGB());
        GlStateManager.resetColor();

        for(Button button : buttons) {
            button.tick(mouseX, mouseY);
        }

        for(Button button : buttons) {
            button.drawPre();
        }

        BlurUtil.blurAll(8);

        for(Button button : buttons) {
            button.drawPost();
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Button button : buttons) {
            button.click(mouseX, mouseY);
        }
    }

    Button relative(String title, double x, double y, double width, double height) {
        return new Button(title, this, x, y, x + width, y + height);
    }

    public class Button {

        public double x1, y1, x2, y2;
        public String title;

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
            if(contains(x, y, x1, y1, x2, y2)) {
                click();
            }
        }

        public void click() {

        }

        public void tick(int x, int y) {
            tick(contains(x, y, x1, y1, x2, y2));
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

    public static boolean contains(double x, double y, double minX, double minY, double maxX, double maxY) {
        return x > minX && x < maxX && y > minY && y < maxY;
    }

}

class BlurUtil {
    private static ShaderGroup blurShader;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static Framebuffer buffer;
    private static float lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static final ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");

    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            buffer.setFramebufferColor(1,1,1,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight) {
        blurShader.getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        blurShader.getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(intensity);

        blurShader.getShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        blurShader.getShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }

    public static void blurAll(float intensity) {
        ScaledResolution scale = new ScaledResolution(mc);
        float factor = scale.getScaleFactor();
        int factor2 = scale.getScaledWidth();
        int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        setShaderConfigs(intensity, 0, 1);
        buffer.bindFramebuffer(true);
        blurShader.loadShaderGroup(Minecraft.getMinecraft().timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);
    }

}


