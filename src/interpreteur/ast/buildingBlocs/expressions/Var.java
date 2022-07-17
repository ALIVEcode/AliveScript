package interpreteur.ast.buildingBlocs.expressions;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.ast.buildingBlocs.Expression;

import java.util.Objects;

public class Var implements Expression<ASObjet<?>> {
    private String nom;

    public Var(String nom) {
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Var{" +
                "nom='" + nom + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Var var)) return false;
        return nom.equals(var.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    public ASVariable getVariableOrThrow() {
        var variable = ASScope.getCurrentScopeInstance().getVariable(nom);
        if (variable == null) {
            throw new ASErreur.ErreurVariableInconnue("La variable '" + this.nom + "' n'est pas d\u00E9clar\u00E9e dans ce scope.");
        }
        return variable;
    }

    /**
     * @return la valeur dans le Nom
     */
    @Override
    public ASObjet<?> eval() {
        return getVariableOrThrow().getValeurApresGetter();
    }
}
