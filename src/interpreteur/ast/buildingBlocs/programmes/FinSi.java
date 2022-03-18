package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import javax.lang.model.type.NullType;
import java.util.List;

public class FinSi extends Programme {
    public FinSi(Executeur executeurInstance) {
        super(executeurInstance);
    }

    @Override
    public NullType execute() {
        assert executeurInstance != null;
        executeurInstance.obtenirCoordRunTime().finBloc();
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        String blocActuel = coord.getBlocActuel().replaceAll("_", " ");
        if (!blocActuel.equals("si") && !blocActuel.equals("sinon") && !blocActuel.startsWith("sinon si")) {
            throw new ASErreur.ErreurFermeture(blocActuel,
                    "fin si", "fin " + blocActuel);
        }
        return coord.finBloc();
    }

    @Override
    public String toString() {
        return "FinSi";
    }
}
