package websocketserver.model;

import language.Language;
import org.json.JSONObject;

import java.util.function.Predicate;

public enum MessageTypes {
    COMPILE("{ lines?: string, context?: object }",
            format -> true),

    RESUME("{ responseData: any[] }",
            format -> true),

    EXEC_FUNC("{ functionName: string, args?: any[] }",
            format -> true),

    LINT_INFO("{lang: \"FR\" | \"EN\" | \"ES\"}",
            format -> format.has("lang")
                      && format.get("lang") instanceof String s
                      && Language.isSupportedLanguage(s)
    ),

    ANALYSE("{}", format -> true);

    private final Predicate<JSONObject> matchesFormat;
    private final String formatToRespect;

    MessageTypes(String formatToRespect, Predicate<JSONObject> matchesFormat) {
        this.matchesFormat = matchesFormat;
        this.formatToRespect = formatToRespect;
    }

    public boolean matchesFormat(JSONObject format) {
        return this.matchesFormat.test(format);
    }

    public String getFormatToRespect() {
        return formatToRespect;
    }

}
