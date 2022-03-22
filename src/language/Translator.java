package language;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;


public final class Translator {

    private Language language;

    public Translator(Language language) {
        this.language = language;
    }

    public void switchLanguage(Language language) {
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
        JSONObject head = this.language.languageDict();
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
            int placeholderCounter = 0;
            for (String car : toFormat.split("")) {
                if ("%".equals(car)) {
                    placeholderCounter++;
                }
            }
            if (params.length == placeholderCounter) {
                return formattedString;
            } else {
                return String.format("""
                                Too many parameters given to the ErrorMessage:
                                ErrorMessage: '%s'
                                Parameters: %s""",
                        toFormat, Arrays.toString(params));
            }
        } catch (MissingFormatArgumentException err) {
            return String.format("""
                            Missing information for the ErrorMessage:
                            ErrorMessage: '%s'
                            Parameters: %s""",
                    toFormat, Arrays.toString(params));
        } catch (IllegalFormatConversionException err) {
            return String.format("""
                            Wrong type in the ErrorMessage's parameters:
                            ErrorMessage: '%s'
                            Parameters: %s""",
                    toFormat, Arrays.toString(params));
        } catch (NullPointerException err) {
            return String.format("""
                            ErrorMessage received Null instead of parameters:
                            ErrorMessage: '%s'
                            Parameters: %s""",
                    toFormat, Arrays.toString(params));
        }
    }

    public static void main(String[] args) {

        var test = new Translator(Language.EN);

        System.out.println();
        System.out.println();
        System.err.println(test.translate("error.type.function.call.nb-parameter.too-small", "a"));
    }
}

