import language.Translator;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDefiTranslation {


    /**
     * Json dans cette variable
     */
    //private final JSONObject jsonFile = new JSONObject(json);

    private static JSONObject loadedLanguage;

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


    @Test
    public void test() {
        //----------------- Tests -----------------//
        var defi = new Translator();
        assertEquals("", defi.translate(""));
        assertEquals("error.type.int", defi.translate("error.type.int"));

        assertEquals("L'entier est trop petit", defi.translate("error.type.int.to-small"));

        assertEquals("AliveScript", defi.translate("alivescript"));

        assertEquals("function.type", defi.translate("function.type"));

        assertEquals("function.call.nb-parameter.to-bi", defi.translate("function.call.nb-parameter.to-bi"));

        assertEquals("Le nombre de param\u00E8tres est trop grand", defi.translate(" function.call.nb-parameter.to-big "));

        assertEquals("function.call.creation", defi.translate("function.call.creation"));

        assertEquals("12345678", defi.translate("12345678"));

        assertEquals(
                "Remplacer la section \\u00e0 compl\\u00E9ter de votre bord",
                defi.translate("Remplacer la section \\u00e0 compl\\u00E9ter de votre bord")
        );
        assertEquals("null", defi.translate("null"));

        assertEquals("...............", defi.translate("..............."));
    }


    @Test
    public void testWithVariables() {
        //----------------- Tests -----------------//
        //----------------- formatting: https://docs.oracle.com/javase/tutorial/essential/io/formatting.html -----------------//
        /*
         * %[argument_index$][flags][width][.precision]conversion
         *
         */

        var defi = new Translator();
        assertEquals("", defi.translate(""));
        assertEquals("error.type.int", defi.translate("error.type.int"));

        assertEquals("L'entier est trop petit", defi.translate("error.type.int.to-small"));

        assertEquals("AliveScript", defi.translate("alivescript"));

        assertEquals("function.type", defi.translate("function.type"));

        assertEquals("function.call.nb-parameter.to-bi", defi.translate("function.call.nb-parameter.to-bi"));

        assertEquals("Le nombre de param\u00E8tres est trop grand", defi.translate("function.call.nb-parameter.to-big"));

        assertEquals("function.call.creation", defi.translate("function.call.creation"));

        assertEquals("12345678", defi.translate("12345678"));

        assertEquals(
                "Remplacer la section \\u00e0 compl\\u00E9ter de votre bord",
                defi.translate("Remplacer la section \\u00e0 compl\\u00E9ter de votre bord")
        );
        assertEquals("null", defi.translate("null"));

        assertEquals("...............", defi.translate("..............."));
    }
}


















