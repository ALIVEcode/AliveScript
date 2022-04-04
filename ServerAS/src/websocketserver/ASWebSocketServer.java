package websocketserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.glassfish.tyrus.server.Server;
import websocketserver.endpoints.AliveScriptExecutionEndpoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ASWebSocketServer {
    private final static Dotenv env = Dotenv.configure()
            .directory("./.env")
            .load();

    private static final int PORT = Integer.parseInt(env.get("WS_SERVER_PORT"));
    private static final String PATH = env.get("WS_BASE_PATH");
    private static final String HOST = env.get("WS_AS_HOST");

    public static void main(String[] args) {
        Server server = new Server(HOST, PORT, PATH, null, AliveScriptExecutionEndpoint.class);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}

