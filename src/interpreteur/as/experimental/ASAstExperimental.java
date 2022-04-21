package interpreteur.as.experimental;

import interpreteur.as.ASAst;
import interpreteur.ast.Ast;
import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.CreerDict;
import interpreteur.ast.buildingBlocs.expressions.CreerListe;
import interpreteur.ast.buildingBlocs.programmes.CreerStructure;
import interpreteur.executeur.Executeur;
import interpreteur.tokens.Token;
import utils.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;


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
    }

    @Override
    protected void ajouterExpressions() {
        super.ajouterExpressions();

        remplacerExpression(
                "BRACES_OUV BRACES_FERM~"
                + "BRACES_OUV #expression BRACES_FERM~"
                + "CROCHET_OUV CROCHET_FERM~"
                + "!expression CROCHET_OUV CROCHET_FERM~"
                + "!expression CROCHET_OUV #expression CROCHET_FERM",
                new Pair<>(
                        "BRACES_OUV BRACES_FERM~"
                        + "BRACES_OUV #expression BRACES_FERM",
                        (p, variante) -> {
                            System.out.println("Dictionary Created");
                            if (variante == 0) {
                                return new CreerDict();
                            }
                            Expression<?> contenu = evalOneExpr(new ArrayList<>(p.subList(1, p.size() - 1)), null);
                            if (contenu instanceof CreerListe.Enumeration enumeration)
                                return enumeration.buildCreerListe();
                            return new CreerDict(contenu);
                        }),
                new Pair<>(
                        "CROCHET_OUV CROCHET_FERM~"
                        + "!expression CROCHET_OUV CROCHET_FERM~"
                        + "!expression CROCHET_OUV #expression CROCHET_FERM",
                        (p, variante) -> {
                            System.out.println("List created");
                            if (variante == 0 || variante == 1) {
                                return new CreerListe();
                            }
                            Expression<?> contenu = evalOneExpr(new ArrayList<>(p.subList(1, p.size() - 1)), null);
                            if (contenu instanceof CreerListe.Enumeration enumeration)
                                return enumeration.buildCreerListe();
                            return new CreerListe(contenu);
                        })
        );
    }


    //----------------- utils -----------------//

    private void changerPatternExpression(String oldPattern, String newPattern) {
        changerPattern(oldPattern, newPattern, expressionsDict, ordreExpressions);
    }

    @SafeVarargs
    private void remplacerExpression(String oldPattern, Pair<String, BiFunction<List<Object>, Integer, ? extends Expression<?>>>... pairs) {
        int idx = retirerExpression(oldPattern);
        for (int i = pairs.length - 1; i >= 0; i--) {
            var pair = pairs[i];
            ajouterExpression(pair.first(), Ast.from(idx, pair.second()));
        }
    }

    private int retirerExpression(String pattern) {
        String nouveauPattern = remplacerCategoriesParMembre(pattern);
        expressionsDict.remove(nouveauPattern);
        int idx = ordreExpressions.indexOf(nouveauPattern);
        ordreExpressions.remove(nouveauPattern);
        return idx;
    }


    private void changerPatternProgramme(String oldPattern, String newPattern) {
        changerPattern(oldPattern, newPattern, programmesDict, ordreProgrammes);
    }


    @SafeVarargs
    private void remplacerProgramme(String oldPattern, Pair<String, BiFunction<List<Object>, Integer, ? extends Programme>>... pairs) {
        int idx = retirerProgramme(oldPattern);
        for (int i = pairs.length - 1; i >= 0; i--) {
            var pair = pairs[i];
            ajouterProgramme(pair.first(), Ast.from(idx, pair.second()));
        }
    }

    private int retirerProgramme(String pattern) {
        String nouveauPattern = remplacerCategoriesParMembre(pattern);
        programmesDict.remove(nouveauPattern);
        int idx = ordreProgrammes.indexOf(nouveauPattern);
        ordreProgrammes.remove(nouveauPattern);
        return idx;
    }


    private void changerPattern(String oldPattern, String newPattern, Hashtable<String, Ast<?>> dict, ArrayList<String> ordre) {
        String ancienPattern = remplacerCategoriesParMembre(oldPattern);
        String nouveauPattern = remplacerCategoriesParMembre(newPattern);
        var ast = dict.remove(ancienPattern);
        dict.put(nouveauPattern, ast);
        int idx = ordre.indexOf(ancienPattern);
        ordre.remove(ancienPattern);
        ordre.add(idx, nouveauPattern);
    }
}

























