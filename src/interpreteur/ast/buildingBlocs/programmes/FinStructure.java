package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.structure.ASPropriete;
import interpreteur.as.lang.datatype.structure.ASStructure;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.List;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class FinStructure extends Programme {
    private final ASScope scope;

    public FinStructure(Executeur executeurInstance) {
        super(executeurInstance);
        this.scope = ASScope.getCurrentScope();
        ASScope.popCurrentScope();
        // creerStructure();
        ASScope.getCurrentScope().getVariable(getNomStructure()).setValeur(new ASStructure(getNomStructure(), new ASPropriete[]{}));
    }


    private String getNomStructure() {
        return ASScopeManager.getScopeName(executeurInstance.obtenirCoordRunTime().getScope());
    }

    /**
     * Executed at compile time
     */
    private void creerStructure() {
        // Get les variables déclarées dans le scope de la structure
        var variables = scope.getVariablesDeclarees();
        // transformer les variables en ASPropriete
        var proprietes = variables.toArray(ASVariable[]::new);
        // Creer ASStructure
        var nomStructure = getNomStructure();
        var structure = new ASStructure(nomStructure, proprietes);
        // changer la valeur de la structure dans le scope instance
        ASScope.getCurrentScope().getVariable(nomStructure).setValeur(structure);
    }

    @Override
    public ASObjet<?> execute() {
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        if (ASScopeManager.getScopeKind(coord.getScope()) != ASScopeManager.ScopeKind.STRUCTURE)
            throw new ASErreur.ErreurFermeture(coord.getScope(), "fin structure");
        return new Coordonnee(executeurInstance.finScope());
    }
}
