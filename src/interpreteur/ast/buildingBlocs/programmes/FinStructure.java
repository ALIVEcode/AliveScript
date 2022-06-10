package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.datatype.ASNul;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Executeur;
import org.jetbrains.annotations.NotNull;

public class FinStructure extends Programme {
    public FinStructure() {
    }

    @Override
    public Object execute() {
        return new ASNul();
    }
}
