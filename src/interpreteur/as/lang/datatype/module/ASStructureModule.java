package interpreteur.as.lang.datatype.module;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASStructureInterface;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.structure.ASPropriete;
import interpreteur.executeur.Coordonnee;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ASStructureModule implements ASStructureInterface {
    private final ASScope scope;
    private final LinkedHashMap<String, ASPropriete> proprietesMap;
    private final String nom;

    public ASStructureModule(String nom, ASPropriete[] proprietes, ASScope scope) {
        this.nom = nom;
        this.scope = scope;
        this.proprietesMap = new LinkedHashMap<>();
        for (var propriete : proprietes) {
            if (propriete.name().equals("instance")) {
                throw new ASErreur.ErreurDeclaration("Il est interdit de déclarer une propriété portant le nom 'instance' dans une structure");
            }
            this.proprietesMap.put(propriete.name(), propriete);
        }
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public ASObjet<?> apply(ArrayList<ASObjet<?>> args) {
        return null;
    }

    public ASVariable toVariable() {
        return new ASVariable(nom, this, new ASTypeExpr(getNomType()));
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
        return nom;
    }
}
