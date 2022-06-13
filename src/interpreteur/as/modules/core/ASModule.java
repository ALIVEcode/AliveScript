package interpreteur.as.modules.core;

import interpreteur.as.lang.*;
import interpreteur.as.lang.datatype.ASNamespace;
import interpreteur.as.lang.datatype.fonction.ASFonctionModule;
import language.Translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public record ASModule(ASFonctionModule[] fonctions,
                       ASVariable[] variables) {

    public ASModule(ASFonctionModule[] fonctions) {
        this(fonctions, new ASVariable[]{});
    }

    public ASModule(ASVariable[] variables) {
        this(new ASFonctionModule[]{}, variables);
    }

    public ASModule traduire(Translator translator) {
        Arrays.stream(fonctions).forEach(f -> f.setNom(translator.translate(f.getNom())));
        Arrays.stream(variables).forEach(v -> v.setNom(translator.translate(v.getNom())));
        return this;
    }

    private void utiliser() {
        for (ASFonctionModule fonction : fonctions) {
            ASScope.getCurrentScope().declarerVariable(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
        }
        for (ASVariable variable : variables) {
            ASScope.getCurrentScope().declarerVariable(variable.clone());
        }
    }

    private void utiliser(List<String> nomMethodes) {
        for (ASFonctionModule fonction : fonctions) {
            if (nomMethodes.contains(fonction.getNom())) {
                ASScope.getCurrentScope().declarerVariable(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
            }
        }
        for (ASVariable variable : variables) {
            if (nomMethodes.contains(variable.getNom())) {
                ASScope.getCurrentScope().declarerVariable(variable.clone());
            }
        }
    }

    public void utiliser(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            utiliser();
            return;
        }
        var module = new ASNamespace(prefix);
        ASScope.getCurrentScope().declarerVariable(new ASConstante(prefix, module));

        // ASFonctionManager.ajouterNamespace(prefix);
        for (ASFonctionModule fonction : fonctions) {
            // ASScope.getCurrentScope().declarerVariable(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
            module.ajouterObjet(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
        }
        for (ASVariable variable : variables) {
            //ASScope.getCurrentScope().declarerVariable(variable.clone());
            module.ajouterObjet(variable.clone());
        }
        // ASFonctionManager.retirerNamespace();
    }

    public void utiliser(List<String> nomMethodes, String prefix) {
        if (prefix == null || prefix.isBlank()) {
            utiliser(nomMethodes);
            return;
        }
        var module = new ASNamespace(prefix);
        ASScope.getCurrentScope().declarerVariable(new ASVariable(prefix, module, new ASTypeExpr("namespace")));

        // ASFonctionManager.ajouterNamespace(prefix);
        for (ASFonctionModule fonction : fonctions) {
            if (nomMethodes.contains(fonction.getNom())) {
                // ASFonctionManager.ajouterFonction(fonction);
                module.ajouterObjet(new ASVariable(fonction.getNom(), fonction, new ASTypeExpr(fonction.getNomType())));
            }
        }
        for (ASVariable variable : variables) {
            if (nomMethodes.contains(variable.getNom())) {
                ASScope.getCurrentScope().declarerVariable(variable.clone());
                module.ajouterObjet(variable.clone());
            }
        }
        // ASFonctionManager.retirerNamespace();
    }

    /**
     * @return un array contenant toutes les fonctions du module
     */
    public ASFonctionModule[] getFonctions() {
        return fonctions;
    }

    /**
     * @return un array contenant toutes les variables du module
     */
    public ASVariable[] getVariables() {
        return variables;
    }

    /**
     * @return la liste des noms des fonctions du module
     */
    public List<String> getNomsFonctions() {
        if (fonctions.length == 0) return new ArrayList<>();
        return Stream.of(fonctions).map(ASFonctionModule::getNom).collect(Collectors.toList());
    }

    /**
     * @return la liste des noms des constantes du module
     */
    public List<String> getNomsVariables() {
        if (variables.length == 0) return new ArrayList<>();
        return Stream.of(variables).map(ASVariable::getNom).collect(Collectors.toList());
    }

    /**
     * @return la liste des noms des constantes du module
     */
    public List<String> getNomsConstantesEtFonctions() {
        List<String> noms = getNomsFonctions();
        noms.addAll(getNomsVariables());
        return noms;
    }

    @Override
    public String toString() {
        return "Module{\n" +
                "fonctions=" + Arrays.toString(fonctions) + "\n" +
                ", variables=" + Arrays.toString(variables) + "\n" +
                '}';
    }
}














