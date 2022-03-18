package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import javax.lang.model.type.NullType;
import java.util.List;

public class Si extends Programme {
    private final Expression<?> test;

    public Si(Expression<?> test, Executeur executeurInstance) {
        super(executeurInstance);
        this.test = test;
    }

    @Override
    public NullType execute() {
        assert executeurInstance != null;
        if (test.eval().boolValue()) {
            executeurInstance.obtenirCoordRunTime().nouveauBloc("si");
        } else if (executeurInstance.leBlocExiste("sinon_si_test")) {
            executeurInstance.obtenirCoordRunTime().nouveauBloc("sinon_si_test");
        } else if (executeurInstance.leBlocExiste("sinon")) {
            executeurInstance.obtenirCoordRunTime().nouveauBloc("sinon");
        }
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        return coord.nouveauBloc("si");
    }

    @Override
    public String transpile(String language) {
        return switch (language) {
            case "java" -> {
               yield "";
            }
            case "python" -> {
                yield "a";
            }
            case "c++", "cpp" -> {
                yield "b";
            }
            default -> throw new IllegalStateException("Unexpected value: " + language);
        };
    }

    @Override
    public String toString() {
        return "Si{" +
                "test=" + test +
                '}';
    }
}
