package websocketserver.model;


import java.util.Hashtable;

public record Message(MessageTypes type, Hashtable<String, Object> options) {

}
