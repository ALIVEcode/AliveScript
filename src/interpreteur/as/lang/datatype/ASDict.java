package interpreteur.as.lang.datatype;

public class ASDict extends ASListe {

    public ASDict(ASObjet<?>... valeurs) {
        super(valeurs);
    }

    @Override
    public boolean estDict() {
        return true;
    }

    @Override
    public String toString() {
        return toString('{', '}');
    }
}
