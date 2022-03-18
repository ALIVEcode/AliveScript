import language.Translator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDefiTranslation {

    @Test
    public void test() {
        //----------------- Tests -----------------//
        var defi = new Translator();
        assertEquals("", defi.t(""));
        assertEquals("error.type.int", defi.t("error.type.int"));

        assertEquals("L'entier est trop petit", defi.t("error.type.int.to-small"));

        assertEquals("AliveScript", defi.t("alivescript"));

        assertEquals("function.type", defi.t("function.type"));

        assertEquals("function.call.nb-parameter.to-bi", defi.t("function.call.nb-parameter.to-bi"));

        assertEquals("Le nombre de param\u00E8tres est trop grand", defi.t(" function.call.nb-parameter.to-big "));

        assertEquals("function.call.creation", defi.t("function.call.creation"));

        assertEquals("12345678", defi.t("12345678"));

        assertEquals(
                "Remplacer la section \\u00e0 compl\\u00E9ter de votre bord",
                defi.t("Remplacer la section \\u00e0 compl\\u00E9ter de votre bord")
        );
        assertEquals("null", defi.t("null"));

        assertEquals("...............", defi.t("..............."));
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
        assertEquals("", defi.t(""));
        assertEquals("error.type.int", defi.t("error.type.int"));

        assertEquals("L'entier est trop petit", defi.t("error.type.int.to-small"));

        assertEquals("AliveScript", defi.t("alivescript"));

        assertEquals("function.type", defi.t("function.type"));

        assertEquals("function.call.nb-parameter.to-bi", defi.t("function.call.nb-parameter.to-bi"));

        assertEquals("Le nombre de param\u00E8tres est trop grand", defi.t("function.call.nb-parameter.to-big"));

        assertEquals("function.call.creation", defi.t("function.call.creation"));

        assertEquals("12345678", defi.t("12345678"));

        assertEquals(
                "Remplacer la section \\u00e0 compl\\u00E9ter de votre bord",
                defi.t("Remplacer la section \\u00e0 compl\\u00E9ter de votre bord")
        );
        assertEquals("null", defi.t("null"));

        assertEquals("...............", defi.t("..............."));
    }
}


















