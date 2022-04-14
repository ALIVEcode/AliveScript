package test.alivescript;

import language.Language;
import org.junit.jupiter.api.BeforeEach;

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

}
