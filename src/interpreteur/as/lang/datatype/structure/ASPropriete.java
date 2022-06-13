package interpreteur.as.lang.datatype.structure;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASConstante;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;

import java.util.Objects;


public final class ASPropriete implements ASObjet<Object> {
    private final String nom;
    private final ASTypeExpr type;
    private ASObjet<?> asValue;
    private boolean isConst;

    public ASPropriete(String nom, ASObjet<?> asValue, ASTypeExpr type, boolean isConst) {
        this.nom = nom;
        this.asValue = asValue instanceof ASVariable variable ? variable.getValeurApresGetterOuNull() : asValue;
        this.type = type;
        this.isConst = isConst;
    }

    public ASPropriete(String nom, ASObjet<?> asValue, ASTypeExpr type) {
        this(nom, asValue, type, false);
    }

    public static ASPropriete obligatoire(String nom, ASTypeExpr type, boolean isConst) {
        return new ASPropriete(nom, null, type, isConst);
    }

    public static ASPropriete optionnelle(String nom, ASObjet<?> asValue, ASTypeExpr type, boolean isConst) {
        return new ASPropriete(nom, asValue, type, isConst);
    }

    public static ASPropriete fromVariable(ASVariable variable) {
        return new ASPropriete(variable.getNom(), variable.getValeurApresGetterOuNull(), variable.getType(), variable instanceof ASConstante);
    }

    public boolean isConst() {
        return isConst;
    }

    public boolean isObligatoire() {
        return asValue == null;
    }

    public void setIsConst(boolean aConst) {
        isConst = aConst;
    }

    public String getNom() {
        return nom;
    }

    public ASTypeExpr getType() {
        return type;
    }

    public ASPropriete copy() {
        return new ASPropriete(nom, asValue, type, isConst);
    }

    @Override
    public Object getValue() {
        return asValue == null ? null : asValue.getValue();
    }

    public void setAsValue(ASObjet<?> asValue) {
        if (isConst) {
            throw new ASErreur.ErreurAssignement("La propri\u00E9t\u00E9 '" + nom + "' est une constante.");
        }
        this.asValue = asValue;
    }

    @Override
    public boolean boolValue() {
        return asValue != null && asValue.boolValue();
    }

    @Override
    public String getNomType() {
        return type.getNom();
    }

    @Override
    public String toString() {
        return nom + ": " + asValue;
    }

    public String name() {
        return nom;
    }

    public ASObjet<?> asValue() {
        return asValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ASPropriete) obj;
        return Objects.equals(this.nom, that.nom) &&
                Objects.equals(this.asValue, that.asValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, asValue);
    }

}
