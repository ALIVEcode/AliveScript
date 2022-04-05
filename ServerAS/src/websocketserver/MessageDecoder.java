package websocketserver;

import org.json.JSONObject;
import websocketserver.model.Message;
import websocketserver.model.MessageTypes;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.Hashtable;
import java.util.Map;

import static websocketserver.ASWebSocketServer.logger;

public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public Message decode(String s) throws DecodeException {
        logger.info("Decoding message: " + s);
        JSONObject jsonObject = new JSONObject(s);
        String type = jsonObject.getString("type");
        MessageTypes messageType;
        try {
            messageType = MessageTypes.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new DecodeException(s, "Unknown message type: " + type);
        }
        Hashtable<String, Object> messageOptions = switch (messageType) {
            case COMPILE -> new Hashtable<>(Map.ofEntries(Map.entry("lines", jsonObject.getString("lines"))));
            case EXEC_FUNC -> new Hashtable<>(Map.ofEntries(
                    Map.entry("funcName", jsonObject.getString("funcName")),
                    Map.entry("args", jsonObject.getJSONArray("args"))
            ));
            default -> new Hashtable<>();
        };
        logger.info("Decoded options: " + messageOptions.toString().replace("\n", "\\n"));
        return new Message(messageType, messageOptions);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
