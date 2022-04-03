package websocketserver;

import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ASWebSocketServer {
    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    private static final String PATH = "/ws";

    public static void main(String[] args) {
        Server server = new Server(HOST, PORT, PATH, null, AliveScriptEndpoint.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}

