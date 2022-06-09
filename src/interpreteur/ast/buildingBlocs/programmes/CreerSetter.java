package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASFonction;
import interpreteur.as.lang.datatype.ASParametre;
import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreerSetter extends Programme {
    private final Var var;
    private final Var nomArg;
    private final ASTypeExpr type;
    private final ASScope scope;

    public CreerSetter(Var var, Var nomArg, ASTypeExpr type, Executeur executeurInstance) {
        super(executeurInstance);
        this.var = var;
        this.nomArg = nomArg;
        this.type = type;
        this.addSetter();
        this.scope = ASScope.makeNewCurrentScope();
    }

    public Var getVar() {
        return var;
    }

    public void addSetter() {
        ASVariable v = ASScope.getCurrentScope().getVariable(var.getNom());

        if (v == null) {
            Declarer.addWaitingSetter(this);
            return;
        }

        v.setSetter((valeur) -> {
            ASScope scope = new ASScope(this.scope);
            scope.setParent(ASScope.getCurrentScopeInstance());
            String scopeName = executeurInstance.obtenirCoordRunTime().getScope();
            String callingCoord = ASScopeManager.formatNewScope(ASScopeManager.ScopeKind.SETTER, scopeName, this.var.getNom());
            ASFonction set = new ASFonction(this.var.getNom(), callingCoord, new ASParametre[]{
                    new ASParametre(this.nomArg.getNom(), this.type, null)
            }, this.type, executeurInstance);

            scope.declarerVariable(new ASVariable(this.nomArg.getNom(), null, this.type));

            set.setScope(scope);

            return set.makeInstance().executer(new ArrayList<>(Collections.singletonList(valeur)));
        });
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        String newScope = ASScopeManager.formatNewScope(
                ASScopeManager.ScopeKind.SETTER,
                coord.getScope(),
                ASFonctionManager.ajouterDansNamespace(this.var.getNom())
        );
        return new Coordonnee(executeurInstance.nouveauScope(newScope));
    }
}
