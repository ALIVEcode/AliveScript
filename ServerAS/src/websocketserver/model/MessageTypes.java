package websocketserver.model;

import language.Language;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Predicate;

public enum MessageTypes {
    COMPILE("{ lines: string, context?: object }",
            format -> {
                format.getString("lines");
                format.optJSONObject("context");
                return true;
            }),

    RESUME("{ responseData?: any[] }",
            format -> {
                format.optJSONArray("responseData");
                return true;
            }),

    EXEC_FUNC("{ funcName: string, args?: any[] }",
            format -> {
                format.getString("funcName");
                format.optJSONArray("args");
                return true;
            }),

    LINT_INFO("{ lang: \"FR\" | \"EN\" | \"ES\" }",
            format -> {
                var lang = format.getString("lang");
                return Language.isSupportedLanguage(lang);
            }
    ),

    ANALYSE("{}", format -> true);

    private final Predicate<JSONObject> matchesFormat;
    private final String formatToRespect;

    MessageTypes(String formatToRespect, Predicate<JSONObject> matchesFormat) {
        this.matchesFormat = matchesFormat;
        this.formatToRespect = formatToRespect;
    }

    public boolean matchesFormat(JSONObject format) {
        try {
            return this.matchesFormat.test(format);
        } catch (JSONException err) {
            return false;
        }
    }

    public String getFormatToRespect() {
        return formatToRespect;
    }

}
