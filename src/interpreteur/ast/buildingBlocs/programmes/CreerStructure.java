package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Executeur;

public class CreerStructure extends Programme {
    private final String nom;

    public CreerStructure(String nom) {
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
