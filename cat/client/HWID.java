package cat.client;

import net.minecraft.client.Minecraft;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;

public class HWID {
    public static String hwid = "unknown";

    public static void getHWID() {
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
    }

    private static String getSerialNumber() {
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
