package interpreteur.as.lang.datatype;

import java.util.ArrayList;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public record Type<Arg>(String nom, BiPredicate<ASObjet<?>, Arg> condition,
                        Arg arg) implements ASObjet<Object> {

    public Type(String nom) {
        this(nom, null);
    }

    public Type(String nom, BiPredicate<ASObjet<?>, Arg> condition) {
        this(nom, condition, null);
    }

    public static void main(String[] args) {
        var typeListe = new Type<Class<ASEntier>>("liste", (asObjet, arg) -> {
            if (!(asObjet instanceof ASListe liste)) return false;
            if (arg == null) return true;
            return liste.getValue().stream().allMatch(arg::isInstance);
        });
    }

    public boolean match(ASObjet<?> o) {
        return condition.test(o, arg);
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
    public String obtenirNomType() {
        return null;
    }
}
