package cat.ui.alt;

import cat.BlueZenith;
import cat.util.ColorUtil;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public final class GuiAltLogin extends GuiScreen {
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField combined;

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    protected void actionPerformed(GuiButton button) {
        switch(button.id) {
            case 0:
                if (this.combined.getText().contains(":")) {
                    String[] split = this.combined.getText().split(":");
                    if(split.length == 2) {
                        String u = this.combined.getText().split(":")[0];
                        String p = this.combined.getText().split(":")[1];
                        this.thread = new AltLoginThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
                        this.thread.start();
                    }
                } else if(!isBlank(this.combined.getText())) {
                    String username = this.combined.getText();
                    if(username.length() > 16)
                        username = username.substring(0, 16);
                    this.thread = new AltLoginThread(username);
                    this.thread.start();
                }
                break;
            case 1:
                this.mc.displayGuiScreen(previousScreen);
                break;
            case 2:
                String[] split = BlueZenith.envAlt.split(":");
                if(split.length == 2) {
                    String u = split[0];
                    String p = split[1];
                    this.thread = new AltLoginThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
                    this.thread.start();
                }
                break;
        }

    }

    public void drawScreen(int x, int y, float z) {
        TFontRenderer font = FontUtil.fontSFLight42;
        String directLoginText = "Direct Login";
        String statusText = this.thread == null ? "Waiting..." : this.thread.getStatus();
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getMainColor().getRGB());
        drawGradientRect(0,0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        this.combined.drawTextBox();
        font.drawString(directLoginText, this.width / 2f - font.getStringWidth(directLoginText)/2f, 15, -1);
        font.drawString(statusText, this.width / 2f - font.getStringWidth(statusText) / 2f, 30, -1);
       // this.drawCenteredString(this.mc.fontRendererObj, "Direct login for now", this.width / 2, 20, -1);
        //this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? EnumChatFormatting.WHITE + "Idling" : this.thread.getStatus(), this.width / 2 - 5, 33, -1);
        if (this.combined.getText().isEmpty()) {
            font.drawString("Email:password / username", this.width / 2f - 96, 54.5f,-5592406);
            //this.drawString(this.mc.fontRendererObj, "Email : Password", this.width / 2 - 96, 56, -5592406);
        }
        font.drawString("Current name: " + mc.session.getUsername(), 10, 10, Color.GRAY.getRGB());
        //mc.fontRendererObj.drawString("Current Name: "+mc.session.getUsername(), 10, 10, Color.WHITE.getRGB());
        super.drawScreen(x, y, z);
    }

    public void initGui() {
        BlueZenith.updateRPC("Alt Manager", "");
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 20, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 20 + 24, "Back"));

        if(BlueZenith.envAlt != null) {
            this.buttonList.add(new GuiButton(2, this.width / 2 - 100, var3 + 20 + 48, "Env Login"));
        }

        this.combined = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 50, 200, 20);
        this.combined.setMaxStringLength(200);
        Keyboard.enableRepeatEvents(true);
    }

    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }

        this.combined.textboxKeyTyped(character, key);
    }

    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException var5) {
            var5.printStackTrace();
        }
        this.combined.mouseClicked(x, y, button);
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        this.combined.updateCursorCounter();
    }
}