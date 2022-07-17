package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.ast.buildingBlocs.Programme;

public class FinNamespace extends Programme {

    public FinNamespace() {
        ASFonctionManager.retirerNamespace();
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public String toString() {
        return "FinStructure";
    }
}
