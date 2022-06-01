package cat.client;

import cat.ui.GuiRegister;
import cat.util.FileUtil;
import cat.util.MinecraftInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

public final class Connection extends MinecraftInstance {

    private final Gson gson = new GsonBuilder().create();
    public Connection() {
        getHWID();
        authenticate();
    }

    public HttpClient client;
    public String serverURL = "http://localhost:8080";
    public String hwid;
    public String username = "§cNot Authorized";
    public String status = "§dUnknown";

    //this code SUCKS
    public void authenticate() {
        new Thread(() -> {
            try {
                String status = getStatus();
                switch (status) {
                    case "69: Pending Registration":
                    case "404: User Not Found":
                        if(FileUtil.exists(true, "option.data", "option.data")) { username = "Not authorized"; status = "§aUser"; return; }
                        mc.displayGuiScreen(new GuiRegister());
                        break;
                    case "69: Found":
                        runCheck();
                    break;
                }
            } catch(Exception ex){}
        }, "Authentication").start();
    }

    public void runCheck() throws Exception {
        String info = getInfo();
        JsonObject response = new JsonParser().parse(info).getAsJsonObject();
        response.entrySet().forEach(a -> {
            switch (a.getKey()) {
                case "username":
                    username = a.getValue().getAsString();
                    break;

                case "status":
                    switch (a.getValue().getAsString().toLowerCase()) {
                        case "dev":
                            this.status = "§cDeveloper";
                            break;
                        case "staff":
                            this.status = "§yStaff";
                            break;
                        case "user":
                            this.status = "§aUser";
                            break;
                    }
            }
        });
    }
    public String register(String username) throws Exception {
        return makeRequest(serverURL + "/authorize/registration?hwid=" + hwid +"&username=" + username);
    }

    private String getInfo() throws Exception {
       return makeRequest(serverURL + "/users/retrieve?hwid=" + hwid);
    }
    private String getStatus() throws Exception {
        return makeRequest(serverURL + "/authorize?key=" + hwid);
    }

    private String makeRequest(String link) throws Exception {
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        reader.close();
        return line;
    }

    private void getHWID() {
        String disk = getSerialNumber();
        String proc = System.getenv("NUMBER_OF_PROCESSORS");
        MessageDigest digest = DigestUtils.getSha256Digest();
        digest.update((disk + proc).getBytes());
        byte[] result = digest.digest();
        StringBuilder buffer = new StringBuilder();
        for (byte b : result) {
            buffer.append(Integer.toHexString(0xFF & b));
        }
        hwid = buffer.toString();
        //System.out.println(hwid);
    }

    private String getSerialNumber() {
        String SN = "";
        try {
            String sc = "cmd /c" + "wmic diskdrive get serialnumber";

            Process p = Runtime.getRuntime().exec(sc);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                SN = (sb.substring(sb.toString().lastIndexOf("r") + 1).trim());
            }

        }
        catch(Exception ex) {
            Minecraft.getMinecraft().shutdown();
        }
        return SN;
    }
}
