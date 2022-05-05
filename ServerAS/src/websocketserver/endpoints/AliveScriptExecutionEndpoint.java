package websocketserver.endpoints;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASListe;
import interpreteur.converter.ASObjetConverter;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import language.Language;
import org.json.JSONArray;
import websocketserver.MessageDecoder;
import websocketserver.MessageEncoder;
import websocketserver.model.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static websocketserver.ASWebSocketServer.logger;

@ServerEndpoint(
        value = "/execute/{tokenId}",
        decoders = MessageDecoder.class
)
public class AliveScriptExecutionEndpoint {
    private static final Set<AliveScriptExecutionEndpoint> ALIVE_SCRIPT_ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final HashMap<String, Session> TOKEN_MAP = new HashMap<>();
    private Session session;
    volatile private Executeur executeur;
    private String tokenId;

    @OnOpen
    public void onOpen(Session session, @PathParam("tokenId") String tokenId) {
        System.out.println("Open: session " + session.getId() + " with token: " + tokenId);
        this.session = session;
        this.tokenId = tokenId;
        this.executeur = new Executeur(Language.FR);
        this.executeur.debug = true;
        ALIVE_SCRIPT_ENDPOINTS.add(this);
        if (TOKEN_MAP.containsKey(tokenId)) {
            try {
                TOKEN_MAP.get(tokenId).close();
                TOKEN_MAP.remove(tokenId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TOKEN_MAP.put(tokenId, session);
    }

    @OnMessage
    public void onMessage(Session session, Message message) {
        int time = LocalDateTime.now().getSecond();
        if (!executorAvailable(session, time)) return;
        send(message.handle(executeur));
    }

    private boolean executorAvailable(Session session, int time) {
        while (executeur == null) {
            if (LocalDateTime.now().getSecond() - time > 5) {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void send(String text) {
        logger.info("Sending: " + text);
        session.getAsyncRemote().sendText(text);
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("Close: " + this.tokenId);
        ALIVE_SCRIPT_ENDPOINTS.remove(this);  // remove from the list
        TOKEN_MAP.remove(this.tokenId); // remove tokenId from map
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
        logger.severe("Error: " + t.getMessage());
        onClose(session);
    }
}
