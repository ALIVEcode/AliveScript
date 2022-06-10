package interpreteur.as.lang.datatype.structure;

import interpreteur.as.lang.datatype.ASObjet;


public record ASPropriete(String name, ASObjet<?> value) implements ASObjet<Object> {
    @Override
    public Object getValue() {
        return value == null ? null : value.getValue();
    }

    @Override
    public boolean boolValue() {
        return value != null && value.boolValue();
    }

    @Override
    public String getNomType() {
        return this.value.getNomType();
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
