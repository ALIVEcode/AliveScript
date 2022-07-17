package interpreteur.as.lang;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ASTypeExpr implements Expression<ASObjet<?>> {

    private String nom;

    public ASTypeExpr(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return this.nom;
    }

    public String getNomFormatted() {
        return nom.equals("tout") ? null : nom
                .replace("nombre", "entier|decimal")
                .replace("iterable", "texte|liste");
    }

    public List<String> getNomAsList() {
        return this.getNomFormatted() == null ? null : Arrays.asList(this.getNomFormatted().split("\\|"));
    }

    public void union(ASTypeExpr type) {
        if (type.getNomFormatted() == null)
            this.nom = "tout";
        else if (this.noMatch(type)) {
            this.nom += "|" + type.nom;
        }
    }

    public void union(String type) {
        if (type.equals("tout"))
            this.nom = "tout";
        else if (this.noMatch(type))
            this.nom += "|" + type
                    .replace("nombre", "entier|decimal")
                    .replace("iterable", "texte|liste");
    }

    public boolean noMatch(Object o) {
        if (this == o) return false;

        if (o instanceof String s) {
            List<String> type = Arrays.asList(s.split("\\|"));
            return this.getNomFormatted() != null && !type.contains("tout") && !type.contains("nulType") && this.getNomAsList().stream().noneMatch(type::contains);
        } else if (o instanceof ASTypeExpr t) {
            List<String> type = t.getNomAsList();
            return this.getNomFormatted() != null && type != null && this.getNomAsList().stream().noneMatch(type::contains);

        } else return true;
    }

    public boolean match(Object o) {
        return !noMatch(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    @Override
    public ASObjet<?> eval() {
        ASVariable var;
        if ((var = ASScope.getCurrentScopeInstance().getVariable(this.nom)) != null) {
            return var.getValeurApresGetter();
        }
        throw new ASErreur.ErreurType("Il est impossible d'\u00E9valuer le type '" + this.nom + "'");
    }

    @Override
    public String toString() {
        return "Type{" +
                "nom=" + nom +
                '}';
    }


}
