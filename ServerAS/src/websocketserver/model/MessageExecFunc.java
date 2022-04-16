package websocketserver.model;

import interpreteur.executeur.Executeur;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import static java.util.Objects.requireNonNullElse;

public final class MessageExecFunc extends Message {
    private final String funcName;
    private final JSONArray funcArgs;

    public MessageExecFunc(@NotNull String funcName, @Nullable JSONArray funcArgs) {
        super(MessageTypes.EXEC_FUNC);
        this.funcName = funcName;
        this.funcArgs = requireNonNullElse(funcArgs, new JSONArray());
    }

    public JSONArray getFuncArgs() {
        return funcArgs;
    }

    public String getFuncName() {
        return funcName;
    }

    @Override
    public String handle(Executeur executeur) {
        return executeur.executerFonction(funcName, funcArgs);
    }

    @Override
    public String toString() {
        return "MessageExecFunc{" +
               "funcName='" + funcName + '\'' +
               ", funcArgs=" + funcArgs +
               '}';
    }
}
