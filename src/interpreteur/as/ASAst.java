package interpreteur.as;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.erreurs.ASErreur.ErreurAssignement;
import interpreteur.as.erreurs.ASErreur.ErreurInputOutput;
import interpreteur.as.erreurs.ASErreur.ErreurSyntaxe;
import interpreteur.as.erreurs.ASErreur.ErreurType;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.as.lang.datatype.*;
import interpreteur.ast.Ast;
import interpreteur.ast.buildingBlocs.Expression;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.*;
import interpreteur.ast.buildingBlocs.programmes.*;
import interpreteur.executeur.Executeur;
import interpreteur.generateurs.ast.AstFrameKind;
import interpreteur.generateurs.ast.AstGenerator;
import interpreteur.tokens.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Les explications vont être rajouté quand j'aurai la motivation de les écrire XD
 *
 * @author Mathis Laroche
 */


public class ASAst extends AstGenerator<AstFrameKind> {
    protected final Executeur executeurInstance;

    public ASAst(Executeur executeurInstance) {
        reset();
        defineAstFrame(AstFrameKind.DEFAULT);
        ajouterProgrammes();
        ajouterExpressions();
        pushAstFrame(AstFrameKind.DEFAULT);
        this.executeurInstance = executeurInstance;
    }


    protected void ajouterProgrammes() {
        ajouterProgramme("", (p) -> null);

        ajouterProgramme("UTILISER expression~"
                        + "UTILISER expression BRACES_OUV MUL BRACES_FERM~"
                        + "UTILISER expression BRACES_OUV expression BRACES_FERM~"
                        + "UTILISER expression NOM_VARIABLE BRACES_OUV MUL BRACES_FERM~"
                        + "UTILISER expression NOM_VARIABLE BRACES_OUV expression BRACES_FERM",
                (p, variante) -> {
                    if (p.get(1) instanceof ValeurConstante valeurConstante && valeurConstante.eval() instanceof ASTexte texte) {
                        String msg = texte.getValue();
                        if (msg.equalsIgnoreCase("experimental")) {
                            //executeurInstance.setAst(new ASAstExperimental(executeurInstance));
                            return new Utiliser(new Var("experimental"), executeurInstance);
                        } else {
                            throw new ErreurSyntaxe("Les noms de modules ne doivent pas \u00EAtre \u00E9crits avec des \" \" ou des ' '");
                        }
                    }

                    String nomPrefix = "";
                    if (variante == 3 || variante == 4) {
                        nomPrefix = ((Token) p.get(2)).getValeur();
                        if (!nomPrefix.endsWith(".")) {
                            throw new ASErreur.ErreurSyntaxe("Le pr\u00E9fix du module doit finir par '.'");
                        }
                        nomPrefix = nomPrefix.substring(0, nomPrefix.length() - 1);
                    }

                    if (variante == 1 || variante == 3) {
                        return new Utiliser((Var) p.get(1), executeurInstance, nomPrefix);

                    } else if (variante == 2 || variante == 4) {
                        int idxEnumeration = variante == 2 ? 3 : 4;
                        Var[] sous_modules;
                        if (p.get(idxEnumeration) instanceof CreerListe.Enumeration enumeration) {
                            sous_modules = enumeration.getExprs().toArray(Var[]::new);
                        } else {
                            sous_modules = new Var[]{(Var) p.get(idxEnumeration)};
                        }
                        return new Utiliser((Var) p.get(1), sous_modules, executeurInstance, nomPrefix);
                    }

                    return new Utiliser((Var) p.get(1), executeurInstance);
                });
        /*
        commentaire
        ajouterProgramme("AFFICHER expression", new Ast<Afficher>() {
            @Override
            public Afficher apply(List<Object> p) {
                return new Afficher((Expression<?>) p.get(1));
            }
        });
         */

        ajouterProgramme("LIRE expression~"
                        + "LIRE expression DANS expression~"
                        + "LIRE expression VIRGULE expression~"
                        + "LIRE expression DANS expression VIRGULE expression",
                (p, variante) -> {
                    Expression<?> message = null, fonction = null;

                    int idxVar = 1;
                    // si une fonction de conversion est appliquée
                    if (variante == 1 || variante == 3) {
                        fonction = (Expression<?>) p.get(1);
                        idxVar = 3;
                    }
                    // s'il y a un message à afficher
                    if (variante == 2 || variante == 3) message = (Expression<?>) p.get(p.size() - 1);

                    if (!(p.get(idxVar) instanceof Var var)) {
                        throw new ErreurInputOutput("Une variable est attendue apr\u00E8s la commande 'lire', mais '" +
                                p.get(idxVar).getClass().getSimpleName() + "' a \u00E9t\u00E9 trouv\u00E9.");
                    }

                    return new Lire(var, message, fonction, executeurInstance);
                });
        /*
        ajouterProgramme("ATTENDRE expression", new Ast<Attendre>() {
            @Override
            public Attendre apply(List<Object> p) {
                return new Attendre((Expression<?>) p.get(1));
            }
        });
         */

        ajouterProgramme("CONSTANTE expression {assignements} expression~"
                        + "CONSTANTE expression DEUX_POINTS expression {assignements} expression~"
                        + "VAR expression~"
                        + "VAR expression {assignements} expression~"
                        + "VAR expression DEUX_POINTS expression {assignements} expression~"
                        + "VAR expression DEUX_POINTS expression~"
                        + "expression {assignements} expression",
                (p, variante) -> {
                    /*
                     * TODO erreur si c'est pas une Var qui est passé comme expression à gauche de l'assignement
                     */

                    int idxValeur;
                    int idxAssignement;

                    BinOp.Operation op = null;

                    // si le premier mot n'est ni "const" ni "var" et qu'un type est précisé
                    if (variante == 7) {
                        throw new ErreurType("Il est impossible de pr\u00E9ciser le type d'une variable " +
                                "ailleurs que dans sa d\u00E9claration");
                    }
                    // si le premier mot n'est ni "const" ni "var"
                    if (variante == 6) {
                        // si on tente d'assigner avec un opérateur spécial (ex: +=, *=, -=, etc.)
                        String nomAssignement = ((Token) p.get(1)).getNom();
                        if (!nomAssignement.equals("ASSIGNEMENT") && !(nomAssignement.equals("ASSIGNEMENT_FLECHE"))) {
                            // only keep the first part of the name (ex: PLUS_ASSIGNEMENT becomes PLUS)
                            op = BinOp.Operation.valueOf(nomAssignement.substring(
                                    0, nomAssignement.lastIndexOf("_"))
                            );
                        }

                        // si la valeur de l'expression est une énumération d'éléments ex: var = 3, "salut", 4
                        // on forme une liste avec la suite d'éléments
                        if (p.get(2) instanceof CreerListe.Enumeration enumeration)
                            p.set(2, enumeration.buildCreerListe());
                        return new Assigner((Expression<?>) p.get(0), (Expression<?>) p.get(2), op);
                    }

                    // déclaration sous la forme "var x"
                    if (variante == 2) {
                        return new Declarer((Expression<?>) p.get(1), new ValeurConstante(new ASNul()), null, false);
                    }

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
                    if (variante == 1 || variante == 4 || variante == 5) {
                        // si le type précisé n'est pas un type
                        if (!(p.get(3) instanceof ASTypeExpr _type))
                            throw new ErreurType("Dans une d\u00E9claration de " +
                                    (estConst ? "constante" : "variable") +
                                    ", les deux points doivent \u00EAtre suivi d'un type valide");
                        type = _type;
                    }

                    if (variante == 5) {
                        return new Declarer((Expression<?>) p.get(1), null, type, false);
                    }

                    // si la précision du type est présente
                    if (variante == 1 || variante == 4) {
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
                            throw new ErreurAssignement("Impossible de modifier la valeur d'une constante");
                        else
                            throw new ErreurAssignement("Impossible de modifier la valeur d'une variable durant sa d\u00E9claration");
                    }

                    // si la valeur de l'expression est une énumération d'éléments ex: 3, "salut", 4
                    // on forme une liste avec la suite d'éléments
                    if (p.get(idxValeur) instanceof CreerListe.Enumeration enumeration)
                        p.set(idxValeur, enumeration.buildCreerListe());

                    // on retourne l'objet Declarer
                    return new Declarer((Expression<?>) p.get(1), (Expression<?>) p.get(idxValeur), type, estConst);
                });


        /* Deprecated
        ajouterProgramme("{methode_moteur} expression~"
                         + "{methode_moteur}",
                (p, variante) -> {
                    String nom = ((Token) p.get(0)).obtenirNom();

                    return new MethodeMoteur(nom, variante == 0 ? (Expression<?>) p.get(1) : null);
                });
        */

        ajouterProgramme("STRUCTURE NOM_VARIABLE", p -> new CreerNamespace(((Token) p.get(1)).getValeur()));

        ajouterProgramme("FIN STRUCTURE", p -> new FinNamespace());

        //<-----------------------------------Les getters----------------------------------------->//
        ajouterProgramme("GET NOM_VARIABLE~" +
                        "GET NOM_VARIABLE FLECHE expression",
                (p, variante) -> {
                    ASTypeExpr type = new ASTypeExpr("tout");
                    if (variante == 1) {
                        if (!(p.get(3) instanceof ASTypeExpr _type)) {
                            throw new ErreurType("'" + p.get(3) + "' n'est pas un type valide");
                        }
                        type = _type;
                    }
                    return new CreerGetter(new Var(((Token) p.get(1)).getValeur()), type, executeurInstance);
                });

        ajouterProgramme("FIN GET", p -> new FinGet(executeurInstance));

        //<-----------------------------------Les setters----------------------------------------->//
        ajouterProgramme("SET NOM_VARIABLE PARENT_OUV NOM_VARIABLE PARENT_FERM~" +
                        "SET NOM_VARIABLE PARENT_OUV NOM_VARIABLE DEUX_POINTS expression PARENT_FERM",
                (p, variante) -> {
                    ASTypeExpr type = new ASTypeExpr("tout");
                    if (variante == 1) {
                        if (!(p.get(5) instanceof ASTypeExpr _type)) {
                            throw new ErreurType("'" + p.get(5) + "' n'est pas un type valide");
                        }
                        type = _type;
                    }
                    return new CreerSetter(
                            new Var(((Token) p.get(1)).getValeur()),
                            new Var(((Token) p.get(3)).getValeur()),
                            type,
                            executeurInstance
                    );
                });

        ajouterProgramme("FIN SET", p -> new FinSet(executeurInstance));

        //<-----------------------------------Les fonctions----------------------------------------->//

        ajouterProgramme("FONCTION expression PARENT_OUV expression PARENT_FERM FLECHE expression~" +
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
                                            throw new ErreurSyntaxe("Une d\u00E9claration de fonction doit commencer par une variable, pas par " + p.get(0));
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
                                                throw new ErreurType("Le symbole ':' doit \u00EAtre suivi d'un type valide ('" + nom + "' n'est pas un type valide)");
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

        ajouterProgramme("RETOURNER~" +
                        "RETOURNER expression",
                (p, variante) -> {
                    if (variante == 1 && p.get(1) instanceof CreerListe.Enumeration enumeration)
                        p.set(1, enumeration.buildCreerListe());
                    return new Retourner(variante == 1 ? (Expression<?>) p.get(1) : new ValeurConstante(new ASNul()));
                });


        ajouterProgramme("FIN FONCTION", p -> new FinFonction(executeurInstance));


        //<-----------------------------------Les blocs de code------------------------------------->
        ajouterProgramme(
                "SI expression~" +
                        "SI expression ALORS",
                p -> new Si((Expression<?>) p.get(1), executeurInstance)
        );

        ajouterProgramme(
                "SINON SI expression~" +
                        "SINON SI expression ALORS",
                p -> new SinonSi((Expression<?>) p.get(2), executeurInstance)
        );

        ajouterProgramme("SINON", p -> new Sinon(executeurInstance));

        ajouterProgramme("FIN SI", p -> new FinSi(executeurInstance));

        ajouterProgramme("FAIRE", p -> new BoucleFaire(executeurInstance));

        ajouterProgramme("TANT_QUE expression",
                p -> new BoucleTantQue((Expression<?>) p.get(1), executeurInstance)
        );

        ajouterProgramme("REPETER expression",
                p -> new BoucleRepeter((Expression<?>) p.get(1), executeurInstance));

        ajouterProgramme("POUR expression DANS expression~"
                        + "POUR VAR expression DANS expression~"
                        + "POUR CONSTANTE expression DANS expression",
                (p, variante) -> {
                    // boucle pour sans déclaration
                    if (variante == 0) {
                        return new BouclePour((Var) p.get(1), (Expression<?>) p.get(3), executeurInstance);
                    } else {
                        boolean estConst = variante == 2;
                        return new BouclePour(
                                (Var) p.get(2),
                                (Expression<?>) p.get(4),
                                executeurInstance
                        ).declarerVar(estConst, null);
                    }
                });

        ajouterProgramme("SORTIR", p -> new Boucle.Sortir(executeurInstance));

        ajouterProgramme("CONTINUER", p -> new Boucle.Continuer(executeurInstance));

        ajouterProgramme("FIN POUR~"
                        + "FIN TANT_QUE~"
                        + "FIN REPETER",
                p -> new FinBoucle(((Token) p.get(1)).getValeur(), executeurInstance)
        );

        ajouterProgramme("expression",
                p -> new Programme() {
                    @Override
                    public Object execute() {
                        // if the expression is not a lonely variable, just eval and return null
                        if (!(p.get(0) instanceof Var var)) {
                            ((Expression<?>) p.get(0)).eval();
                            return null;
                        }
                        try {
                            new AppelFonc(var, new CreerListe()).eval();
                        } catch (ASErreur.ErreurTypePasAppelable err) {
                            var.eval();
                        }
                        return null;
                    }

                    @Override
                    public String toString() {
                        return p.get(0).toString();
                    }
                }
        );
    }


    protected void ajouterExpressions() {

        ajouterExpression("NOM_VARIABLE", p -> new Var(((Token) p.get(0)).getValeur()));

        ajouterExpression("{nom_type_de_donnees}",
                p -> new ASTypeExpr(((Token) p.get(0)).getValeur())
        );

        ajouterExpression("{type_de_donnees}",
                p -> {
                    Token valeur = (Token) p.get(0);
                    String nom = valeur.getNom();
                    return new ValeurConstante(switch (nom) {
                        case "ENTIER" -> new ASEntier(valeur);
                        case "DECIMAL" -> new ASDecimal(valeur);
                        case "TEXTE" -> new ASTexte(valeur);
                        case "BOOLEEN" -> new ASBooleen(valeur);
                        case "NUL" -> new ASNul();
                        default -> throw new ErreurType("Type de donnee invalide");
                    });
                });

        //call fonction
        ajouterExpression("expression PARENT_OUV #expression PARENT_FERM~"
                        + "expression PARENT_OUV PARENT_FERM",
                new Ast<AppelFonc>() {
                    @Override
                    public AppelFonc apply(List<Object> p, Integer idxVariante) {
                        if (p.size() == 3) {
                            //Expression<?> nom = p.get(0) instanceof Type type
                            //        ? new Var(type.nom())
                            //        : (Expression<?>) p.get(0);
                            return new AppelFonc((Expression<?>) p.get(0), new CreerListe());
                        }
                        Hashtable<String, Ast<? extends Expression<?>>> astParams = new Hashtable<>();

                        //astParams.put("expression DEUX_POINTS expression", new Ast<Argument>(8){
                        //    @Override
                        //    public Argument apply(List<Object> p) {
                        //        assert p.get(0) instanceof Var : "gauche assignement doit être Var (source: appelFonction dans ASAst)";
                        //
                        //        return new Argument((Var) p.get(0), (Expression<?>) p.get(2), null);
                        //    }
                        //});
                        Expression<?> contenu = evalOneExpr(new ArrayList<>(p.subList(2, p.size() - 1)), astParams);

                        CreerListe args = contenu instanceof CreerListe.Enumeration enumeration ?
                                enumeration.buildCreerListe() :
                                new CreerListe(contenu);


                        //final Expression<?> nom;
                        //if (p.get(0) instanceof Type type) {
                        //    nom = new Var(type.nom());
                        //} else {
                        //    nom = (Expression<?>) p.get(0);
                        //}

                        return new AppelFonc((Expression<?>) p.get(0), args);
                    }
                });

        ajouterExpression("PARENT_OUV #expression PARENT_FERM~"
                        + "PARENT_OUV expression PARENT_FERM~"
                        + "PARENT_OUV PARENT_FERM",
                (p, variante) -> {
                    if (variante == 2) return new Expression.ExpressionVide();
                    return evalOneExpr(new ArrayList<>(p.subList(1, p.size() - 1)), null);
                });

        ajouterExpression("BRACES_OUV #expression TROIS_POINTS #expression BRACES_FERM~"
                        + "BRACES_OUV #expression TROIS_POINTS #expression BOND #expression BRACES_FERM~"
                        + "CROCHET_OUV #expression TROIS_POINTS #expression BOND #expression CROCHET_FERM~"
                        + "CROCHET_OUV #expression TROIS_POINTS #exrpession CROCHET_FERM",
                /*
                 * [1...10] -> {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
                 * ["a"..."g"] -> {"a", "b", "c", "d", "e", "f", "g"}
                 * ["A"..."G"] -> {"A", "B", "C", "D", "E", "F", "G"}
                 */
                p -> {
                    int idxTroisPoints = p.indexOf(p.stream()
                            .filter(exp -> exp instanceof Token token && token.getNom().equals("TROIS_POINTS"))
                            .findFirst()
                            .orElseThrow());

                    Token bondToken = (Token) p.stream()
                            .filter(exp -> exp instanceof Token token && token.getNom().equals("BOND"))
                            .findFirst()
                            .orElse(null);

                    System.out.println(p);

                    Expression<?> debut = evalOneExpr(new ArrayList<>(p.subList(1, idxTroisPoints)), null);
                    Expression<?> fin, bond = null;

                    // pas de bond, forme {debut...fin}
                    if (bondToken == null) {
                        fin = evalOneExpr(new ArrayList<>(p.subList(idxTroisPoints + 1, p.size() - 1)), null);
                    } else {
                        int idxBond = p.indexOf(bondToken);
                        fin = evalOneExpr(new ArrayList<>(p.subList(idxTroisPoints + 1, idxBond)), null);
                        bond = evalOneExpr(new ArrayList<>(p.subList(idxBond + 1, p.size() - 1)), null);
                    }

                    return new Suite(debut, fin, bond);
                });


        ajouterExpression("expression CROCHET_OUV DEUX_POINTS CROCHET_FERM~"
                        + "expression CROCHET_OUV #expression DEUX_POINTS #expression CROCHET_FERM~"
                        + "expression CROCHET_OUV #expression DEUX_POINTS CROCHET_FERM~"
                        + "expression CROCHET_OUV DEUX_POINTS #expression CROCHET_FERM~"
                        + "expression CROCHET_OUV #expression CROCHET_FERM",
                (p, variante) -> {
                    boolean hasDeuxPoints = variante < 4;

                    // pas de deux points, forme val[idxOrKey]
                    if (!hasDeuxPoints) {
                        Expression<?> idx = evalOneExpr(new ArrayList<>(p.subList(2, p.size() - 1)), null);
                        return new CreerListe.SousSection.IndexSection((Expression<?>) p.get(0), idx);
                    }
                    // deux points, forme val[debut:fin] ou val[:fin] ou val[debut:] ou val[:]
                    else {
                        Expression<?> debut = null, fin = null;
                        int idxDeuxPoints = p.indexOf(p.stream()
                                .filter(exp -> exp instanceof Token token && token.getNom().equals("DEUX_POINTS"))
                                .findFirst()
                                .orElse(null)
                        );
                        // si debut dans sous section
                        if (idxDeuxPoints > 2) {
                            debut = evalOneExpr(new ArrayList<>(p.subList(2, idxDeuxPoints)), null);
                        }
                        // si fin dans sous section
                        if (idxDeuxPoints < p.size() - 2) {
                            fin = evalOneExpr(new ArrayList<>(p.subList(idxDeuxPoints + 1, p.size() - 1)), null);
                        }
                        return new CreerListe.SousSection.CreerSousSection((Expression<?>) p.get(0), debut, fin);
                    }
                });

        ajouterExpression("BRACES_OUV BRACES_FERM~"
                        + "BRACES_OUV #expression BRACES_FERM~"
                        + "CROCHET_OUV CROCHET_FERM~"
                        + "!expression CROCHET_OUV CROCHET_FERM~"
                        + "!expression CROCHET_OUV #expression CROCHET_FERM",
                (p, variante) -> {
                    if (variante == 0) {
                        return new CreerDict();
                    } else if (variante == 2 || variante == 3) {
                        return new CreerListe();
                    }
                    Expression<?> contenu = evalOneExpr(new ArrayList<>(p.subList(1, p.size() - 1)), null);
                    if (contenu instanceof CreerListe.Enumeration enumeration)
                        return enumeration.buildCreerListe();
                    return new CreerListe(contenu);
                });

        ajouterExpression("expression PLUS PLUS~"
                        + "expression MOINS MOINS",
                (p, variante) -> {
                    final byte signe = (byte) (variante == 1 ? -1 : 1);
                    return new Incrementer((Expression<?>) p.get(0), signe);
                });


        ajouterExpression("!expression MOINS expression",
                p -> new UnaryOp((Expression<?>) p.get(1), UnaryOp.Operation.NEGATION));

        ajouterExpression("!expression PLUS expression",
                p -> new UnaryOp((Expression<?>) p.get(1), UnaryOp.Operation.PLUS));


        ajouterExpression("expression MOD expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.MOD, (Expression<?>) p.get(2)));


        ajouterExpression("expression POW expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.POW, (Expression<?>) p.get(2)));


        ajouterExpression("expression MUL expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.MUL, (Expression<?>) p.get(2)));


        ajouterExpression("expression DIV expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.DIV, (Expression<?>) p.get(2)));


        ajouterExpression("expression DIV_ENTIERE expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.DIV_ENTIERE, (Expression<?>) p.get(2)));


        ajouterExpression("expression PLUS expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.PLUS, (Expression<?>) p.get(2)));

        ajouterExpression("expression MOINS expression",
                p -> new BinOp((Expression<?>) p.get(0), BinOp.Operation.MOINS, (Expression<?>) p.get(2)));

        ajouterExpression("expression PIPE expression",
                p -> {
                    if (!(p.get(0) instanceof ASTypeExpr typeG && p.get(2) instanceof ASTypeExpr typeD)) {
                        return new BinOp((Expression<?>) p.get(0), BinOp.Operation.PIPE, (Expression<?>) p.get(2));
                    }
                    typeG.union(typeD);
                    return typeG;
                });

        ajouterExpression("expression DANS expression~" +
                        "expression PAS DANS expression",
                (p, variante) -> variante == 0 ?
                        new BinComp((Expression<?>) p.get(0), BinComp.Comparateur.DANS, (Expression<?>) p.get(2))
                        :
                        new BinComp((Expression<?>) p.get(0), BinComp.Comparateur.PAS_DANS, (Expression<?>) p.get(3)));


        ajouterExpression("expression {comparaison} expression",
                p -> new BinComp(
                        (Expression<?>) p.get(0),
                        BinComp.Comparateur.valueOf(((Token) p.get(1)).getNom()),
                        (Expression<?>) p.get(2))
        );


        ajouterExpression("expression {porte_logique} expression",
                p -> new BoolOp(
                        (Expression<?>) p.get(0),
                        BoolOp.Operateur.valueOf(((Token) p.get(1)).getNom()),
                        (Expression<?>) p.get(2)));

        ajouterExpression("PAS expression",
                p -> new BoolOp((Expression<?>) p.get(1), BoolOp.Operateur.PAS, null));

        ajouterExpression("expression SI expression SINON expression",
                p -> new Ternary((Expression<?>) p.get(2), (Expression<?>) p.get(0), (Expression<?>) p.get(4)));

        ajouterExpression("expression DEUX_POINTS expression",
                p -> new Paire((Expression<?>) p.get(0), (Expression<?>) p.get(2)));

        ajouterExpression("expression VIRGULE expression~",
                p -> {
                    if (p.size() == 2) {
                        if (p.get(0) instanceof CreerListe.Enumeration enumeration)
                            return enumeration;
                        else
                            return new CreerListe.Enumeration((Expression<?>) p.get(0));
                    }

                    Expression<?> valeur = (Expression<?>) p.get(2);
                    if (p.get(2) instanceof CreerListe.Enumeration enumeration) {
                        valeur = enumeration.buildCreerListe();
                    }
                    if (p.get(0) instanceof CreerListe.Enumeration enumeration) {
                        enumeration.add(valeur);
                        return enumeration;
                    }

                    return new CreerListe.Enumeration((Expression<?>) p.get(0), valeur);
                });

        ajouterExpression("expression expression",
                p -> {
                    Expression<?> contenu;
                    CreerListe args;
                    if (p.size() == 2 && !(p.get(1) instanceof Expression.ExpressionVide)) {
                        contenu = (Expression<?>) p.get(1);

                        args = contenu instanceof CreerListe.Enumeration enumeration ?
                                enumeration.buildCreerListe() :
                                new CreerListe(contenu);
                    } else {
                        args = new CreerListe();
                    }

                    return new AppelFonc((Expression<?>) p.get(0), args);
                });
    }
}

























