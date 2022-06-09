package interpreteur.as.lang.datatype;


//import interpreteur.ast.buildingBlocs.expressions.Type;


/**
 * Interface que tous les objets d'alivescript doivent r\u00E9aliser
 *
 * @author Mathis Laroche
 */
public interface ASObjet<T> {

    T getValue();

    boolean boolValue();

    String getNomType();

    // TODO override cette méthode dans tous les types de données pour que la fonction info soit actually utile à qq chose

    /**
     * Donne des informations sur l'objet qui pourront \u00EAtre accessibles gr\u00E2ce \u00E0 la fonction builtin info()
     *
     * @return une chaine de caractères contenant les informations, par défaut "No Info"
     */
    default String info() {
        return "No Info";
    }
}



















