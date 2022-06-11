package interpreteur.as.lang;

import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.ASParametre;
import interpreteur.executeur.Coordonnee;

import java.util.ArrayList;

public interface ASStructureInterface extends ASObjet<Object> {
    String getNom();

    ASObjet<?> apply(ArrayList<ASObjet<?>> args);

}
