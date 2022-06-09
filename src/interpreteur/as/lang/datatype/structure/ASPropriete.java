package interpreteur.as.lang.datatype.structure;

import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.ASObjet;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ASPropriete implements ASObjet<Object> {
    private final String name;
    private final ASTypeExpr type;
    private final @Nullable ASObjet<?> defaultValue;

    public ASPropriete(String name, ASTypeExpr type, @Nullable ASObjet<?> defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
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

    public String name() {
        return name;
    }

    public ASTypeExpr type() {
        return type;
    }

    public @Nullable ASObjet<?> defaultValue() {
        return defaultValue;
    }

}
