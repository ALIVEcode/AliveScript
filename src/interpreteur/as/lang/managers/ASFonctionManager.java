package interpreteur.as.lang.managers;

import interpreteur.as.lang.datatype.fonction.ASFonctionModule;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.ASTypeExpr;

public class ASFonctionManager {
    private static String structure = "";

    // met la fonction dans le dictionnaire de fonction et cree enregistre la fonction dans une Variable
    // pour que le code puisse la retrouver plus tard
    public static void ajouterFonction(ASFonctionModule fonction) {
        ASScope.getCurrentScope().declarerVariable(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
        //VariableManager.ajouterConstante(new Constante(fonction.getNom(), fonction));
        //fonction.nom = ajouterDansStructure(fonction.getNom());
    }

    public static String obtenirNamespace() {
        return structure;
    }

    public static String ajouterDansNamespace(String element) {
        return (structure.isBlank() ? "" : structure + ".") + element;
    }

    public static void ajouterNamespace(String nomStruct) {
        if (nomStruct == null || nomStruct.isBlank()) return;
        structure += (structure.isBlank() ? "" : ".") + nomStruct;
    }

    public static void retirerNamespace() {
        structure = structure.contains(".") ? structure.substring(0, structure.lastIndexOf(".")) : "";

    }

    public static void reset() {
        structure = "";
    }
}
