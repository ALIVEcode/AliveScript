package websocketserver.model;

import language.Language;
import org.json.JSONObject;

import java.util.function.Predicate;

public enum MessageTypes {
    /**
     * Format:
     * <pre>
     * {
     *     lines?: string,
     *     context?: object,
     *     responseData?: any[]
     * }
     * </pre>
     */
    COMPILE(format -> true),

    /**
     * responseData: any[]
     */
    RESUME(format -> true),

    /**
     * Utilisé pour dire à alivescript d'exécuter une fonction en particulier
     * Format:
     * <pre>
     * {
     *     functionName: string,
     *     args?: any[]
     * }
     * </pre>
     */
    EXEC_FUNC(format -> true),
    /**
     * Format:
     * <pre>
     * {
     *     "lang": "FR" | "EN" | "ES"
     * }
     * </pre>
     */
    LINT_INFO(format -> format.has("lang")
                        && format.get("lang") instanceof String s
                        && Language.isSupportedLanguage(s)
    ),
    /**
     * Coming soon...
     */
    ANALYSE(format -> true);

    private final Predicate<JSONObject> matchesFormat;

    MessageTypes(Predicate<JSONObject> matchesFormat) {
        this.matchesFormat = matchesFormat;
    }

    public boolean matchesFormat(JSONObject format) {
        return this.matchesFormat.test(format);
    }

}
