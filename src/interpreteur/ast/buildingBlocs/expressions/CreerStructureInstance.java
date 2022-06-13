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
public class CreerStructureInstance implements Expression<ASStructure.ASStructureInstance> {
    private final Expression<?> varStructure;
    private final ArgumentStructure[] argumentStructures;

    public CreerStructureInstance(Expression<?> varStructure, ArgumentStructure[] argumentStructures) {
        this.varStructure = varStructure;
        this.argumentStructures = argumentStructures;
    }

    @Override
    public ASStructure.ASStructureInstance eval() {
        // 1. Récupérer la structure
        var valeur = varStructure.eval();
        if (!(valeur instanceof ASStructure structure)) {
            throw new ASErreur.ErreurType("Les \u00E9l\u00E9ments de type '" + valeur.getNomType() + "' ne sont pas des structures et ne peuvent pas \u00EAtre construits.");
        }
        // 2. Récupérer les valeurs des propriétés
        ASPropriete[] proprietes = Arrays.stream(argumentStructures).map(ArgumentStructure::eval).toArray(ASPropriete[]::new);
        // 3. Créer une instance de structure
        return structure.makeInstance(proprietes);
    }
}














