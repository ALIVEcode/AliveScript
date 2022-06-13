package interpreteur.as.lang.datatype.structure;


import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.experimental.annotations.Experimental;
import interpreteur.as.experimental.annotations.ExperimentalStage;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASHasAttr;
import interpreteur.as.lang.datatype.ASObjet;

import java.util.*;
import java.util.stream.Collectors;

@Experimental(stage = ExperimentalStage.PROTOTYPE)
public class ASStructure implements ASObjet<Object> {
    private final LinkedHashMap<String, ASPropriete> proprietesMap;
    private final String nom;

    public ASStructure(String nom, ASPropriete[] proprietes) {
        this.nom = nom;
        this.proprietesMap = new LinkedHashMap<>();
        for (var propriete : proprietes) {
            if (propriete.getNom().equals("instance")) {
                throw new ASErreur.ErreurDeclaration("Il est interdit de déclarer une propriété portant le nom 'instance' dans une structure");
            }
            this.proprietesMap.put(propriete.getNom(), propriete);
        }
    }

    public ASStructure(String nom, ASVariable[] variables) {
        this.nom = nom;
        this.proprietesMap = new LinkedHashMap<>();
        for (var variable : variables) {
            if (variable.getNom().equals("instance")) {
                throw new ASErreur.ErreurDeclaration("Il est interdit de déclarer une propriété portant le nom 'instance' dans une structure");
            }
            this.proprietesMap.put(variable.getNom(), ASPropriete.fromVariable(variable));
        }
    }

    private void testProprieteValide(ASPropriete propriete, ASPropriete nouvellePropriete) {
        if (propriete.getType().noMatch(nouvellePropriete.getType())) {
            throw new ASErreur.ErreurAssignement("La propri\u00E9t\u00E9 '" +
                    propriete.getNom() +
                    "' est de type *" +
                    getNomType() +
                    "*. Elle ne peut pas prendre une valeur de type *" +
                    nouvellePropriete.getNomType() +
                    "*.");
        }
    }

    protected ASPropriete[] makeFinalProprietes(ASPropriete[] proprietesInstance) {
        var proprietesStructure = this.getProprietesMap();

        // 1. Verifier que le nombre de propriétés est correct (nbProprietesObligatoires <= proprietesInstance.length)
        if (proprietesInstance.length > proprietesStructure.size())
            throw new ASErreur.ErreurPropriete(this.getNom(), "Le nombre de propri\u00E9t\u00E9s donn\u00E9s est '" + proprietesInstance.length +
                    "' alors que la structure en prend au plus '" + proprietesStructure.size() + "'");

        // 2. Faire une hashmap pour comparer les propriétés
        HashMap<String, ASPropriete> proprietesInstanceMap = new HashMap<>();
        for (var propriete : proprietesInstance) {
            proprietesInstanceMap.put(propriete.name(), propriete);
        }

        // 2. Verifier que toutes les propriétés obligatoires ont bien une valeur
        var pasInit = new ArrayList<ASPropriete>();
        var proprietesFinales = new ArrayList<ASPropriete>();

        for (var proprieteStructEntry : proprietesStructure.entrySet()) {
            var proprieteInstance = proprietesInstanceMap.getOrDefault(proprieteStructEntry.getKey(), null);
            var proprieteStructure = proprieteStructEntry.getValue();
            if (proprieteStructure.isObligatoire()) { // Si la propriété est obligatoire
                // Si elle n'est pas initialisée dans la création de l'instance
                if (proprieteInstance == null) pasInit.add(proprieteStructure);
                else {
                    testProprieteValide(proprieteStructure, proprieteInstance);
                    proprieteInstance.setIsConst(proprieteStructure.isConst());
                    proprietesFinales.add(proprieteInstance); // Si elle est initialisée dans la création de l'instance
                }
            } else { // Si la propriété n'est pas obligatoire
                // Si elle n'est pas initialisée dans la création de l'instance
                var propriete = Objects.requireNonNullElseGet(
                        proprieteInstance,
                        proprieteStructure::copy);
                testProprieteValide(proprieteStructure, propriete);
                propriete.setIsConst(proprieteStructure.isConst());
                proprietesFinales.add(propriete);
            }
        }

        if (!pasInit.isEmpty()) {
            throw new ASErreur.ErreurPropriete(this.getNom(), "Les propri\u00E9t\u00E9s obligatoires suivantes n'ont pas \u00E9t\u00E9 donn\u00E9es : " +
                    pasInit.stream().map(ASPropriete::getNom).toList());
        }

        return proprietesFinales.toArray(ASPropriete[]::new);
    }

    protected LinkedHashMap<String, ASPropriete> getProprietesMap() {
        return proprietesMap;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String getNomType() {
        return "structureType";
    }

    @Override
    public boolean boolValue() {
        return true;
    }

    @Override
    public Object getValue() {
        return this;
    }

    public ASStructureInstance makeInstance(ASPropriete[] proprietes) {
        proprietes = makeFinalProprietes(proprietes);
        return new ASStructureInstance(this, proprietes);
    }

    private String proprieteToString(ASPropriete propriete) {
        return //(propriete.isConst() ? "(const) " : "") +
                propriete.getNom() + ": " +
                        propriete.getNomType() + (propriete.isObligatoire() ? "" : " = " + propriete.asValue());
    }

    @Override
    public String toString() {
        return nom + " {" + proprietesMap.values().stream().map(this::proprieteToString).collect(Collectors.joining(", ")) + "}";
    }

    public static class ASStructureInstance implements ASObjet<Object>, ASHasAttr {
        private final ASStructure structure;
        private final ASPropriete[] proprietes;

        public ASStructureInstance(ASStructure structure, ASPropriete[] proprietes) {
            this.structure = structure;
            this.proprietes = proprietes;
            initProprietes();
        }

        private void initProprietes() {

        }

        private ASPropriete getProprieteOrThrow(String nom) {
            return Arrays.stream(proprietes).filter(p -> p.name().equals(nom)).findFirst().orElseThrow(() -> new ASErreur.ErreurPropriete(
                    structure.getNom(), "La propri\u00E9t\u00E9 '" + nom + "' n'existe pas dans la structure '" + structure.getNom() + "'")
            );
        }

        @Override
        public ASObjet<?> getAttr(String attrName) {
            return getProprieteOrThrow(attrName).asValue();
        }

        @Override
        public void setAttr(String attrName, ASObjet<?> newValue) {
            var propriete = getProprieteOrThrow(attrName);
            propriete.setAsValue(newValue);
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
            return structure.getNom();
        }

        @Override
        public String toString() {
            return structure.getNom() + " {" +
                    Arrays.stream(proprietes).map(ASPropriete::toString).collect(Collectors.joining(", ")) +
                    "}";
        }
    }
}
