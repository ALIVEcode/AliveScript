package interpreteur.generateurs.ast;

import interpreteur.ast.Ast;
import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.generateurs.lexer.LexerGenerator;
import utils.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public record AstFrame(
        Pair<Hashtable<String, Ast<? extends Programme>>, ArrayList<String>> programmes,
        Pair<Hashtable<String, Ast<? extends Expression<?>>>, ArrayList<String>> expressions
) {
    public AstFrame() {
        this(
                new Pair<>(new Hashtable<>(), new ArrayList<>()),  // programmes
                new Pair<>(new Hashtable<>(), new ArrayList<>())   // expressions
        );
    }

    public Hashtable<String, Ast<? extends Programme>> programmesDict() {
        return programmes.first();
    }

    public Hashtable<String, Ast<? extends Expression<?>>> expressionsDict() {
        return expressions.first();
    }

    public ArrayList<String> ordreProgrammes() {
        return programmes.second();
    }

    public ArrayList<String> ordreExpressions() {
        return expressions.second();
    }

    public void ajouterProgramme(String pattern, Ast<? extends Programme> fonction) {
        pattern = LexerGenerator.remplacerCategoriesParMembre(pattern);
        var sousAstCopy = new Hashtable<>(fonction.getSousAst());
        for (String p : sousAstCopy.keySet()) {
            fonction.getSousAst().remove(p);
            fonction.getSousAst().put(pattern, sousAstCopy.get(p));
        }
        if (fonction.getImportance() == -1)
            fonction.setImportance(ordreProgrammes().size());
        var previous = programmesDict().put(pattern, fonction);
        if (previous == null) {
            ordreProgrammes().add(fonction.getImportance(), pattern);
        }
    }

    public void ajouterProgramme(String pattern, Function<List<Object>, ? extends Programme> fonction) {
        ajouterProgramme(pattern, Ast.from(fonction));
    }

    public void ajouterProgramme(String pattern, BiFunction<List<Object>, Integer, ? extends Programme> fonction) {
        ajouterProgramme(pattern, Ast.from(fonction));
    }

    public void ajouterExpression(String pattern, Ast<? extends Expression<?>> fonction) {
        String nouveauPattern = LexerGenerator.remplacerCategoriesParMembre(pattern);
        if (fonction.getImportance() == -1)
            fonction.setImportance(ordreExpressions().size());
        var previous = expressionsDict().put(nouveauPattern, fonction);
        if (previous == null) {
            ordreExpressions().add(fonction.getImportance(), nouveauPattern);
        }
    }

    public void ajouterExpression(String pattern, Function<List<Object>, ? extends Expression<?>> fonction) {
        ajouterExpression(pattern, Ast.from(fonction));
    }

    public void ajouterExpression(String pattern, BiFunction<List<Object>, Integer, ? extends Expression<?>> fonction) {
        ajouterExpression(pattern, Ast.from(fonction));
    }
}
