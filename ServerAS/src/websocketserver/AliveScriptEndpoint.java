package websocketserver;

import websocketserver.model.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(
        value = "/alivescript/{tokenId}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class
)
public class AliveScriptEndpoint {
    private static final Set<AliveScriptEndpoint> ALIVE_SCRIPT_ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> TOKEN_MAP = new HashMap<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("tokenId") String tokenId) {
        System.out.println("Open: " + tokenId + " from session " + session.getId());
        this.session = session;
        ALIVE_SCRIPT_ENDPOINTS.add(this);
        TOKEN_MAP.put(session.getId(), tokenId);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Message: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        String tokenId = TOKEN_MAP.get(session.getId());  // get tokenId from sessionId
        System.out.println("Close: " + tokenId);
        ALIVE_SCRIPT_ENDPOINTS.remove(this);  // remove from the list
        TOKEN_MAP.remove(session.getId()); // remove tokenId from map
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }
}
