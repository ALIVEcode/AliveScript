package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.lang.datatype.fonction.ASFonctionInterface;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;

public record AppelFonc(Expression<?> var,
                        CreerListe args) implements Expression<ASObjet<?>> {

    @Override
    public ASObjet<?> eval() {
        ASObjet<?> fonction = var.eval();
        if (!(fonction instanceof ASFonctionInterface f)) {
            throw new ASErreur.ErreurTypePasAppelable("Un \u00E9l\u00E9ment de type '" + fonction.getNomType() + "' ne peut pas \u00EAtre appel\u00E9");
        }
        return f.apply(args.eval().getValue());
    }

    @Override
    public String toString() {
        return "AppelFonc{" +
               "nom=" + var +
               ", args=" + args +
               '}';
    }
}
