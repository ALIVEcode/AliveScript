package interpreteur.as.lang.managers;

import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.ASType;
import org.jetbrains.annotations.Contract;

public class ASFonctionManager {
    public static final String FONCTION_SCOPE_START = "fonc~";
    public static final String GETTER_SCOPE_START = "get~";
    public static final String SETTER_SCOPE_START = "set~";
    public static final String SCOPE_SEPARATOR = "@";
    private static String structure = "";

    // met la fonction dans le dictionnaire de fonction et cree enregistre la fonction dans une Variable
    // pour que le code puisse la retrouver plus tard
    public static void ajouterFonction(ASFonctionModule fonction) {
        ASScope.getCurrentScope().declarerVariable(new ASVariable(fonction.getNom(), fonction, new ASType(fonction.obtenirNomType())));
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

    @Contract(pure = true)
    public static String makeFunctionNameSignature(String currentScope, String functionName) {
        String s = currentScope + SCOPE_SEPARATOR + functionName;
        return s;
    }

    public static void reset() {
        structure = "";
    }
}
