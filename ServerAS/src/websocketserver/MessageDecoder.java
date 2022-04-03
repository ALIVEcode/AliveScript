package websocketserver;

import org.json.JSONObject;
import websocketserver.model.Message;
import websocketserver.model.MessageTypes;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public Message decode(String s) throws DecodeException {
        System.out.println("Decoding message: " + s);
        JSONObject jsonObject = new JSONObject(s);
        String type = jsonObject.getString("type");
        try {
            return new Message(MessageTypes.valueOf(type));
        } catch (IllegalArgumentException e) {
            throw new DecodeException(s, "Unknown message type: " + type);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
