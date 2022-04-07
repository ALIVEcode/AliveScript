package test.alivescript;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static test.utils.AliveScriptTester.*;

public class TestBuiltinsAS {
    private static final boolean DEBUG = true;

    @BeforeEach
    public void setup() {
        resetExecuteur(DEBUG);
    }

    @Test
    public void testTypeDe() {
        assertCompiles("""
                                
                """);

        assertExecution()
                .prints("");
    }

}
