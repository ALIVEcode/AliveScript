package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.ast.buildingBlocs.Programme;

public class CreerNamespace extends Programme {
    private final String nom;

    public CreerNamespace(String nom) {
        this.nom = nom;
        ASFonctionManager.ajouterNamespace(nom);
    }

    public String getNom() {
        return nom;
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public String toString() {
        return "CreerStructure{" +
                "nom='" + nom + '\'' +
                '}';
    }
}
