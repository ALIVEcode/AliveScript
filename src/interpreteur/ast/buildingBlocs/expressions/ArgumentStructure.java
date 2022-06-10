package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.structure.ASPropriete;
import interpreteur.ast.buildingBlocs.Expression;
import org.jetbrains.annotations.Nullable;

public record ArgumentStructure(Var var, @Nullable Expression<?> valeur) implements Expression<ASPropriete> {

    @Override
    public ASPropriete eval() {
        ASObjet<?> valeurPropriete = valeur == null ? null : valeur.eval();
        if (valeurPropriete == null) {
            valeurPropriete = ASScope.getCurrentScopeInstance().getVariable(var.getNom());
        }
        return new ASPropriete(var.getNom(), valeurPropriete);
    }
}