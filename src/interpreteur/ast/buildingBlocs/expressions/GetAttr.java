package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASHasAttr;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;

public record GetAttr(Expression<?> obj, Var attr) implements Expression<ASObjet<?>> {
    @Override
    public ASObjet<?> eval() {
        ASObjet<?> val = obj.eval();
        if (!(obj.eval() instanceof ASHasAttr hasAttr)) {
            throw new ASErreur.ErreurType("Le type '" + val.getNomType() + "' ne poss\u00E8de pas d'attributs.");
        }
        return hasAttr.getAttr(attr.getNom());
    }
}
