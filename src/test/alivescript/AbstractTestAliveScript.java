package test.alivescript;

import language.Language;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import static test.utils.AliveScriptTester.isDone;
import static test.utils.AliveScriptTester.resetExecuteur;

public class AbstractTestAliveScript {
    protected final boolean DEBUG;
    protected final Language LANGUAGE;


    public AbstractTestAliveScript(boolean debug, Language language) {
        DEBUG = debug;
        LANGUAGE = language;
    }

    @BeforeEach
    public void setup() {
        resetExecuteur(DEBUG, Language.FR);
    }

    @AfterEach
    public void checkup() {
        assertTrue(isDone(), "The test fails to cover all the data returned by the Executeur");
    }

}
