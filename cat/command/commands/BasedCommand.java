package cat.command.commands;

import cat.command.Command;
import cat.util.ClientUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class BasedCommand extends Command {

    public BasedCommand() {
        super("Test", "Hello", "Hi");
    }

    @Override
    public void execute(String[] args) {
        try {
            URL url = new URL("http://localhost:8080/register");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("hwid", "sigma");
            connection.setDoOutput(true);
            connection.connect();
            ClientUtils.fancyMessage(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
            connection.disconnect();

            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    URLConnection c = url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    return reader.readLine();
                } catch (Exception e) { }
                return "errorxd";
            });
            chat(future.get());
        } catch(Exception ex) {
            chat("error ");
        }
    }
}
