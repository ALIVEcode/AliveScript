package websocketserver;

import org.json.JSONException;
import org.json.JSONObject;
import websocketserver.model.*;

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
        if (!messageType.matchesFormat(jsonObject)) {
            throw new DecodeException(s, "The object " + jsonObject
                                         + " doesn't match the format for the message type: "
                                         + messageType + ". "
                                         + "The valid format is " + messageType.getFormatToRespect());
        }
        Message message = switch (messageType) {
            case COMPILE -> new MessageCompile(jsonObject.getString("lines"), jsonObject.optJSONObject("context"));
            case RESUME -> new MessageResume(jsonObject.optJSONArray("responseData"));
            case EXEC_FUNC -> new MessageExecFunc(jsonObject.getString("funcName"), jsonObject.optJSONArray("args"));
            default -> throw new DecodeException(s, "The type: " + messageType + " is not supported yet.");
        };
        logger.info("Decoded options: " + message);
        return message;
    }

    @Override
    public boolean willDecode(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            return jsonObject.get("type") instanceof String;
        } catch (JSONException err) {
            return false;
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
