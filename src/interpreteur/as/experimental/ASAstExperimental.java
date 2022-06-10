package interpreteur.as.experimental;

import interpreteur.as.ASAst;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.ASNul;
import interpreteur.ast.Ast;
import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.*;
import interpreteur.ast.buildingBlocs.programmes.*;
import interpreteur.executeur.Executeur;
import interpreteur.generateurs.ast.AstFrameKind;
import interpreteur.generateurs.lexer.LexerGenerator;
import interpreteur.tokens.Token;
import utils.Pair;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * Les explications vont être rajouté quand j'aurai la motivation de les écrire XD
 *
 * @author Mathis Laroche
 */
public class ASAstExperimental extends ASAst {
    public ASAstExperimental(Executeur executeurInstance) {
        super(executeurInstance);
        // set the kind of the ast to STRUCTURE to define it.
        defineAstFrame(AstFrameKind.STRUCTURE);
        ajouterProgrammesStructure();
        ajouterExpressionsStructure();
        // puts the current ast frame to the default one.
        pushAstFrame(AstFrameKind.DEFAULT);
    }

    @Override
    protected void ajouterProgrammes() {
        super.ajouterProgrammes();

        remplacerProgramme("FONCTION expression PARENT_OUV expression PARENT_FERM FLECHE expression~" +
                        "FONCTION expression PARENT_OUV expression PARENT_FERM~" +
                        "FONCTION expression PARENT_OUV PARENT_FERM FLECHE expression~" +
                        "FONCTION expression PARENT_OUV PARENT_FERM",
                new Ast<CreerFonction>(
                        Map.entry(
                                "expression DEUX_POINTS expression ASSIGNEMENT expression~"
                                        + "expression ASSIGNEMENT expression~"
                                        + "expression DEUX_POINTS expression",
                                new Ast<Argument>(19) {
                                    @Override
                                    public Argument apply(List<Object> p, Integer idxVariante) {
                                        ASTypeExpr type = new ASTypeExpr("tout");
                                        Expression<?> valParDefaut = null;

                                        if (!(p.get(0) instanceof Var var)) {
                                            throw new ASErreur.ErreurSyntaxe("Une d\u00E9claration de fonction doit commencer par une variable, pas par " + p.get(0));
                                        }

                                        Token deuxPointsToken = (Token) p.stream()
                                                .filter(t -> t instanceof Token token && token.getNom().equals("DEUX_POINTS"))
                                                .findFirst()
                                                .orElse(null);
                                        if (deuxPointsToken != null) {
                                            Expression<?> typeObj = (Expression<?>) p.get(p.indexOf(deuxPointsToken) + 1);
                                            if (!(typeObj instanceof ASTypeExpr)) {
                                                String nom;
                                                if (p.get(0) instanceof Var) {
                                                    nom = ((Var) typeObj).getNom();
                                                } else {
                                                    nom = typeObj.eval().toString();
                                                }
                                                throw new ASErreur.ErreurType("Le symbole ':' doit \u00EAtre suivi d'un type valide ('" + nom + "' n'est pas un type valide)");
                                            }
                                            type = (ASTypeExpr) typeObj;
                                        }
                                        Token assignementToken = (Token) p.stream()
                                                .filter(t -> t instanceof Token token && token.getNom().equals("ASSIGNEMENT"))
                                                .findFirst()
                                                .orElse(null);
                                        if (assignementToken != null) {
                                            valParDefaut = (Expression<?>) p.get(p.indexOf(assignementToken) + 1);
                                        }

                                        return new Argument(var, valParDefaut, type);
                                    }
                                })
                ) {
                    @Override
                    public CreerFonction apply(List<Object> p, Integer idxVariante) {
                        pushAstFrame(AstFrameKind.DEFAULT);

                        Argument[] params = new Argument[]{};

                        ASTypeExpr typeRetour = p.get(p.size() - 1) instanceof ASTypeExpr type ? type : new ASTypeExpr("tout");

                        if (p.get(p.size() - 1) == null && p.get(3) instanceof ASTypeExpr type) {
                            typeRetour = type;
                            return new CreerFonction((Var) p.get(1), params, typeRetour, executeurInstance);
                        }

                        if (p.get(3) != null && !(p.get(3) instanceof Token)) {
                            if (p.get(3) instanceof CreerListe.Enumeration enumeration) {
                                params = enumeration.getExprs()
                                        .stream()
                                        .map(expr -> expr instanceof Argument arg
                                                ? arg
                                                : new Argument((Var) expr, null, null))
                                        .toArray(Argument[]::new);
                            } else if (p.get(3) instanceof Argument arg) {
                                params = new Argument[]{arg};
                            } else {
                                params = new Argument[]{
                                        new Argument((Var) p.get(3), null, null)
                                };
                            }
                        }

                        return new CreerFonction((Var) p.get(1), params, typeRetour, executeurInstance);
                    }
                });

        remplacerProgramme("RETOURNER~" +
                        "RETOURNER expression",
                (p, variante) -> {
                    popAstFrame();
                    if (variante == 1 && p.get(1) instanceof CreerListe.Enumeration enumeration)
                        p.set(1, enumeration.buildCreerListe());
                    return new Retourner(variante == 1 ? (Expression<?>) p.get(1) : new ValeurConstante(new ASNul()));
                });

        remplacerProgramme("FIN FONCTION", p -> {
            popAstFrame();
            return new FinFonction(executeurInstance);
        });

        remplacerProgramme("STRUCTURE NOM_VARIABLE", p -> {
            pushAstFrame(AstFrameKind.STRUCTURE);
            return new DefinirStructure(new Var(((Token) p.get(1)).getValeur()), executeurInstance);
        });

        remplacerProgramme("FIN STRUCTURE", p -> {
            throw new ASErreur.ErreurFermeture(executeurInstance.obtenirCoordRunTime().getBlocActuel(), "fin structure");
        });
    }

