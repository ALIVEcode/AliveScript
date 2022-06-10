package interpreteur.as.lang.datatype.structure;

import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;


public record ASPropriete(String name, ASObjet<?> value) implements ASObjet<Object> {
    public ASPropriete(String name, ASObjet<?> value) {
        this.name = name;
        this.value = value instanceof ASVariable variable ? variable.getValeurApresGetter() : value;
    }

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
