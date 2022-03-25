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

    @Test
    public void testAfficher() {
        assertCompiles("""
                afficher "bonjour"
                afficher 12
                afficher (-333)
                afficher (-23.11)
                afficher {1, 2, 3, 4}
                afficher vrai
                afficher ([5, 6, 7])
                """);

        assertExecution()
                .prints("bonjour")
                .prints("12")
                .prints("-333")
                .prints("-23.11")
                .prints("[1, 2, 3, 4]")
                .prints("vrai")
                .prints("[5, 6, 7]");
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


    @Test
    public void testAddition() {
        assertCompiles("""
                afficher 3 + 12
                afficher 87 + -2
                afficher([1, 2, 3] + 2)
                afficher([1, [1, 2], vrai] + [1, 2, 3])
                """);

        assertExecution()
                .prints("15")
                .prints("85")
                .prints("[1, 2, 3, 2]")
                .prints("[1, [1, 2], vrai, [1, 2, 3]]")
                .ends();
    }
}




























