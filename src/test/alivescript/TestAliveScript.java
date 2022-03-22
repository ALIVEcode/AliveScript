package test.alivescript;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.data_manager.Data;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static test.utils.AliveScriptTester.*;

public class TestAliveScript {
    private static final boolean DEBUG = true;

    @BeforeEach
    public void setup() {
        resetExecuteur(DEBUG);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"bonjour\" ~~ bonjour",
            "12 ~~ 12",
            "-333 ~~ -333",
            "-98.2 ~~ -98.2",
            "{1, 2, 3} ~~ [1, 2, 3]",
            "[4, 5, 6] ~~ [4, 5, 6]"
    })
    public void testAfficher(String toPrint) {
        String expected = toPrint.split("~~")[1].trim();
        toPrint = toPrint.split("~~")[0];
        assertCompiles("afficher(" + toPrint + ")");

        assertExecution()
                .prints(expected);
    }

    @Test
    public void testAfficherSansParentheses() {
        assertCompiles("""
                afficher [1, 2, 3]
                """);

        assertExecution()
                .throwsASErreur(ASErreur.ErreurType.class)
                .ends();
    }


    @Test
    public void testLire() {
        assertCompiles("""
                afficher 2 + 2
                var abc
                lire abc
                afficher abc
                """);

        assertExecution()
                .prints("4")
                .asksForDataResponse(Data.Id.GET, "read", "Entrez un input");

        assertExecution("bonjour")
                .prints("bonjour")
                .ends();
    }
}
