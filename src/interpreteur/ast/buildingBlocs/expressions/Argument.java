package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.ASParametre;
import interpreteur.ast.buildingBlocs.Expression;

public class Argument implements Expression<ASParametre> {
    private final Var var;
    private final ASObjet<?> valeurParDefaut;
    private final ASTypeExpr type;

    public Argument(Var var, Expression<?> valeurParDefaut, ASTypeExpr type) {
        this.var = var;
        this.type = type;
        try {
            this.valeurParDefaut = valeurParDefaut == null ? null : valeurParDefaut.eval();
        } catch (ASErreur.ErreurVariableInconnue e) {
            throw new ASErreur.ErreurVariableInconnue("Impossible d'utiliser une variable comme valeur par défaut d'une fonction");
        }
        if (this.type != null && this.valeurParDefaut != null && this.type.noMatch(this.valeurParDefaut.getNomType())) {
            throw new ASErreur.ErreurType("Le parametre '" + var.getNom() + "' est de type '" + type.getNom() +
                    "', mais la valeur par d\u00E9faut est de type '" + this.valeurParDefaut.getNomType() + "'.");
        }
    }


    @Override
    public ASParametre eval() {
        return new ASParametre(var.getNom(), type, valeurParDefaut);
    }

    @Override
    public String toString() {
        return "Argument{" +
                "nom=" + var +
                ", valeurParDefaut?=" + valeurParDefaut +
                ", type?=" + type +
                '}';
    }
}
