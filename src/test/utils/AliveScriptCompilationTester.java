package test.utils;

import interpreteur.as.erreurs.ASErreur;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AliveScriptCompilationTester {
    private final JSONArray compilationResult;
    private int currentIdx = 0;

    public AliveScriptCompilationTester(JSONArray compilationResult) {
        this.compilationResult = compilationResult;
    }

    //----------------- utils -----------------//

    private void assertHasNext() {
        assertTrue(
                "There are no more execution results to test",
                compilationResult.length() > currentIdx
        );
    }

    private JSONObject getNextAction() {
        assertHasNext();
        return compilationResult.getJSONObject(currentIdx);
    }

    public AliveScriptCompilationTester throwsASErreur(Class<? extends ASErreur.ErreurAliveScript> erreurClass) {
        ASErreur.ErreurAliveScript erreur = null;
        try {
            erreur = erreurClass.getConstructor(String.class).newInstance("");
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
        }
        assertNotNull(
                "There are not constructor for the error class '" + erreurClass.getSimpleName() + "'",
                erreur
        );
        var action = getNextAction();
        assertEquals(400, action.getInt("id"));
        assertEquals(erreur.getNomErreur(), action.getJSONArray("p").getString(0));
        currentIdx++;
        return this;
    }
}
