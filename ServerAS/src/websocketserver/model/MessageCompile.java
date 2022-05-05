package websocketserver.model;

import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

public final class MessageCompile extends Message {
    private final String lines;
    private JSONObject context;

    public MessageCompile(@NotNull String lines, @Nullable JSONObject context) {
        super(MessageTypes.COMPILE);
        this.lines = lines;
        this.context = context;
    }

    public Optional<JSONObject> getContext() {
        return Optional.ofNullable(context);
    }

    public void setContext(JSONObject context) {
        this.context = context;
    }

    public String[] getLines() {
        return lines.split("\n");
    }

    @Override
    public String handle(Executeur executeur) {
        JSONArray result = executeur.compiler(getLines(), true);
        if (!result.toString().equals("[]")) {
            return result.toString();
        }
        JSONArray resultExec = executeur.executerMain(false);
        // FIXME maybe remove in the future? This is mark as FIXME to not forget about it
        // Basically, it prevents the `lire` command to execute
        if (resultExec.getJSONObject(resultExec.length() - 1).getInt("id") == Data.Id.GET.getId()) {
            return new Data(Data.Id.ERREUR).addParam("ErreurIO").addParam("Les commandes d'IO sont interdites dans ce contexte").toString();
        }
        return resultExec.toString();
    }

    @Override
    public String toString() {
        return "MessageCompile{" +
               "lines='" + lines.replaceAll("\n", "\\\\n") + '\'' +
               ", context=" + context +
               '}';
    }
}
