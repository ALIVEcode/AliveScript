package websocketserver.model;

import interpreteur.executeur.Executeur;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import static java.util.Objects.requireNonNullElse;

public final class MessageResume extends Message {
    private final JSONArray responseData;

    public MessageResume(@Nullable JSONArray responseData) {
        super(MessageTypes.RESUME);
        this.responseData = requireNonNullElse(responseData, new JSONArray());
    }

    @Override
    public String handle(Executeur executeur) {
        for (int i = 0; i < responseData.length(); i++) {
            executeur.pushDataResponse(responseData.get(i));
        }
        var result = executeur.executerMain(true);
        return result.toString();
    }

    @Override
    public String toString() {
        return "MessageResume{" +
               "responseData=" + responseData +
               '}';
    }
}
