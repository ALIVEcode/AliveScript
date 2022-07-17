package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.ASScope;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASNul;
import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.List;

public class FinGet extends Programme {

    public FinGet(Executeur executeurInstance) {
        super(executeurInstance);
        ASScope.popCurrentScope();
    }

    @Override
    public ASNul execute() {
        return new ASNul();
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        if (ASScopeManager.getScopeKind(coord.getScope()) != ASScopeManager.ScopeKind.GETTER)
            throw new ASErreur.ErreurFermeture(coord.getScope(), "fin get");
        return new Coordonnee(executeurInstance.finScope());
    }

    @Override
    public String toString() {
        return "FinFonction";
    }
}
