package language;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.IllegalFormatConversionException;
import java.util.MissingFormatArgumentException;

public final class Translator {
    private static final String json = """
            {
                "error": {
                    "base-error": "Ceci est une erreur standard",
                    "type": {
                        "unknown-type": "Ce type est inconnu",
                        "int": {
                            "to-big": "L'entier est trop gros",
                            "to-small": "L'entier est trop petit"
                        },
                    },
                },
                "alivescript": "AliveScript",
                "function": {
                    "call": {
                         "nb-parameter": {
                             "to-small": "Le nombre de param\u00E8tres est trop petit. (Attendu: %d Re\u00E7u: %d)",
                             "to-big": "Le nombre de param\u00E8tres est trop grand. (Attendu: %d Re\u00E7u: %d)"
                         },
                         "call-type": "Un argument ne match pas le type du param\u00E8tre"
                    },
                    "creation": {}
                }
            }
            """;

    /**
     * Json dans cette variable
     */
    private final JSONObject jsonFile = new JSONObject(json);

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
        JSONObject head = jsonFile;
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
            return String.format(toFormat, params);
        } catch (MissingFormatArgumentException err) {
            return String.format("Missing information for the ErrorMessage:\nErrorMessage: '%s'\nParameters: %s",
                    toFormat, Arrays.toString(params));
        } catch (IllegalFormatConversionException err) {
            return String.format("Wrong type in the ErrorMessage's parameters:\nErrorMessage: '%s'\nParameters: %s",
                    toFormat, Arrays.toString(params));
        }
    }

    public static void main(String[] args) {
        var test = new Translator();
        System.out.println(test.translate(" function.call.nb-parameter.to-big "));
    }
}

