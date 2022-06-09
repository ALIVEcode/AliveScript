package interpreteur.as.lang.datatype;

import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.ASTypeBuiltin;

/**
 * Classe responsable de definir les proprietes des parametres des fonctions
 */
public record ASParametre(String nom, ASTypeExpr type,
                          ASObjet<?> valeurParDefaut) implements ASObjet<Object> {
    /**
     * @param nom             <li>
     *                        Nom du parametre
     *                        </li>
     * @param type            <li>
     *                        Nom du type du parametre (ex: <i>entier</i>, <i>texte</i>, <i>liste</i>, ect.)
     *                        </li>
     *                        <li>
     *                        le parametre peut avoir plusieurs types
     *                        -> separer chaque type par un <b>|</b> (les espaces sont ignores)
     *                        <br> (ex: <i>texte | liste</i>, <i>entier | decimal</i>)
     *                        </li>
     *                        <li>
     *                        Mettre <b>null</b> si le parametre n'a pas de type forcee
     *                        </li>
     * @param valeurParDefaut <li>
     *                        Valeur de type ASObjet qui sera assigne au parametre s'il ne recoit aucune valeur lors de l'appel de la fonction
     *                        </li>
     *                        <li>
     *                        Mettre <b>null</b> pour rendre ce parametre obligatoire lors de l'appel de la fonction
     */
    public ASParametre(String nom, ASTypeExpr type, ASObjet<?> valeurParDefaut) {
        this.nom = nom;
        this.type = type == null ? ASTypeBuiltin.tout.asType() : type;
        this.valeurParDefaut = valeurParDefaut;
    }

    public static ASParametre obligatoire(String nom, ASTypeExpr type) {
        return new ASParametre(nom, type, null);
    }

    public String getNom() {
        return nom;
    }

    public ASTypeExpr getType() {
        return type;
    }

    public ASObjet<?> getValeurParDefaut() {
        return valeurParDefaut;
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
        return this.type.getNom();
    }

    @Override
    public String toString() {
        return "Parametre{" +
               "nom='" + nom + '\'' +
               ", type=" + type +
               ", valeurParDefaut=" + valeurParDefaut +
               '}';
    }

}
