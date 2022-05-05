package websocketserver.model;


import interpreteur.executeur.Executeur;

import java.util.Hashtable;

public abstract sealed class Message permits MessageCompile, MessageExecFunc, MessageResume {
    private final MessageTypes type;

    public Message(MessageTypes type) {
        this.type = type;
    }

    public MessageTypes type() {
        return type;
    }

    public abstract String handle(Executeur executeur);
}
