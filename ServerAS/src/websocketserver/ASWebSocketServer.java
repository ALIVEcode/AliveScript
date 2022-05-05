package websocketserver;

import io.github.cdimascio.dotenv.Dotenv;
import org.glassfish.tyrus.server.Server;
import server.executionApi.AliveScriptApi;
import server.lintingApi.ASLinterApi;
import websocketserver.endpoints.AliveScriptExecutionEndpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ASWebSocketServer {
    public static final Logger logger;
    private final static Dotenv env = Dotenv.configure()
            .directory("./.env")
            .load();
    private static final int PORT = Integer.parseInt(env.get("WS_SERVER_PORT"));
    private static final String PATH = env.get("WS_BASE_PATH");
    private static final String HOST = env.get("WS_AS_HOST");

    static {
        logger = setupLogger("WSLogger.log");
    }

    public static void main(String[] args) {
        Server server = new Server(HOST, PORT, PATH, null, AliveScriptExecutionEndpoint.class);

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

    public static Logger setupLogger(String fileName) {
        var logger = Logger.getLogger(ASWebSocketServer.class.getName());
        try {
            if (Files.notExists(Path.of("./log/"))) {
                Files.createDirectory(Path.of("./log/"));
            }
            FileHandler fileHandler = new FileHandler("./log/" + fileName, true);
            logger.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }
}

