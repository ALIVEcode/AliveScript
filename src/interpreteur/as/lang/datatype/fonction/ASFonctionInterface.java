package interpreteur.as.lang.datatype.fonction;

import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.fonction.ASParametre;
import interpreteur.executeur.Coordonnee;

import java.util.ArrayList;

public interface ASFonctionInterface extends ASObjet<Object> {
    String getNom();

    ASTypeExpr getTypeRetour();

    ASParametre[] getParams();

    ASObjet<?> apply(ArrayList<ASObjet<?>> args);

    Coordonnee getStartingCoord();
}
