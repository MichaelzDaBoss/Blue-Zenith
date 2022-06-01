package cat.ui.menu;

import cat.util.MathUtil;
import cat.util.MousePos;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class TextField extends GuiTextField {

    public TextField(int componentId, FontRenderer fontrendererObj, int x, int y, int x2, int y2) {
        super(componentId, fontrendererObj, x, y, x2 - x, y2 - y);
    }

    boolean pre;

    public void pre() {
        pre = true;
    }

    public void post() {
        pre = false;
    }

    @Override
    public void drawTextBox() {
        if (this.getVisible())
        {

            rect(xPosition, yPosition, width + xPosition, height + yPosition, new Color(0, 0, 0, pre? 40 : 60));

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (s.length() > 0)
            {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRendererInstance.drawStringWithShadow(s1, (float)l, (float)i1, i);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + this.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length())
            {
                this.fontRendererInstance.drawStringWithShadow(s.substring(j), (float) j1, (float) i1, i);
            }

            if (flag1)
            {
                if (flag2)
                {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                }
                else
                {
                    this.fontRendererInstance.drawStringWithShadow("_", (float)k1, (float)i1, i);
                }
            }

            if (k != j)
            {
                int l1 = l + this.fontRendererInstance.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
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

}
