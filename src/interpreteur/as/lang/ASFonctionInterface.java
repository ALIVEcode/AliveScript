package interpreteur.as.lang;

import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.ASParametre;
import interpreteur.executeur.Coordonnee;

import java.util.ArrayList;

public interface ASFonctionInterface extends ASObjet<Object> {
    String getNom();

    ASType getTypeRetour();

    ASParametre[] getParams();

    ASObjet<?> apply(ArrayList<ASObjet<?>> args);

    Coordonnee getStartingCoord();
}
