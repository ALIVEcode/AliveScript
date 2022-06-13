package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.fonction.ASFonction;
import interpreteur.as.lang.datatype.fonction.ASParametre;
import interpreteur.as.lang.managers.ASScopeManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.Argument;
import interpreteur.ast.buildingBlocs.expressions.Var;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import javax.lang.model.type.NullType;
import java.util.Arrays;
import java.util.List;


public class CreerFonction extends Programme {
    private final ASScope scope;
    private final Var var;
    private final List<Argument> args;
    private final ASTypeExpr typeRetour;

    public CreerFonction(Var var, Argument[] args, ASTypeExpr typeRetour, Executeur executeurInstance) {
        super(executeurInstance);
        this.var = var;
        this.args = Arrays.asList(args);
        this.typeRetour = typeRetour;
        // declare fonction
        var varFonction = new ASVariable(var.getNom(), null, new ASTypeExpr("fonctionType"));
        var.setNom(varFonction.getNom());
        ASScope.getCurrentScope().declarerVariable(varFonction);
        this.scope = ASScope.makeNewCurrentScope();
    }

    @Override
    public NullType execute() {
        ASScope scope = new ASScope(this.scope);
        String currentScope = executeurInstance.obtenirCoordRunTime().getScope();
        String callingCoord = ASScopeManager.formatNewScope(ASScopeManager.ScopeKind.FONCTION, currentScope, this.var.getNom());
        ASFonction fonction = new ASFonction(
                var.getNom(),
                callingCoord,
                this.args.stream().map(Argument::eval).toArray(ASParametre[]::new),
                this.typeRetour,
                executeurInstance
        );

        ASScope.getCurrentScopeInstance().getVariable(fonction.getNom()).changerValeur(fonction);
        // declare fonction
        // Scope.getCurrentScope().declarerVariable(new ASObjet.Variable(fonction.getNom(), fonction, new Type(fonction.obtenirNomType())));

        // declare params
        for (Argument arg : this.args) {
            ASParametre param = arg.eval();
            scope.declarerVariable(new ASVariable(param.getNom(), param.getValeurParDefaut(), param.getType()));
            //ASObjet.VariableManager.ajouterVariable(new ASObjet.Variable(param.getNom(), param.getValeurParDefaut(), param.getType()), scopeName);
        }

        fonction.setScope(scope);
        scope.setParent(ASScope.getCurrentScopeInstance());

        return null;
    }

    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        String newScope = ASScopeManager.formatNewScope(
                ASScopeManager.ScopeKind.FONCTION,
                coord.getScope(),
                this.var.getNom()
        );
        return new Coordonnee(executeurInstance.nouveauScope(newScope));
    }

    @Override
    public String toString() {
        return "CreerFonction{" +
                "nom=" + var +
                ", args=" + args +
                ", typeRetour?=" + typeRetour +
                '}';
    }
}
