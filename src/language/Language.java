package language;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public enum Language {
    EN ("en"),
    FR ("fr"),
    ES ("es");




    private final JSONObject languageDict;

    Language(String codeISO639_1) {
        languageDict = loadLanguage(codeISO639_1);
    }

    /**
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
     * @param path Le chemin du fichier à partir du dossier '{@code src}'
     * @return Un objet JSON
     */
    public JSONObject loadJSON(String path) {
        StringBuilder fileContent = new StringBuilder();
        try {

            var file = new File(Objects.requireNonNull(getClass().getClassLoader()
                    .getResource(path)).getFile());

            Scanner in = new Scanner(file);
            while (in.hasNextLine())
                fileContent.append(in.nextLine());

            in.close();

        } catch (FileNotFoundException e) {
            System.err.println("Le fichier n'a pas été trouvé !");

        }
        return (!fileContent.toString().equals("") ?
                // Si le fichier a bien été trouvé
                new JSONObject(fileContent.toString()) :
                // Sinon
                null);
    }

    public JSONObject languageDict() {
        return languageDict;
    }
}










