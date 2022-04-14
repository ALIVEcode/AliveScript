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
        while (executeur == null) {
            if (LocalDateTime.now().getSecond() - time > 5) {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        switch (message.type()) {
            case COMPILE -> {
                JSONArray result = executeur.compiler(((String) message.options().get("lines")).split("\n"), true);
                if (!result.toString().equals("[]")) {
                    send(result.toString());
                    return;
                }
                JSONArray resultExec = executeur.executerMain(false);
                if (resultExec.getJSONObject(resultExec.length() - 1).getInt("id") == Data.Id.GET.getId()) {
                    send(new Data(Data.Id.ERREUR).addParam("ErreurIO").addParam("Les commandes d'IO sont interdites dans ce contexte").toString());
                    return;
                }

                send(resultExec.toString());
            }
            case RESUME -> {
                if (executeur.obtenirCoordCompileDict().isEmpty()) {
                    // NON, PAS BIEN DU TOUT GRR GRR
                    send(new ASErreur.ErreurAppelFonction("ErreurCompilation", "Le code n'est pas compil\u00E9")
                            .getAsData(executeur).toString());
                    return;
                }
                JSONArray responseData = (JSONArray) message.options().get("responseData");
                for (int i = 0; i < responseData.length(); i++) {
                    executeur.pushDataResponse(responseData.get(i));
                }
                var result = executeur.executerMain(true);
                System.out.println("RESULT: " + result);
                send(result.toString());
            }
            case EXEC_FUNC -> {
                if (executeur.obtenirCoordCompileDict().isEmpty()) {
                    // NON, PAS BIEN DU TOUT GRR GRR
                    send(new ASErreur.ErreurAppelFonction("ErreurCompilation", "Le code n'est pas compil\u00E9")
                            .getAsData(executeur).toString());
                    return;
                }
                var funcName = (String) message.options().get("funcName");
                ASListe args;
                if (message.options().containsKey("args"))
                    args = ASObjetConverter.fromJSON((JSONArray) message.options().get("args"));
                else
                    args = new ASListe();

                var datas = executeur.executerFonction(funcName, args.getValue());
                send(datas);
            }
            default -> {
            }
        }
    }

    private void send(String text) {
        System.out.println("Sending: " + text);
        session.getAsyncRemote().sendText(text);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Close: " + this.tokenId);
        ALIVE_SCRIPT_ENDPOINTS.remove(this);  // remove from the list
        TOKEN_MAP.remove(this.tokenId); // remove tokenId from map
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
        System.out.println("Error: " + t.getMessage());
    }
}
