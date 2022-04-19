package interpreteur.as.experimental;

import interpreteur.as.ASAst;
import interpreteur.ast.buildingBlocs.programmes.CreerStructure;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;


/**
 * Les explications vont être rajouté quand j'aurai la motivation de les écrire XD
 *
 * @author Mathis Laroche
 */


public class ASAstExperimental extends ASAst {
    public ASAstExperimental(Executeur executeurInstance) {
        super(executeurInstance);
    }

    @Override
    protected void ajouterProgrammes() {
        super.ajouterProgrammes();
        ajouterProgramme("STRUCTURE NOM_VARIABLE", p -> {
            System.out.println("Houraaaa!");
            return new CreerStructure(((Token) p.get(1)).obtenirValeur());
        });
    }

    @Override
    protected void ajouterExpressions() {
        super.ajouterExpressions();
    }
}

