    @Override
    protected void ajouterExpressions() {
        ajouterExpression("NOM_VARIABLE BRACES_OUV #expression VIRGULE BRACES_FERM~" +
                "NOM_VARIABLE BRACES_OUV #expression BRACES_FERM~" +
                "NOM_VARIABLE BRACES_OUV BRACES_FERM", (p, variante) -> {
            var varStructure = new Var(((Token) p.get(0)).getValeur());

            Hashtable<String, Ast<? extends Expression<?>>> astParams = new Hashtable<>();

            astParams.put("expression DEUX_POINTS expression", new Ast<ArgumentStructure>(-2) {
                @Override
                public ArgumentStructure apply(List<Object> p, Integer variante) {
                    if (p.get(0) instanceof Var) {
                        return new ArgumentStructure((Var) p.get(0), (Expression<?>) p.get(2));
                    } else {
                        throw new ASErreur.ErreurSyntaxe("Une d\u00E9finition de propri\u00E9t\u00E9 d'une structure " +
                                "doit commencer par une variable.");
                    }
                }
            });

            ArgumentStructure[] argsStructure = new ArgumentStructure[]{};
            if (variante == 2) {
                return new CreerStructureInstance(varStructure, argsStructure);
            }
            int lastIndex = variante == 1 ? p.size() - 1 : p.size() - 2;
            Expression<?> contenu = evalOneExpr(new ArrayList<>(p.subList(2, lastIndex)), astParams);

            if (contenu instanceof ArgumentStructure argumentStructure) {
                argsStructure = new ArgumentStructure[]{argumentStructure};
            } else if (contenu instanceof CreerListe.Enumeration enumeration) {

                argsStructure = enumeration.getExprs()
                        .stream()
                        .map(expr -> {
                            if (expr instanceof ArgumentStructure arg) {
                                return arg;
                            } else if (expr instanceof Var var) {
                                return new ArgumentStructure(var, null);
                            } else {
                                throw new ASErreur.ErreurType("Une structure doit contenir des variables ou des arguments");
                            }
                        })
                        .toArray(ArgumentStructure[]::new);
            }

            return new CreerStructureInstance(varStructure, argsStructure);
        });

        super.ajouterExpressions();

        ajouterExpression("expression POINT expression", new Ast<GetAttr>(3) {
            @Override
            public GetAttr apply(List<Object> p, Integer idxVariante) {
                return new GetAttr((Expression<?>) p.get(0), (Var) p.get(2));
            }
        });
    }

    private void ajouterProgrammesStructure() {
        ajouterProgramme("CONSTANTE expression~"
                        + "CONSTANTE expression DEUX_POINTS expression~"
                        + "VAR expression~"
                        + "VAR expression DEUX_POINTS expression",
                (p, variante) -> {
                    /*
                     * TODO erreur si c'est pas une Var qui est passé comme expression à gauche du assignment
                     */
                    // si le premier mot est "const"
                    boolean estConst = variante < 2;

                    // le type de la variable déclarer (null signifie qu'il n'est pas mentionné dans la déclaration)
                    ASTypeExpr type = null;

                    /*
                     * Déclaration sous une des formes:
                     * 1. const x: type = valeur
                     * 4. var x: type = valeur
                     * 5. var x: type
                     */
                    if (variante == 1 || variante == 3) {
                        // si le type précisé n'est pas un type
                        if (!(p.get(3) instanceof ASTypeExpr _type))
                            throw new ASErreur.ErreurType("Dans une d\u00E9claration de " +
                                    (estConst ? "constante" : "variable") +
                                    ", les deux points doivent \u00EAtre suivi d'un type valide");
                        type = _type;
                    }

                    // on retourne l'objet Declarer
                    return new Declarer((Expression<?>) p.get(1), null, type, estConst);
                });


        ajouterProgramme("FIN STRUCTURE", p -> {
            popAstFrame();
            return new CreerStructure(executeurInstance);
        });
    }


