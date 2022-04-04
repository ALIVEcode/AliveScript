package test.converters;


import interpreteur.as.lang.datatype.*;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import interpreteur.converter.ASObjetConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ASObjetConverterTest {

    //----------------- ASObjet to JSON -----------------//
    @Test
    public void testConvertSimpleASList() {

    }

    @Test
    public void testConvertNestedASList() {

    }


    @Test
    public void testConvertSimpleASDict() {

    }

    @Test
    public void testConvertNestedASDict() {

    }

    @Test
    public void testConvertASDictInsideASList() {

    }

    @Test
    public void testConvertASListInsideASDict() {

    }


    @Test
    public void testASObjetToJSONConversion() {
        var maListe = new ASListe();
        maListe.ajouterElement(new ASEntier(12));
        maListe.ajouterElement(new ASTexte("wow"));
        maListe.ajouterElement(new ASBooleen(false));
        maListe.ajouterElement(new ASListe(new ASEntier(12), new ASBooleen(false), new ASBooleen(true)));
        maListe.ajouterElement(new ASListe(new ASPaire(new ASTexte("haha"), new ASListe(new ASEntier(-23232)))));
        maListe.ajouterElement(new ASNul());

        var json = ASObjetConverter.toJSON(maListe);
        assertEquals(json.toString(), "[12,\"wow\",false,[12,false,true],{\"haha\":[-23232]},null]");
    }


    //----------------- JSON to ASObjet -----------------//
    @Test
    public void testConvertSimpleJSONArray() {
        JSONArray test = new JSONArray("""
                ["abc", 12, 9.8, true, true, false, null, "type", -1, -1.2]
                """);
        ASListe expected = new ASListe(
                new ASTexte("abc"),
                new ASEntier(12),
                new ASDecimal(9.8),
                new ASBooleen(true),
                new ASBooleen(true),
                new ASBooleen(false),
                new ASNul(),
                new ASTexte("type"),
                new ASEntier(-1),
                new ASDecimal(-1.2)
        );
        ASListe actual = ASObjetConverter.fromJSON(test);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertNestedJSONArray() {
        JSONArray test = new JSONArray("""
                ["abc", [12, 9.8], true, [true, [false, [null, "type"], -1]], -1.2]
                """);
        ASListe expected = new ASListe(
                new ASTexte("abc"),
                new ASListe(
                        new ASEntier(12),
                        new ASDecimal(9.8)
                ),
                new ASBooleen(true),
                new ASListe(
                        new ASBooleen(true),
                        new ASListe(
                                new ASBooleen(false),
                                new ASListe(
                                        new ASNul(),
                                        new ASTexte("type")
                                ),
                                new ASEntier(-1)
                        )
                ),
                new ASDecimal(-1.2)
        );
        ASListe actual = ASObjetConverter.fromJSON(test);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertSimpleJSONObject() {

    }

    @Test
    public void testConvertNestedJSONObject() {

    }
}
