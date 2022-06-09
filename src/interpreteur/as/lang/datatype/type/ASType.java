package interpreteur.as.lang.datatype.type;

import interpreteur.as.lang.datatype.ASObjet;

public abstract sealed class ASType implements ASObjet<Object>
        permits ASTypePrimitive, ASTypeStructure {

    public enum ASTypeKind {
        STRUCTURE,
        LITERAL,
        PRIMITIVE,
        COMPLEX, // TODO later
        ALIAS,
        ALL,


    }


    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean boolValue() {
        return false;
    }

    @Override
    public String getNomType() {
        return null;
    }
}
