package interpreteur.as.lang.datatype.structure;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;

import java.util.Objects;


public final class ASPropriete implements ASObjet<Object> {
    private final String name;
    private ASObjet<?> asValue;
    private boolean isConst;

    public ASPropriete(String name, ASObjet<?> asValue) {
        this.name = name;
        this.asValue = asValue instanceof ASVariable variable ? variable.getValeurApresGetter() : asValue;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setIsConst(boolean aConst) {
        isConst = aConst;
    }

    @Override
    public Object getValue() {
        return asValue == null ? null : asValue.getValue();
    }

    public void setAsValue(ASObjet<?> asValue) {
        if (isConst) {
            throw new ASErreur.ErreurAssignement("La propri\u00E9t\u00E9 '" + name + "' est une constante.");
        }
        this.asValue = asValue;
    }

    @Override
    public boolean boolValue() {
        return asValue != null && asValue.boolValue();
    }

    @Override
    public String getNomType() {
        return this.asValue.getNomType();
    }

    @Override
    public String toString() {
        return name + ": " + asValue;
    }

    public String name() {
        return name;
    }

    public ASObjet<?> asValue() {
        return asValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ASPropriete) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.asValue, that.asValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, asValue);
    }

}
