package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.lang.datatype.ASDict;
import interpreteur.as.lang.datatype.ASListe;

public class CreerDict extends CreerListe {
    @Override
    public ASDict eval() {
        return new ASDict();
    }
}
