package language;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
     *
     * @param codeISO639_1 Code ISO 639-1 correspondant au langage désiré
     * @return Un JSON du langage
     */
    public JSONObject loadLanguage(String codeISO639_1)
    {
        // Endroit où se trouve le fichier JSON correspondant au langage
        String path = "language/languages/"+ codeISO639_1 +".json";
        return loadJSON(path);

    }

    /**
     *
     * @param path Le chemin du fichier à partir du dossier '{@code src}'
     * @return Un objet JSON
     */
    public JSONObject loadJSON(String path)
    {
        StringBuilder fileContent = new StringBuilder();
        try {

            var file = new File(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(path)).getFile());

            Scanner in = new Scanner(file);
            while(in.hasNextLine())
                fileContent.append(in.nextLine());

            in.close();

        } catch (FileNotFoundException e) {
            System.out.println("Le fichier n'a pas été trouvé !");

        }
        return (!fileContent.toString().equals("") ?
                // Si le fichier a bien été trouvé
                new JSONObject(fileContent.toString()):
                // Sinon
                null);
    }

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
        JSONObject head = loadLanguage("en");
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
        System.out.println(test.loadLanguage("en").toString(4));
        System.out.println();
        System.err.println(test.translate("error.type.function.call.nb-parameter.too-small", "test", 4, "q"));
        System.out.println(String.format("Hello %s", "yo", "blab"));

    }
}

