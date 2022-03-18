package interpreteur.ast.buildingBlocs;

import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.NullType;
import java.io.Serializable;
import java.util.List;

public abstract class Programme implements Serializable {
    private int numLigne = -1;
    protected final Executeur executeurInstance;

    protected Programme() {
        this.executeurInstance = null;
    }

    protected Programme(@NotNull Executeur executeurInstance) {
        this.executeurInstance = executeurInstance;
    }

    public static Programme evalExpression(Expression<?> expression, String toString) {
        return new Programme() {
            @Override
            public Object execute() {
                expression.eval();
                return null;
            }

            @Override
            public String toString() {
                return toString;
            }
        };
    }

    public int getNumLigne() {
        return numLigne;
    }

    public void setNumLigne(int numLigne) {
        this.numLigne = numLigne;
    }

    /**
     * appelé au runtime
     */
    public abstract Object execute();

    public String transpile(String language) {
        return "";
    }

    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        return coord;
    }

    @Override
    public String toString() {
        return "vide";
    }

    /**
     * INDIQUE LA FIN DU PROGRAMME
     */
    public static class ProgrammeFin extends Programme {
        @Override
        public NullType execute() {
            return null;
        }

        @Override
        public String toString() {
            return "'FIN'";
        }
    }
}
