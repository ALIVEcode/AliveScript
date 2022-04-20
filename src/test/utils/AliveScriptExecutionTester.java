package test.utils;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.data_manager.Data;
import language.Translator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class AliveScriptExecutionTester {
    private final JSONArray executionResult;
    private final Translator translator;
    private int currentIdx = 0;

    public AliveScriptExecutionTester(JSONArray executionResult, Translator translator) {
        this.executionResult = executionResult;
        this.translator = translator;
    }

    //----------------- utils -----------------//

    private void assertHasNext() {
        assertTrue(
                "There are no more execution results to test",
                executionResult.length() > currentIdx
        );
    }

    private JSONObject getNextAction() {
        assertHasNext();
        return executionResult.getJSONObject(currentIdx);
    }

    //----------------- tests -----------------//

    public AliveScriptExecutionTester prints(Object message) {
        message = message.toString();
        var action = getNextAction();
        assertEquals(300, action.getInt("id"));
        assertEquals(message, action.getJSONArray("p").getString(0));
        currentIdx++;
        return this;
    }

    public AliveScriptExecutionTester throwsASErreur(Class<? extends ASErreur.ErreurAliveScript> erreurClass) {
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
        var nomErreur = translator.translate(erreur.getNomErreur());
        assertEquals(nomErreur, action.getJSONArray("p").getString(0));
        currentIdx++;
        return this;
    }

    public AliveScriptExecutionTester does(Data.Id id) {
        var action = getNextAction();
        assertEquals(id.getId(), action.getInt("id"));
        currentIdx++;
        return this;
    }

    public AliveScriptExecutionTester does(Data.Id id, Object... params) {
        var action = getNextAction();
        assertEquals(id.getId(), action.getInt("id"));
        var actualParams = action.getJSONArray("p");
        assertEquals(params.length, actualParams.length());
        for (int i = 0; i < params.length; i++) {
            assertEquals(params[i], actualParams.get(i));
        }
        currentIdx++;
        return this;
    }

    public AliveScriptExecutionTester ends() {
        var action = getNextAction();
        assertEquals(0, action.getInt("id"));
        currentIdx++;
        assertEquals(executionResult.length(), currentIdx);
        return this;
    }

    public AliveScriptExecutionTester asksForDataResponse() {
        var action = getNextAction();
        var id = action.getInt("id");
        var dataId = Data.Id.dataIdFromId(id);
        assertNotNull("There are no Data.Id defined with id '" + id + "'", dataId);
        assertTrue(
                "Data.Id that ask for data must have a name that starts with 'GET_' (Data.Id name was '" + dataId.name() + "')",
                dataId.name().startsWith("GET")
        );
        currentIdx++;
        assertEquals(executionResult.length(), currentIdx);
        return this;
    }

    public AliveScriptExecutionTester asksForDataResponse(Data.Id id) {
        this.does(id);
        assertEquals(executionResult.length(), currentIdx);
        return this;
    }

    public AliveScriptExecutionTester asksForDataResponse(Data.Id id, Object... params) {
        this.does(id, params);
        assertEquals(executionResult.length(), currentIdx);
        return this;
    }


}
