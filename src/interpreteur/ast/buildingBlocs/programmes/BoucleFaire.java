package interpreteur.ast.buildingBlocs.programmes;

import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.executeur.Coordonnee;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;

import java.util.List;

public class BoucleFaire extends Programme {
    public BoucleFaire(Executeur executeurInstance) {
        super(executeurInstance);
    }

    @Override
    public Object execute() {
        assert this.executeurInstance != null;
        this.executeurInstance.obtenirCoordRunTime().nouveauBloc("faire");
        return null;
    }


    @Override
    public Coordonnee prochaineCoord(Coordonnee coord, List<Token> ligne) {
        return coord.nouveauBloc("faire");
    }


    @Override
    public String toString() {
        return "BoucleFaire";
    }
}
