package test.alivescript;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.data_manager.Data;
import language.Language;
import org.junit.jupiter.api.*;

import static test.utils.AliveScriptTester.*;

public class TestAliveScript extends AbstractTestAliveScript {
    public TestAliveScript() {
        super(true, Language.FR);
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
    public void testQuitter() {
        assertCompiles("""
                afficher "bonjour"
                afficher 12
                afficher (-333)
                afficher (-23.11)
                quitter
                afficher {1, 2, 3, 4}
                afficher vrai
                afficher ([5, 6, 7])
                """);

        assertExecution()
                .prints("bonjour")
                .prints("12")
                .prints("-333")
                .prints("-23.11")
                .ends();
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
                var num: entier
                lire entier dans num
                afficher num
                afficher typeDe(num)
                """);

        assertExecution()
                .prints("4")
                .asksForDataResponse(Data.Id.GET, "read", "Entrez un input");

        assertExecution("bonjour")
                .prints("bonjour")
                .asksForDataResponse(Data.Id.GET, "read", "Entrez un input");

        assertExecution("288")
                .prints("288")
                .prints("entier")
                .ends();
    }

    @Test
    public void variablesNonDeclarer1() {
        assertCompiles("""
                var num: entier
                afficher num
                """);

        assertExecution()
                .throwsASErreur(ASErreur.ErreurAssignement.class)
                .ends();
    }

    @Test
    public void variablesNonDeclarer2() {
        assertCompiles("""
                var abc: liste
                abc += 2
                """);

        assertExecution()
                .throwsASErreur(ASErreur.ErreurAssignement.class)
                .ends();
    }

    @Test
    public void variablesNonDeclarer3() {
        assertCompiles("""
                var abc: liste
                abc[2:1] = [2]
                """);

        assertExecution()
                .throwsASErreur(ASErreur.ErreurAssignement.class)
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

    @Test
    public void testFoncGetSet() {
        assertCompiles("""
                fonction o()
                    afficher "o"
                fin fonction
                            
                fonction abc()
                    fonction o()
                        afficher "b"
                    fin fonction
                    o()
                fin fonction
                            
                var _ao = 0
                var ao
                get ao
                    retourner _ao
                fin get
                            
                            
                set ao(a)
                    afficher a
                    _ao = a
                    retourner _ao
                fin set
                            
                afficher ao
                ao += 1
                afficher ao
                abc()
                o()
                """);
        assertExecution()
                .prints(0)
                .prints(1)
                .prints(1)
                .prints("b")
                .prints("o")
                .ends();

    }

    @Test
    public void lire() {
        assertCompiles("""
                var temps
                var direction
                                
                tant que vrai
                    lire nombre dans temps, "Entrez le temps"
                    avancer temps
                    lire direction, "Entrez une direction"
                    si direction == "g" alors
                        gauche
                    sinon si direction == "d" alors
                        droite
                    sinon si direction == "q" alors
                        sortir
                    fin si
                    
                fin tant que
                """);

        assertExecution()
                .asksForDataResponse(Data.Id.GET, "read", "Entrez le temps");
        assertExecution("1")
                .does(Data.Id.AVANCER, 1)
                .asksForDataResponse(Data.Id.GET, "read", "Entrez une direction");
        assertExecution("g")
                .does(Data.Id.TOURNER, 90)
                .asksForDataResponse(Data.Id.GET, "read", "Entrez le temps");

        assertExecution("3.2")
                .does(Data.Id.AVANCER, 3.2)
                .asksForDataResponse(Data.Id.GET, "read", "Entrez une direction");
        assertExecution("d")
                .does(Data.Id.TOURNER, -90)
                .asksForDataResponse(Data.Id.GET, "read", "Entrez le temps");

        assertExecution("0")
                .does(Data.Id.AVANCER, 0)
                .asksForDataResponse(Data.Id.GET, "read", "Entrez une direction");
        assertExecution("q").ends();
    }
}




