    private void ajouterExpressionsStructure() {
        ajouterExpressions();
    }

    //----------------- utils -----------------//

    private void changerPatternExpression(String oldPattern, String newPattern) {
        changerPattern(oldPattern, newPattern, currentExpressionsDict(), currentOrdreExpressions());
    }

    @SafeVarargs
    private void remplacerExpression(String oldPattern, Pair<String, BiFunction<List<Object>, Integer, ? extends Expression<?>>>... pairs) {
        int idx = retirerExpression(oldPattern);
        for (int i = pairs.length - 1; i >= 0; i--) {
            var pair = pairs[i];
            ajouterExpression(pair.first(), Ast.from(idx, pair.second()));
        }
    }

    private void remplacerExpression(String oldPattern, Ast<? extends Expression<?>> expression) {
        int idx = retirerProgramme(oldPattern);
        expression.setImportance(idx);
        ajouterExpression(oldPattern, expression);
    }

    private void remplacerExpression(String oldPattern, BiFunction<List<Object>, Integer, ? extends Expression<?>> func) {
        int idx = retirerProgramme(oldPattern);
        ajouterExpression(oldPattern, Ast.from(idx, func));
    }

    private void remplacerExpression(String oldPattern, Function<List<Object>, ? extends Expression<?>> func) {
        int idx = retirerProgramme(oldPattern);
        ajouterExpression(oldPattern, Ast.from(idx, func));
    }

    private int retirerExpression(String pattern) {
        String nouveauPattern = LexerGenerator.remplacerCategoriesParMembre(pattern);
        currentExpressionsDict().remove(nouveauPattern);
        int idx = currentOrdreExpressions().indexOf(nouveauPattern);
        currentOrdreExpressions().remove(nouveauPattern);
        return idx;
    }


    private void changerPatternProgramme(String oldPattern, String newPattern) {
        changerPattern(oldPattern, newPattern, currentProgrammesDict(), currentOrdreProgrammes());
    }


    @SafeVarargs
    private void remplacerProgramme(String oldPattern, Pair<String, BiFunction<List<Object>, Integer, ? extends Programme>>... pairs) {
        int idx = retirerProgramme(oldPattern);
        for (int i = pairs.length - 1; i >= 0; i--) {
            var pair = pairs[i];
            ajouterProgramme(pair.first(), Ast.from(idx, pair.second()));
        }
    }

    private void remplacerProgramme(String oldPattern, Ast<? extends Programme> program) {
        int idx = retirerProgramme(oldPattern);
        program.setImportance(idx);
        ajouterProgramme(oldPattern, program);
    }

    private void remplacerProgramme(String oldPattern, BiFunction<List<Object>, Integer, ? extends Programme> func) {
        int idx = retirerProgramme(oldPattern);
        ajouterProgramme(oldPattern, Ast.from(idx, func));
    }

    private void remplacerProgramme(String oldPattern, Function<List<Object>, ? extends Programme> func) {
        int idx = retirerProgramme(oldPattern);
        ajouterProgramme(oldPattern, Ast.from(idx, func));
    }

    private int retirerProgramme(String pattern) {
        String nouveauPattern = LexerGenerator.remplacerCategoriesParMembre(pattern);
        currentProgrammesDict().remove(nouveauPattern);
        int idx = currentOrdreProgrammes().indexOf(nouveauPattern);
        currentOrdreProgrammes().remove(nouveauPattern);
        return idx;
    }


    private <T> void changerPattern(String oldPattern, String newPattern, Hashtable<String, Ast<? extends T>> dict, ArrayList<String> ordre) {
        String ancienPattern = LexerGenerator.remplacerCategoriesParMembre(oldPattern);
        String nouveauPattern = LexerGenerator.remplacerCategoriesParMembre(newPattern);
        var ast = dict.remove(ancienPattern);
        dict.put(nouveauPattern, ast);
        int idx = ordre.indexOf(ancienPattern);
        ordre.remove(ancienPattern);
        ordre.add(idx, nouveauPattern);
    }
}

























