package interpreteur.as.experimental;

import interpreteur.as.ASAst;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASScope;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.ASNul;
import interpreteur.as.lang.datatype.structure.ASStructure;
import interpreteur.as.lang.managers.ASScopeManager;
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
                new Ast<>(
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

                                        setType:
                                        if (idxVariante != 1) {
                                            if (p.get(2) instanceof ASTypeExpr _type) {
                                                type = _type;
                                                break setType;
                                            }

                                            if (p.get(2) instanceof Var varType) {
                                                var variable = ASScope.getCurrentScope().getVariable(varType.getNom());
                                                if (variable != null && variable.getValeurApresGetter() instanceof ASStructure structure) {
                                                    type = new ASTypeExpr(structure.getNom());
                                                    break setType;
                                                }
                                            }
                                            throw new ASErreur.ErreurType("Le symbole ':' doit \u00EAtre suivi d'un type valide.");
                                        }

                                        if (idxVariante < 2) {
                                            valParDefaut = (Expression<?>) p.get(idxVariante == 1 ? 2 : 4);
                                        }

                                        return new Argument(var, valParDefaut, type);
                                    }
                                })
                ) {
                    @Override
                    public CreerFonction apply(List<Object> p, Integer idxVariante) {
                        Argument[] params = new Argument[]{};

                        ASTypeExpr typeRetour = p.get(p.size() - 1) instanceof ASTypeExpr type ? type : new ASTypeExpr("tout");

                        // if (p.get(p.size() - 1) == null && p.get(3) instanceof ASTypeExpr type) {
                        //     typeRetour = type;
                        //     return new CreerFonction((Var) p.get(1), params, typeRetour, executeurInstance);
                        // }

                        if (idxVariante < 2) {
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

        remplacerProgramme("STRUCTURE NOM_VARIABLE", p -> {
            pushAstFrame(AstFrameKind.STRUCTURE);
            return new DefinirStructure(new Var(((Token) p.get(1)).getValeur()), executeurInstance);
        });

        remplacerProgramme("FIN STRUCTURE", p -> new FinStructure());
    }

    @Override
    protected void ajouterExpressions() {
        super.ajouterExpressions();

        ajouterExpression("expression POINT expression", new Ast<GetAttr>(2) {
            @Override
            public GetAttr apply(List<Object> p, Integer idxVariante) {
                return new GetAttr((Expression<?>) p.get(0), (Var) p.get(2));
            }
        });

        ajouterExpression("expression BRACES_OUV #expression VIRGULE BRACES_FERM~" +
                "expression BRACES_OUV #expression BRACES_FERM~" +
                "expression BRACES_OUV BRACES_FERM", new Ast<>(3) {
            @Override
            public CreerStructureInstance apply(List<Object> p, Integer variante) {
                var varStructure = (Expression<?>) p.get(0);

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
                } else if (contenu instanceof Var var) {
                    argsStructure = new ArgumentStructure[]{new ArgumentStructure(var, null)};
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
            }
        });



    }

    private void ajouterProgrammesStructure() {
        ajouterProgramme("CONSTANTE expression~"
                        + "CONSTANTE expression DEUX_POINTS expression~"
                        + "CONSTANTE expression {assignements} expression~"
                        + "CONSTANTE expression DEUX_POINTS expression {assignements} expression~"
                        + "VAR expression~"
                        + "VAR expression {assignements} expression~"
                        + "VAR expression DEUX_POINTS expression {assignements} expression~"
                        + "VAR expression DEUX_POINTS expression~"
                        + "expression {assignements} expression",
                (p, variante) -> {

                    /*TODO erreur si c 'est pas une Var qui est passé comme expression à gauche de l' assignement */


                    int idxValeur;
                    int idxAssignement;


                    // si le premier mot n'est ni "const" ni "var"
                    if (variante == 8) {
                        throw new ASErreur.ErreurSyntaxe("Seul les d\u00E9clarations de constantes ou de variables " +
                                "sont autoris\u00E9es dans une structure.");
                    }

                    // déclaration sous la forme "const x"
                    if (variante == 0) {
                        return new Declarer((Expression<?>) p.get(1), null, null, true);
                    }

                    // déclaration sous la forme "var x"
                    if (variante == 4) {
                        return new Declarer((Expression<?>) p.get(1), new ValeurConstante(new ASNul()), null, false);
                    }

                    // si le premier mot est "const"
                    boolean estConst = variante < 4;

                    // le type de la variable déclarer (null signifie qu'il n'est pas mentionné dans la déclaration)
                    ASTypeExpr type = null;

                    /* Déclaration sous une des formes:
                     * 1. const x: type = valeur
                     * 4. var x:type = valeur
                     * 5. var x:type
                     */

                    setType:
                    if (variante == 1 || variante == 3 || variante == 6 || variante == 7) {
                        // si le type précisé n'est pas un type
                        if (p.get(3) instanceof ASTypeExpr _type) {
                            type = _type;
                            break setType;
                        }

                        if (p.get(3) instanceof Var var) {
                            var current = ASScope.popCurrentScope();
                            var variable = ASScope.getCurrentScope().getVariable(var.getNom());
                            ASScope.pushCurrentScope(current);
                            if (variable != null) {
                                // manière complexe de checker si le nom du type est le même nom que la structure dans laquelle il est déclaré
                                if (variable.getNom().equals(ASScopeManager.getScopeName(executeurInstance.obtenirCoordRunTime().getScope()))) {
                                    type = new ASTypeExpr(variable.getNom());
                                    break setType;
                                }
                                if (variable.getValeurApresGetter() instanceof ASStructure structure) {
                                    type = new ASTypeExpr(structure.getNom());
                                    break setType;
                                }
                            }
                        }

                        throw new ASErreur.ErreurType("Dans une d\u00E9claration de " +
                                (estConst ? "constante" : "variable") +
                                ", les deux points doivent \u00EAtre suivi d'un type valide");
                    }

                    if (variante == 7 || variante == 1) {
                        return new Declarer((Expression<?>) p.get(1), null, type, variante == 1);
                    }

                    // si la précision du type est présente
                    if (variante == 3 || variante == 6) {
                        idxValeur = 5;
                        idxAssignement = 4;
                    }
                    // si la précision du type n'est pas présente
                    else {
                        idxValeur = 3;
                        idxAssignement = 2;
                    }

                    // si on tente de déclarer une constante avec autre chose que = (ex: +=, *=, -=, etc.)
                    String nomAssignement = ((Token) p.get(idxAssignement)).getNom();
                    if (!nomAssignement.equals("ASSIGNEMENT") && !(nomAssignement.equals("ASSIGNEMENT_FLECHE"))) {
                        if (estConst)
                            throw new ASErreur.ErreurAssignement("Impossible de modifier la valeur d'une constante");
                        else
                            throw new ASErreur.ErreurAssignement("Impossible de modifier la valeur d'une variable durant sa d\u00E9claration");
                    }

                    // si la valeur de l'expression est une énumération d'éléments ex: 3, "salut", 4
                    // on forme une liste avec la suite d'éléments
                    if (p.get(idxValeur) instanceof CreerListe.Enumeration enumeration)
                        p.set(idxValeur, enumeration.buildCreerListe());

                    // on retourne l'objet Declarer
                    return new Declarer((Expression<?>) p.get(1), (Expression<?>) p.get(idxValeur), type, estConst, true);
                });
/*

        ajouterProgramme("CONSTANTE expression~"
                        + "CONSTANTE expression DEUX_POINTS expression~"
                        + "VAR expression~"
                        + "VAR expression DEUX_POINTS expression",
                (p, variante) -> {
                    */
        /*
         * TODO erreur si c'est pas une Var qui est passé comme expression à gauche du assignment
         *//*

                    // si le premier mot est "const"
                    boolean estConst = variante < 2;

                    // le type de la variable déclarer (null signifie qu'il n'est pas mentionné dans la déclaration)
                    ASTypeExpr type = null;

                    */
        /*
         * Déclaration sous une des formes:
         * 1. const x: type = valeur
         * 4. var x: type = valeur
         * 5. var x: type
         *//*

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
*/


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

























