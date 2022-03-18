package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Executeur;

import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class Boucle extends Programme {
    private final String nomBoucle;

    public static HashMap<String, ArrayList<Boucle>> boucles = new HashMap<>();

    public abstract void sortir();

    public static void sortirScope(String scope) {
        Boucle.boucles.getOrDefault(scope, new ArrayList<>()).forEach(Boucle::sortir);
    }

    protected Boucle(String nomBoucle, Executeur executeurInstance) {
        super(executeurInstance);
        this.nomBoucle = nomBoucle;
        boucles.getOrDefault(executeurInstance.obtenirCoordRunTime().toString(), new ArrayList<>()).add(this);
    }

    public String getNomBoucle() {
        return nomBoucle;
    }

    public static class Sortir extends Programme {
        public Sortir(Executeur executeurInstance) {
            super(executeurInstance);
        }
        @Override
        public NullType execute() {
            assert executeurInstance != null;
            switch (executeurInstance.obtenirCoordRunTime().getBoucleActuelle()) {
                case "pour" -> BouclePour.sortir = true;
                case "repeter" -> BoucleRepeter.sortir = true;
                case "tant_que" -> BoucleTantQue.sortir = true;
                default -> throw new ASErreur.ErreurSyntaxe("Il faut \u00EAtre dans une boucle pour pouvoir utiliser le mot clef 'sortir'");
            }
            executeurInstance.obtenirCoordRunTime().recommencerBoucleActuelle();
            return null;
        }
    }

    public static class Continuer extends Programme {
        public Continuer(Executeur executeurInstance) {
            super(executeurInstance);
        }
        @Override
        public NullType execute() {
            assert executeurInstance != null;
            if (executeurInstance.obtenirCoordRunTime().getBoucleActuelle() == null) {
                throw new ASErreur.ErreurSyntaxe("Il faut \u00EAtre dans une boucle pour pouvoir utiliser le mot clef 'continuer'");
            }
            executeurInstance.obtenirCoordRunTime().recommencerBoucleActuelle();
            return null;
        }
    }
}
