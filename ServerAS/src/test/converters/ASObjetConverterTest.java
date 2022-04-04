package test.converters;


import interpreteur.as.lang.datatype.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import interpreteur.converter.ASObjetConverter;

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
        Assertions.assertEquals(json.toString(), "[12,\"wow\",false,[12,false,true],{\"haha\":[-23232]},null]");
    }


    //----------------- JSON to ASObjet -----------------//
    @Test
    public void convertSimpleJSONArray() {

    }

    @Test
    public void convertNestedJSONArray() {

    }

    @Test
    public void convertSimpleJSONObject() {

    }

    @Test
    public void convertNestedJSONObject() {

    }
}
