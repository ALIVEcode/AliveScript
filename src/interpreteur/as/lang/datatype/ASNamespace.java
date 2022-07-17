package interpreteur.as.lang.datatype;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASVariable;

import java.util.ArrayList;

public record ASNamespace(String nom, ArrayList<ASVariable> contenu) implements ASObjet<Object>, ASHasAttr {

    public ASNamespace(String nom) {
        this(nom, new ArrayList<>());
    }

    @Override
    public ASObjet<?> getAttr(String attrName) {
        return contenu.stream()
                .filter(objet -> objet.getNom().equals(attrName))
                .findFirst()
                .orElseThrow(() -> new ASErreur.ErreurMembreModule(nom,
                        "L'\u00E9l\u00E9ment '" + attrName + "' n'est pas d\u00E9fini.")
                ).getValeurApresGetter();
    }

    @Override
    public void setAttr(String attrName, ASObjet<?> newValue) {

    }

    public void ajouterObjet(ASVariable objet) {
        contenu.add(objet);
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public boolean boolValue() {
        return false;
    }

    @Override
    public String getNomType() {
        return "namespace";
    }

    @Override
    public String toString() {
        return new ASListe(contenu.stream().map(ASVariable::getNom).map(ASTexte::new).toArray(ASObjet[]::new)).toString();
    }
}
