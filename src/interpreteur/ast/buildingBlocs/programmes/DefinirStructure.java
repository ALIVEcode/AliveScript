package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;
import interpreteur.as.lang.ASConstante;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.List;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class DefinirStructure extends Programme {
    private final Var varExpr;

    public DefinirStructure(Var varExpr, Executeur executeurInstance) {
        super(executeurInstance);
        this.varExpr = varExpr;
        // FIXME : bug potentiel avec les namespaces, car le nom du type sera seulement le nom de la structure (sans le namespace devant)
        // Je ne vais pas fix le bug, car le système de namespace et de type doit être changer anyway
        var varStructure = new ASConstante(varExpr.getNom(), null, new ASTypeExpr(varExpr.getNom()));
        this.varExpr.setNom(varStructure.getNom());
        ASScope.getCurrentScope().declarerVariable(varStructure);
        ASScope.makeNewCurrentScope();
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        String newScope = ASScopeManager.formatNewScope(
                ASScopeManager.ScopeKind.STRUCTURE,
                coord.getScope(),
                this.varExpr.getNom()
        );
        return new Coordonnee(executeurInstance.nouveauScope(newScope));
    }
}