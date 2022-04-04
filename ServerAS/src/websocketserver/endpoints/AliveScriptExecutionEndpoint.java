package websocketserver.endpoints;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.ASTexte;
import interpreteur.ast.buildingBlocs.expressions.AppelFonc;
import interpreteur.ast.buildingBlocs.expressions.CreerListe;
import interpreteur.ast.buildingBlocs.expressions.ValeurConstante;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import interpreteur.generateurs.ast.AstGenerator;
import language.Language;
import org.json.JSONArray;
import websocketserver.MessageDecoder;
import websocketserver.MessageEncoder;
import websocketserver.model.Message;
import websocketserver.model.MessageTypes;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(
        value = "/execute/{tokenId}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class
)
public class AliveScriptExecutionEndpoint {
    private static final Set<AliveScriptExecutionEndpoint> ALIVE_SCRIPT_ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final HashMap<String, Session> TOKEN_MAP = new HashMap<>();
    private Session session;
    private Executeur executeur;
    private String tokenId;

    @OnOpen
    public void onOpen(Session session, @PathParam("tokenId") String tokenId) {
        System.out.println("Open: session " + session.getId() + " with token: " + tokenId);
        this.session = session;
        this.tokenId = tokenId;
        this.executeur = new Executeur(Language.FR);
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
        switch (message.type()) {
            case COMPILE -> {
                JSONArray result = executeur.compiler(((String) message.options().get("lines")).split("\n"), true);
                if (!result.toString().equals("[]")) {
                    session.getAsyncRemote().sendText(result.toString());
                    return;
                }
                JSONArray resultExec = executeur.executerMain(false);
                if (resultExec.getJSONObject(resultExec.length() - 1).getInt("id") != 0) {
                    session.getAsyncRemote().sendText(new Data(Data.Id.ERREUR).addParam("ErreurIO").addParam("Les commandes d'IO sont interdites dans ce contexte").toString());
                    return;
                }
                session.getAsyncRemote().sendText(resultExec.toString());
            }
            case EXEC_FUNC -> {
                if (executeur.obtenirCoordCompileDict().isEmpty()) {
                    // NON, PAS BIEN DU TOUT GRR GRR
                    session.getAsyncRemote().sendText(
                            new ASErreur.ErreurAppelFonction("ErreurCompilation", "Le code n'est pas compil\u00E9")
                                    .getAsData(executeur).toString()
                    );
                    return;
                }
                var funcName = (String) message.options().get("funcName");
                var args = (JSONArray) message.options().get("args");
                var argsArrayList = new ArrayList<ASObjet<?>>();
                // TODO conversion entre JSONArray -> ArrayList<ASObjet<?>>
                executeur.executerFonction(funcName, new ArrayList<>(List.of(new ASTexte(args.getString(0)))));
                session.getAsyncRemote().sendText(executeur.consumeData().toString());
            }
            default -> {
            }
        }
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
