package test.alivescript;

import language.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static test.utils.AliveScriptTester.*;

public class TestBuiltinsAS extends AbstractTestAliveScript {

    public TestBuiltinsAS() {
        super(true, Language.FR);
    }

    @BeforeEach
    public void setup() {
        resetExecuteur(DEBUG, Language.FR);
    }

    @Test
    public void testTypeDe() {
        assertCompiles("""
                                
                """);

        assertExecution()
                .prints("");
    }

}
