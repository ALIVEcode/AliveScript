package language;

import org.json.JSONException;
import org.json.JSONObject;

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
                             "to-small": "Le nombre de param\u00E8tres est trop petit",
                             "to-big": "Le nombre de param\u00E8tres est trop grand"
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
     * @return
     */
    public String t(String path) {
        String[] tokens = path.trim().split("\\.");
        JSONObject head = jsonFile;
        try {
            for (int i = 0; i < tokens.length - 1; i++) {
                head = head.getJSONObject(tokens[i]);
            }
            return head.getString(tokens[tokens.length - 1]);
        } catch (JSONException | NegativeArraySizeException err) {
            return path;
        }
    }
}
