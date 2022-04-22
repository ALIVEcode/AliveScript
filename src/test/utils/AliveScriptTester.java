package test.utils;


import interpreteur.executeur.Executeur;
import language.Language;
import org.json.JSONArray;

import static org.junit.Assert.*;

public class AliveScriptTester {
    private static Executeur executeur = null;

    public static void resetExecuteur(boolean debug, Language language) {
        executeur = new Executeur(language);
        executeur.debug = debug;
    }

    public static AliveScriptExecutionTester assertCompilesAndExecutes(String code) {
        assertCompiles(code);
        return assertExecution();
    }

    public static void assertCompiles(String code) {
        final var lines = code.split("\n");
        var result = executeur.compiler(lines, true);
        assertEquals("[]", result.toString());
    }

    public static AliveScriptCompilationTester assertCompilation(String code) {
        final var lines = code.split("\n");
        var result = executeur.compiler(lines, true);
        if (executeur.debug) System.out.println(result);
        return new AliveScriptCompilationTester(result);
    }

    public static AliveScriptExecutionTester assertExecution() {
        return new AliveScriptExecutionTester(execute(null), executeur.getTranslator());
    }

    public static AliveScriptExecutionTester assertExecution(Object responseData) {
        return new AliveScriptExecutionTester(execute(new Object[]{responseData}), executeur.getTranslator());
    }

    public static AliveScriptExecutionTester assertExecution(Object[] responseData) {
        return new AliveScriptExecutionTester(execute(responseData), executeur.getTranslator());
    }

    private static JSONArray execute(Object[] responseData) {
        if (executeur.getLignes() == null) {
            throw new IllegalArgumentException("You must compile the lines before executing the code.");
        }
        if (responseData != null) {
            for (var data : responseData)
                executeur.pushDataResponse(data);
        }
        var result = executeur.executerMain(responseData != null);
        if (executeur.debug) System.out.println(result);
        return result;
    }

}































