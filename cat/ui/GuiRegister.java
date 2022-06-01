package cat.ui;

import cat.BlueZenith;
import cat.util.FileUtil;
import cat.util.RenderUtil;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;

public class GuiRegister extends GuiScreen {

    private String text2 = "Register? It's free.";
    private GuiButton btn;
    private GuiTextField usernameField;
    public void initGui() {
        int j = this.height / 3 + 48;
        this.buttonList.clear();
        btn = new GuiButton(4, this.width / 2 - 100, this.height / 3 + 48 + 96, "Main Menu");
        usernameField = new GuiTextField(2, mc.fontRendererObj, this.width / 2 - 100, j - 50, 200, 20);
        usernameField.setMaxStringLength(14);
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(0, new GuiButton(1, this.width / 2 - 100, j + 24, "Register"));
        this.buttonList.add(1, new GuiButton(2, this.width / 2 - 100, j + 48, "Remind me later"));
        this.buttonList.add(2, new GuiButton(3, this.width / 2 - 100, j + 72, "Don't ask again"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case 1:
                BlueZenith.executorService.submit(new registerTask(usernameField.getText()));
            break;

            case 2:
            case 4:
                mc.displayGuiScreen(BlueZenith.guiMain);
            break;

            case 3:
                BlueZenith.connection.username = "§fNot authorized";
                BlueZenith.connection.status = "§aUser";
                mc.displayGuiScreen(BlueZenith.guiMain);
                BufferedWriter w = FileUtil.getWriter(true, "option.data");
                w.write("no");
                w.close();
            break;

        }
    }

    private void addBtn() {
        if(text2.equals("§cFailed to register") || text2.equals("§aRegistered successfully!")) {
            this.buttonList.add(3, btn);
        } else {
            this.buttonList.remove(btn);
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(100, 100, 100));
        float width = sr.getScaledWidth()/2f;
        float height = sr.getScaledHeight()/7f;
        this.usernameField.drawTextBox();
        String text1 = "Looks like you're not authorized.";
        drawCenteredString(text1, width, sr.getScaledHeight()/7f, -1);
        drawCenteredString(text2, width, height + mc.fontRendererObj.FONT_HEIGHT*2f, -1);
        addBtn();
        if(usernameField.getText().isEmpty()) drawCenteredString("Username", width - 70, this.height / 3 + 4f, -5592406);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException ignored) {}
        usernameField.textboxKeyTyped(character, key);
    }

    protected void mouseClicked(int x, int y, int button) {
        try { super.mouseClicked(x, y, button); } catch (IOException ignored) {}
        usernameField.mouseClicked(x, y, button);
    }

    private void drawCenteredString(String text, float x, float y, int color) {
        mc.fontRendererObj.drawString(text, x - (mc.fontRendererObj.getStringWidth(text)/2f), y, color, true);
    }

    private final class registerTask implements Runnable {

        final String username;
        protected registerTask(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            text2 = "§eAttempting to register..." + username;
            if(username.isEmpty() || username.matches("[^a-zA-Z0-9]")) {
                text2 = "§cInvalid username provided.";
                return;
            }
            try {
                BlueZenith.connection.register(username);
                try {
                    BlueZenith.connection.runCheck();
                } catch(Exception ex) {

                }
                //BlueZenith.connection.username = username;
                text2 = "§aRegistered successfully!";
            } catch (Exception ex) {
                text2 =  "§cFailed to register";
            }
        }
    }
}
