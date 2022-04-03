package websocketserver;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public record ASWebSocketConnection(Socket serverSocket) {
    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public void start() {
        new Thread(() -> {
            try {
                Optional<String> message;
                while ((message = decodeMessage()).isPresent()) {
                    handleMessage(message.get());
                }
                System.out.println("Connection closed by client");
            } catch (SocketException e) {
                System.err.println("Connection closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Optional<String> decodeMessage() throws IOException {
        byte[] buffer = new byte[3000];
        int read = serverSocket.getInputStream().read(buffer);
        if (read == -1) {
            return Optional.empty();
        }
        return Optional.of(new String(buffer, 0, read, StandardCharsets.UTF_8));
    }

    private void handleMessage(String message) throws NoSuchAlgorithmException {
        if (message.startsWith("GET")) {
            makeHandshake(message);
        } else {

        }
    }


    private void makeHandshake(String message) throws NoSuchAlgorithmException {
        if (!message.contains("Sec-WebSocket-Key")) {
            sendBadRequest();
            return;
        }
        String secWebSocketKey = message.split("Sec-WebSocket-Key: ")[1].split("\r\n")[0];

        var response = """
                HTTP/1.1 101 Switching Protocols\r
                Upgrade: websocket\r
                Connection: Upgrade\r
                Sec-WebSocket-Accept:\s""";
        response += Base64.getEncoder().encodeToString(
                MessageDigest
                        .getInstance("SHA-1")
                        .digest((secWebSocketKey + GUID).getBytes(StandardCharsets.UTF_8))
        ) + "\r\n\r\n";
        send(response);
    }

    private void sendBadRequest() {
        send("HTTP/1.1 400 Bad Request\r\n\r\n");
    }

    public void send(String message) {
        try {
            serverSocket.getOutputStream().write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
