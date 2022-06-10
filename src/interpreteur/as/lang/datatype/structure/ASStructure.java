package interpreteur.as.lang.datatype.structure;


import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class ASStructure implements ASObjet<Object> {
    private final ASScope scope;
    private final HashMap<String, ASVariable> proprietesMap;
    private final String nom;

    public ASStructure(String nom, ASVariable[] proprietes, ASScope scope) {
        this.nom = nom;
        this.scope = scope;
        this.proprietesMap = new HashMap<>();
        for (var propriete : proprietes) {
            this.proprietesMap.put(propriete.getNom(), propriete);
        }
    }

    private ASPropriete[] validateProprietes(ASPropriete[] proprietesInstance) {
        var proprietesStructure = this.proprietesMap;

        // 1. Verifier que le nombre de propriétés est correct (nbProprietesObligatoires <= proprietesInstance.length)
        if (proprietesInstance.length > proprietesStructure.size())
            throw new ASErreur.ErreurPropriete(this.nom, "Le nombre de propri\u00E9t\u00E9s donn\u00E9s est '" + proprietesInstance.length +
                    "' alors que la structure en prend au plus '" + proprietesStructure.size() + "'");

        // 2. Faire une hashmap pour comparer les propriétés
        HashMap<String, ASPropriete> proprietesInstanceMap = new HashMap<>();
        for (var propriete : proprietesInstance) {
            proprietesInstanceMap.put(propriete.name(), propriete);
        }

        // 2. Verifier que toutes les propriétés obligatoires ont bien une valeur
        var pasInit = new ArrayList<ASVariable>();
        var proprietesFinales = new ArrayList<ASPropriete>();

        for (var proprieteStructEntry : proprietesStructure.entrySet()) {
            var proprieteInstance = proprietesInstanceMap.getOrDefault(proprieteStructEntry.getKey(), null);
            var proprieteStructure = proprieteStructEntry.getValue();
            if (proprieteStructEntry.getValue().pasInitialisee()) { // Si la propriété est obligatoire
                // Si elle n'est pas initialisée dans la création de l'instance
                if (proprieteInstance == null) pasInit.add(proprieteStructure);
                else proprietesFinales.add(proprieteInstance); // Si elle est initialisée dans la création de l'instance
            } else { // Si la propriété n'est pas obligatoire
                // Si elle n'est pas initialisée dans la création de l'instance
                proprietesFinales.add(Objects.requireNonNullElseGet(
                        proprieteInstance,
                        () -> new ASPropriete(proprieteStructure.getNom(), proprieteStructure.getValeurApresGetter())
                ));
            }
        }

        if (!pasInit.isEmpty()) {
            throw new ASErreur.ErreurPropriete(this.nom, "Les propri\u00E9t\u00E9s obligatoires suivantes n'ont pas \u00E9t\u00E9 donn\u00E9es : " +
                    pasInit.stream().map(ASVariable::getNom).toList());
        }

        return proprietesFinales.toArray(ASPropriete[]::new);
    }

    public StructureInstance makeInstance(ASPropriete[] proprietes) {
        validateProprietes(proprietes);
        return new StructureInstance(this, proprietes);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public boolean boolValue() {
        return false;
    }

    @Override
    public String getNomType() {
        return nom;
    }


    @Override
    public String toString() {
        return "ASStructure{" +
                "scope=" + scope +
                ", proprietesMap=" + proprietesMap +
                ", nom='" + nom + '\'' +
                '}';
    }

    public static class StructureInstance implements ASObjet<Object> {
        private final ASStructure structure;
        private final ASScope.ScopeInstance scopeInstance;
        private final ASPropriete[] proprietes;

        public StructureInstance(ASStructure structure, ASPropriete[] proprietes) {
            this.structure = structure;
            this.scopeInstance = structure.scope.makeScopeInstanceFromScopeParent();
            this.proprietes = proprietes;
            initProprietes();
        }

        private void initProprietes() {
            for (var propriete : proprietes) {
                scopeInstance.getVariable(propriete.name()).changerValeur(propriete.value());
            }
        }

        @Override
        public Object getValue() {
            return this;
        }

        @Override
        public boolean boolValue() {
            return true;
        }

        @Override
        public String getNomType() {
            return structure.nom;
        }

        @Override
        public String toString() {
            return structure.nom + " {" +
                    Arrays.stream(proprietes).map(ASPropriete::toString).collect(Collectors.joining(", ")) +
                    "}";
        }
    }
}
