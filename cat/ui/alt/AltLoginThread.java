package cat.ui.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.net.Proxy;

public final class AltLoginThread extends Thread {
    private final String password;
    private final String username;
    private String status;
    private final Minecraft mc = Minecraft.getMinecraft();

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    public AltLoginThread(String username) {
        super("Alt Login Thread");
        this.username = username;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
        this.password = "NONE";
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);

        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //why do notifications look choppy in menus?
    public void run() {
        if (this.password.equalsIgnoreCase("none")) {
            this.mc.session = new Session(this.username, "", "", "mojang");
            //NotificationManager.publish("fps: " + Minecraft.getMinecraft().fpsCounter +"Logged in as " + this.username + "!", NotificationType.SUCCESS, 3000);
            this.status = EnumChatFormatting.GREEN + "Logged in as " + this.username + ".";
        } else {
            this.status = EnumChatFormatting.GREEN + "Logging in...";
            Session auth = this.createSession(this.username, this.password);
            if (auth == null) {
                this.status = EnumChatFormatting.RED + "Failed to login!";
                //NotificationManager.publish("Failed to login!", NotificationType.ERROR, 2000);
            } else {
                //NotificationManager.publish("fps: " + Minecraft.getMinecraft().fpsCounter +"Logged in as " + auth.getUsername() + "!", NotificationType.SUCCESS, 3000);
                this.status = EnumChatFormatting.GREEN + "Logged in as " + auth.getUsername() + ".";
                this.mc.session = auth;
            }

        }
    }

    public void loginWithCracked(String username) {
        this.mc.session = new Session(username, "", "", "mojang");
        this.status = EnumChatFormatting.GREEN + "Logged in as " + this.username + ".";
    }
}
