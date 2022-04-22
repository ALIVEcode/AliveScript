package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.lang.datatype.ASDict;
import interpreteur.as.lang.datatype.ASListe;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;

public class CreerDict extends CreerListe {
    public CreerDict(Expression<?>... exprs) {
        super(exprs);
    }

    @Override
    public ASDict eval() {
        return new ASDict(getExprs().stream().map(Expression::eval).toArray(ASObjet[]::new));
    }
}
