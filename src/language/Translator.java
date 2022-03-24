package language;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;


public final class Translator {

    private Language language;

    public Translator(Language language) {
        this.language = language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }


    /**
     * Remplacer la section \u00e0 compl\u00E9ter de votre bord
     * <p>
     * En cas de la moindre erreur -> retourne le path et affiche l'erreur dans stderr<br>
     * {@code System.err.println(String);}
     *
     * @param path
     * @param params
     * @return
     */
    public String translate(String path, Object... params) {
        String[] tokens = path.trim().split("\\.");
        JSONObject head = this.language.getLanguageDict();
        try {
            for (int i = 0; i < tokens.length - 1; i++) {
                head = head.getJSONObject(tokens[i]);
            }
            return formatTranslated(head.getString(tokens[tokens.length - 1]), params);
        } catch (JSONException | NegativeArraySizeException err) {
            return path;
        }
    }

    private String formatTranslated(String toFormat, Object... params) {
        try {
            String formattedString = String.format(toFormat, params);
            //FIXME Cannot start or end with format
            int placeholderCounter = toFormat.split("(?!<%)%" +
                    "(?:(\\d+)\\$)?" +
                    "((, )|[-#+ 0,(]|<)?" +
                    "\\d*" +
                    "(?:\\.\\d+)?" +
                    "(?:[bBhHsScCdoxXeEfgGaAtT]|" +
                    "[tT][HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc])").length - 1;
            System.out.println(placeholderCounter);
            if (params.length == placeholderCounter) {
                return formattedString;
            } else {
                throw new TranslationFormatException(String.format("""
                                Too many parameters given to the translation:
                                Failed translation: "%s"
                                Parameters: %s""",
                        toFormat, Arrays.stream(params)
                                .map(p -> p instanceof String ? "\"" + p + "\"" : p.toString())
                                .toList()
                                .toString()));
            }
        } catch (MissingFormatArgumentException err) {
            throw new TranslationFormatException(String.format("""
                            Missing parameters for the translation:
                            Failed translation: "%s"
                            Parameters: %s""",
                    toFormat, Arrays.stream(params)
                            .map(p -> p instanceof String ? "\"" + p + "\"" : p.toString())
                            .toList()
                            .toString()));
        } catch (IllegalFormatConversionException err) {
            throw new TranslationFormatException(String.format("""
                            Wrong type in the parameters during translation:
                            Failed translation: "%s"
                            Parameters: %s""",
                    toFormat, Arrays.stream(params)
                            .map(p -> p instanceof String ? "\"" + p + "\"" : p.toString())
                            .toList()
                            .toString()));
        }
    }

    public static void main(String[] args) {

        var test = new Translator(Language.FR);
        System.out.println();
        System.err.println(test.translate("error.Index", 1000000));
    }

    public Language getLanguage() {
        return language;
    }
}

