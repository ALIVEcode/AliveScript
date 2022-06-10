package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.structure.ASPropriete;
import interpreteur.as.lang.datatype.structure.ASStructure;
import interpreteur.ast.buildingBlocs.Expression;

import java.util.Arrays;

/*
 * Structure {}
 * Structure { prop1: expr1, prop2: expr2 }
 *
 * var prop1 = expr1
 * var prop2 = expr2
 * Structure { prop1, prop2 }
 */
public class CreerStructureInstance implements Expression<ASStructure.StructureInstance> {
    private final Var varStructure;
    private final ArgumentStructure[] argumentStructures;

    public CreerStructureInstance(Var varStructure, ArgumentStructure[] argumentStructures) {
        this.varStructure = varStructure;
        this.argumentStructures = argumentStructures;
    }

    @Override
    public ASStructure.StructureInstance eval() {
        // 1. Récupérer la structure
        if (!(varStructure.eval() instanceof ASStructure structure)) {
            throw new ASErreur.ErreurVariableInconnue("La variable '" + varStructure.getNom() + "' n'est pas une structure.");
        }
        // 2. Récupérer les valeurs des propriétés
        ASPropriete[] proprietes = Arrays.stream(argumentStructures).map(ArgumentStructure::eval).toArray(ASPropriete[]::new);
        // 3. Créer une instance de structure
        return structure.makeInstance(proprietes);
    }
}
