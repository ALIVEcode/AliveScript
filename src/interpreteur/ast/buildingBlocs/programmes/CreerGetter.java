package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASFonction;
import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class CreerGetter extends Programme {
    private final Var var;
    private final ASTypeExpr type;
    private final ASScope scope;

    public CreerGetter(Var var, ASTypeExpr type, Executeur executeurInstance) {
        super(executeurInstance);
        this.var = var;
        this.type = type;
        this.addGetter();
        this.scope = ASScope.makeNewCurrentScope();
    }

    public Var getVar() {
        return var;
    }

    public void addGetter() {
        ASVariable v = ASScope.getCurrentScope().getVariable(var.getNom());

        if (v == null) {
            Declarer.addWaitingGetter(this);
            return;
        }

        v.setGetter(() -> {
            ASScope scope = new ASScope(this.scope);
            scope.setParent(ASScope.getCurrentScopeInstance());
            String scopeName = executeurInstance.obtenirCoordRunTime().getScope();
            String callingCoord = ASScopeManager.formatNewScope(ASScopeManager.ScopeKind.GETTER, scopeName, this.var.getNom());
            ASFonction get = new ASFonction(this.var.getNom(), callingCoord, this.type, executeurInstance);
            get.setScope(scope);
            return get.makeInstance().executer(new ArrayList<>());
        });
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        String newScope = ASScopeManager.formatNewScope(
                ASScopeManager.ScopeKind.GETTER,
                coord.getScope(),
                ASFonctionManager.ajouterDansNamespace(this.var.getNom())
        );
        return new Coordonnee(executeurInstance.nouveauScope(newScope));
    }

    @Override
    public String toString() {
        return "CreerGetter{" +
                "var=" + var +
                "type?=" + type +
                '}';
    }
}
