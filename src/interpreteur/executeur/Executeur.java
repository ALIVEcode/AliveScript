package interpreteur.executeur;

import interpreteur.as.ASAst;
import interpreteur.as.ASLexer;
import interpreteur.as.lang.ASFonctionInterface;
import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.datatype.ASFonction;
import interpreteur.as.lang.datatype.ASNul;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.managers.ASFonctionManager;
import interpreteur.as.lang.ASScope;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.erreurs.ASErreur.*;
import interpreteur.as.modules.core.ASModuleManager;
import interpreteur.ast.buildingBlocs.Programme;
import interpreteur.ast.buildingBlocs.expressions.AppelFonc;
import interpreteur.ast.buildingBlocs.programmes.Declarer;
import interpreteur.converter.ASObjetConverter;
import interpreteur.data_manager.Data;
import interpreteur.data_manager.DataVoiture;
import interpreteur.tokens.Token;
import io.github.cdimascio.dotenv.Dotenv;
import language.Language;
import language.Translator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Pair;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



/*

afficher 3

attendre 2

var = 2

si var == 2
	avancer
sinon
	afficher faux
fin si

afficher "fin"

*/


/**
 * Cette classe
 *
 * @author Mathis Laroche
 */

public class Executeur {

    private final static int MAX_DATA_BEFORE_SEND;
    // coordonne ou commencer tous les programmes
    final private static Coordonnee debutCoord = new Coordonnee("<0>main");

    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("./.env")
                .load();
        MAX_DATA_BEFORE_SEND = Integer.parseInt(dotenv.get("MAX_DATA_BEFORE_SEND"));
    }

    // lexer et parser
    private final ASLexer lexer;
    //------------------------ compilation -----------------------------//
    private final Hashtable<String, Hashtable<String, Programme>> coordCompileDict = new Hashtable<>();
    private final ArrayList<Coordonnee> coordCompileTime = new ArrayList<>();
    // Coordonnee utilisee lors de l'execution pour savoir quelle ligne executer
    private final Coordonnee coordRunTime = new Coordonnee(debutCoord.toString());
    // modules
    private final ASModuleManager asModuleManager;

    // data explaining the actions to do to the com.server
    private final ArrayList<Data> datas = new ArrayList<>();
    // data stack used when the program asks the site for information
    private final Stack<Object> dataResponse = new Stack<>();
    // ast
    private final ASAst ast;
    private final Translator translator;
    private final ExecuteurState executeurState;
    //debug mode
    public boolean debug = false;
    private JSONObject context = null;
    private String[] anciennesLignes = null;
    // failsafe
    private boolean compilationActive = false;
    private boolean executionActive = false;
    private boolean canExecute = false;

    public Executeur(Language language) {
        translator = new Translator(language);
        lexer = new ASLexer(language.getASLexerPath()); // utiliser translator pour passer bon yaml
        asModuleManager = new ASModuleManager(this);
        ast = new ASAst(this);
        this.executeurState = new ExecuteurState(this);
    }

    @Deprecated(since = "now")
    public Executeur() {
        this(Language.FR);
    }

    public static void printCompiledCode(String code) {
        int nbTab = 0;
        code = code.replaceAll(", ", ",");

        boolean inString = false;
        boolean skipNext = false;
        for (String chr : code.split("")) {
            if (inString) {
                System.out.print(chr);
                if (chr.equals("\\")) {
                    skipNext = true;
                    continue;
                }
                inString = !chr.equals("'") || skipNext;
                skipNext = false;
                continue;
            }
            switch (chr) {
                case "{", "[" -> {
                    nbTab++;
                    System.out.print(" " + chr + "\n" + "\t".repeat(nbTab));
                }
                case "}", "]" -> {
                    nbTab--;
                    System.out.print("\n" + "\t".repeat(nbTab) + chr);
                }
                case "," -> System.out.print(chr + "\n" + "\t".repeat(nbTab));
                case "'" -> {
                    System.out.print(chr);
                    inString = true;
                }
                default -> System.out.print(chr);
            }
        }
        System.out.println();
    }

    public Translator getTranslator() {
        return translator;
    }

    /**
     * @return le lexer utilise par l'interpreteur (voir ASLexer)
     */
    public ASLexer getLexer() {
        return lexer;
    }


    // methode utilis??e ?? chaque fois qu'une info doit ??tre affich?? par le langage
    public void ecrire(String texte) {
        if (debug) System.out.println(texte);
    }

    public void printCompileDict() {
        int nbTab = 0;
        for (String scope : coordCompileDict.keySet()) {
            System.out.print("\n" + scope + ":\n");
            String[] ordreCoord = coordCompileDict.get(scope).keySet()
                    .stream()
                    .sorted(Comparator.comparingInt(coord -> coordCompileDict.get(scope).get(coord).getNumLigne()))
                    .toArray(String[]::new);

            for (String coord : ordreCoord) {
                String prog = coordCompileDict.get(scope).getOrDefault(coord,
                        new Programme() {
                            @Override
                            public Object execute() {
                                return null;
                            }
                        }
                ).toString();
                System.out.print("\t".repeat(nbTab) + coord + "=");
                printCompiledCode(prog);
            }
        }
    }

    public void addData(Data data) {
        datas.add(data);
    }

    public Stack<Object> getDataResponse() {
        return this.dataResponse;
    }

    public Object getDataResponseOrAsk(Data.Id id, Object... additionalParams) {
        if (this.dataResponse.isEmpty()) {
            Data dataToGet = new Data(id);
            for (var param : additionalParams)
                dataToGet.addParam(param);
            throw new AskForDataResponse(dataToGet);
        } else
            return this.dataResponse.pop();
    }

    public JSONObject getContext() {
        if (context == null)
            throw new ASErreur.ErreurContexteAbsent("Il n'y a pas de contexte");
        return context;
    }

    public void setContext(JSONObject context) {
        if (this.context != null) {
            throw new IllegalArgumentException("aaaaa");
        }
        this.context = context;
    }

    public Object pushDataResponse(Object item) {
        return this.dataResponse.push(item);
    }

    /**
     * @return les dernieres lignes a avoir ete compile sous la forme d'une array de String
     */
    public String[] getLignes() {
        return anciennesLignes;
    }

    /**
     * @param coord <br><li>la coordonne d'une certaine ligne de code</li>
     * @return la position de la la ligne de code dans le code
     */
    public Integer getLineFromCoord(Coordonnee coord) {
        return coordCompileDict.get(coord.getScope()).get(coord.toString()).getNumLigne();
    }

    /**
     * @return le dictionnaire de coordonnees compilees
     */
    public Hashtable<String, Hashtable<String, Programme>> obtenirCoordCompileDict() {
        return coordCompileDict;
    }

    public boolean enAction() {
        return (compilationActive || executionActive);
    }

    public void arreterExecution() {
        executionActive = false;
    }

    /**
     * @return le parser utilise par l'interpreteur (voir ASAst)
     */
    public ASAst getAst() {
        return ast;
    }

    public ASModuleManager getAsModuleManager() {
        return asModuleManager;
    }

    /**
     * @param nomDuScope <br><li>cree un nouveau scope et ajoute la premiere coordonnee a ce scope</li>
     * @return la premiere coordonnee du scope
     */
    public String nouveauScope(String nomDuScope) {
        coordCompileDict.put(nomDuScope, new Hashtable<>());
        // peut-etre faire en sorte qu'il y ait une erreur si le scope existe deja
        coordCompileTime.add(new Coordonnee("<0>" + nomDuScope));
        coordCompileDict.get(nomDuScope).put("<0>" + nomDuScope, new Programme() {
            @Override
            public Object execute() {
                return null;
            }

            @Override
            public String toString() {
                return "DEBUT FONCTION: '" + nomDuScope + "'";
            }
        });
        return "<0>" + nomDuScope;
    }

    /**
     * met fin au scope actuel et retourne dans le scope precedent
     *
     * @return la nouvelle coordonne actuelle
     */
    public String finScope() {
        if (coordCompileTime.size() == 1) {
            throw new ErreurFermeture("main", "");
        }
        coordCompileTime.remove(coordCompileTime.size() - 1);
        return coordCompileTime.get(coordCompileTime.size() - 1).toString();
    }

    public ArrayList<Data> consumeData() {
        var data = new ArrayList<>(this.datas);
        this.datas.clear();
        return data;
    }

    /**
     * @return la coordonne actuelle lors de l'execution du code
     */
    public Coordonnee obtenirCoordRunTime() {
        return coordRunTime;
    }

    /**
     * permet de changer la coordonne lors de l'execution du code
     *
     * @param coord <br><li>la nouvelle coordonnee</li>
     */
    public void setCoordRunTime(String coord) {
        coordRunTime.setCoord(coord);
    }

    /**
     * @param nom
     * @return
     */
    public boolean leBlocExiste(String nom) {
        return coordCompileDict.get(coordRunTime.getScope()).containsKey("<1>" + nom + coordRunTime.toString());
    }

    public boolean laCoordExiste(String coord) {
        return coordCompileDict.get(coordRunTime.getScope()).containsKey(coord + coordRunTime.toString());
    }

    /**
     * Cette fonction permet de compiler des lignes de code afin de pouvoir les executer (voir Executeur.executerMain)
     *
     * @param lignes            <li>
     *                          Type: String[]
     *                          </li>
     *                          <li>
     *                          Represente les lignes de code a compiler, une ligne se finit par un <code>\n</code>
     *                          </li>
     * @param compilationForcee <br><li>
     *                          Type: boolean
     *                          </li>
     *                          <li>
     *                          Indique si l'on souhaite forcer la compilation du code, <br>
     *                          (le code sera alors compile meme s'il est identique au code precedemment compile)
     *                          </li>
     */
    public JSONArray compiler(String[] lignes, boolean compilationForcee) {
        reset();

        /*
         * Cette condition est remplie si l'array de lignes de codes mises en parametres est identique
         * a l'array des dernieres lignes de code compilees
         *
         * -> Elle evite donc de compiler le meme code plusieurs fois
         *
         * Cependant, cette condition peut etre overwrite si la compilation est forcee (compilationForce serait alors true)
         */
        if (Arrays.equals(lignes, anciennesLignes) && !compilationForcee) {
            if (debug) System.out.println("No changes: compilation done");
            return new JSONArray();
        } else {
            // Si le code est different ou que la compilation est forcee, compiler les lignes
            //System.out.println(Arrays.toString(PreCompiler.preCompile(lignes)));
            lignes = PreCompiler.preCompile(lignes);
            return compiler(lignes);
        }
    }

    /**
     * Fonction privee charge de compiler un array de ligne de code
     *
     * @param lignes <li>
     *               Type: String[]
     *               </li>
     *               <li>
     *               Represente les lignes de code a compiler, une ligne se finit par un <code>\n</code>
     *               </li>
     */
    private JSONArray compiler(String[] lignes) {

        // sert au calcul du temps qu'a pris le code pour etre compile
        LocalDateTime before = LocalDateTime.now();

        if (debug) System.out.println("compiling...");

        // vide le dictionnaire de coordonne ainsi que la liste de coordonne
        coordCompileDict.clear();
        coordCompileTime.clear();

        // indique le programme est en train de compiler le code
        compilationActive = true;
        canExecute = false;

        // Hashtable representant les erreurs detectees par le linter avant la compilation (voir ASLinter.appliquerLinter)
        //Hashtable<Integer, String> erreurs = ASLinter.obtenirErreurs();
        //if (! erreurs.isEmpty()) {
        //	// pour chaque erreur detectee par le linter avant la compilation:
        //	// 		afficher l'erreur ainsi que sa position (numero de la ligne)
        //	for (Integer ligne : erreurs.keySet()) {
        //		new ErreurCompilation(erreurs.get(ligne)).afficher(ligne);
        //	}
        //	// s'assure de ne pas compiler le code s'il y a des erreurs
        //	compilationActive = false;
        //	return false;
        //}
        coordCompileTime.add(debutCoord);
        /*
         *  ajoute le scope "main" au dictionnaire de coordonnee
         *  c'est dans ce sous-dictionnaire que seront mises toutes les lignes appartenant au scope "main"
         */
        coordCompileDict.put("main", new Hashtable<>());

        // boucle a travers toutes les lignes de l'array "lignes"
        for (int i = 0; i < lignes.length; i++) {
            String line = lignes[i];

            // produit la liste de Token representant la ligne (voir lexer.lex)
            var lineToken = lexer.lex(line.trim());

            // obtiens la coordonne ainsi que le scope ou sera enregistree la ligne compilee
            String coordActuelle = coordCompileTime.get(coordCompileTime.size() - 1).toString();
            String scopeActuel = coordCompileTime.get(coordCompileTime.size() - 1).getScope();

            // System.out.println(coordActuelle + "   " + scopeActuel);
            try {
                /*
                 * transforme la liste de Token obtenu precedemment en un Object[] de forme:
                 * Object[] {
                 * 		programme (String representant le programme de la ligne
                 * 					ex: AFFICHER expression, POUR NOM_VARIABLE DANS expression, etc.)
                 *
                 * 		arbre de syntaxe de la ligne (voir parser.parse)
                 *
                 * 		le programme sous la forme d'une liste de Token
                 * }
                 */


                Programme ligneParsed;

                if (lineToken.isEmpty()) {
                    ligneParsed = new Programme() {
                        @Override
                        public Object execute() {
                            return null;
                        }
                    };
                } else {
                    ligneParsed = ast.parse(lineToken);
                }

                ligneParsed.setNumLigne(i);

                // met ligneParsed dans le dictionnaire de coordonne
                coordCompileDict.get(scopeActuel).put(coordActuelle, ligneParsed);

                // accede a la fonction prochaineCoord du programme trouvee afin de definir la prochaine coordonnee
                coordRunTime.setCoord(ligneParsed.prochaineCoord(new Coordonnee(coordActuelle), lineToken).toString());
                coordActuelle = coordRunTime.toString();
                scopeActuel = coordRunTime.getScope();

                // si la coordonnee est de taille 0, cela signifie que le code contient un "fin ..." a l'exterieur d'un bloc de code
                // -> cela provoque une erreur de fermeture
                if (coordActuelle.length() == 0) {
                    throw new ErreurFermeture(scopeActuel);
                }

            } catch (ErreurAliveScript err) {
                canExecute = false;
                compilationActive = false;
                err.afficher(this, i + 1);
                return new JSONArray().put(err.getAsData(i + 1));

            }

            // update la coordonnee
            coordCompileTime.set(coordCompileTime.size() - 1,
                    new Coordonnee(coordRunTime.plusUn().toString())
            );

            // ajoute une ligne null ?? la fin pour signaler la fin de l'ex??cution
            if (i + 1 == lignes.length) {
                Programme fin = new Programme.ProgrammeFin();
                fin.setNumLigne(i + 1);
                coordCompileDict.get(scopeActuel).put(coordRunTime.toString(), fin);
            }
        }
        try {
            if (!coordRunTime.getBlocActuel().equals("main")) {
                throw new ErreurFermeture(coordRunTime.getBlocActuel());
            }
            if (!ASFonctionManager.obtenirStructure().isBlank()) {
                throw new ErreurFermeture(ASFonctionManager.obtenirStructure());
            }
        } catch (ErreurAliveScript err) {
            canExecute = false;
            compilationActive = false;
            err.afficher(this, lignes.length);
            return new JSONArray().put(err.getAsData(lignes.length));

        }

        /*
         * affiche le temps qu'a pris la compilation du programme
         */
        if (debug)
            System.out.println("compilation done in "
                               + (LocalDateTime.now().toLocalTime().toNanoOfDay() - before.toLocalTime().toNanoOfDay()) / Math.pow(10, 9)
                               + " seconds\n");

        // set la valeur des anciennes lignes de code aux nouvelles lignes donnees en parametre
        anciennesLignes = lignes;
        compilationActive = false;
        canExecute = true;
        return new JSONArray();
    }

    public Object executerScope(String scope, Hashtable<String, Hashtable<String, Programme>> coordCompileDict, String startCoord) {
        if (coordCompileDict == null) coordCompileDict = this.coordCompileDict;
        if (startCoord == null) startCoord = "<0>" + scope;


        if (!coordCompileDict.containsKey(scope) || !coordCompileDict.get(scope).containsKey(startCoord)) {
            String messageErreur = scope.equals("main")
                    ? "Le code n'a pas \u00E9t\u00E9 compil\u00E9 avant l'ex\u00E9cution"
                    : "Le scope " + scope + " n'existe pas.";
            var err = new ASErreur.ErreurScopeInexistant(messageErreur);
            datas.add(err.getAsData(this));
            arreterExecution();
            err.afficher(this);
            return datas.toString();
        }
        // set la coordonne au debut du scope
        coordRunTime.setCoord(startCoord);

        Object resultat = "[]";
        Programme ligneParsed = null;

        while (executionActive && canExecute) {
            // System.out.println(coordRunTime);
            // get la ligne a executer dans le dictionnaire de coordonnees
            ligneParsed = coordCompileDict.get(scope).get(coordRunTime.toString());

            if (ligneParsed instanceof Programme.ProgrammeFin) { // ne sera vrai que si cela est la derniere ligne du programme
                coordRunTime.setCoord(null);
                break;
            }


            // s'il y a une erreur dans l'execution, on arr???te l'execution et on ???crit le message d'erreur dans la console de l'app
            try {
                // execution de la ligne et enregistrement du resultat dans la variable du meme nom
                resultat = ligneParsed.execute();

                if (resultat instanceof Data) {
                    datas.add((Data) resultat);

                } else if (resultat != null && !coordRunTime.getScope().equals("main")) {
                    // ne sera vrai que si l'on retourne d'une fonction
                    break;
                }

                if (datas.size() >= MAX_DATA_BEFORE_SEND) {
                    synchronized (datas) {
                        return datas.toString();
                    }
                }
            } catch (StopSendData e) {
                // data is already a Serialized JSONObject
                return e.getDataString();

            } catch (AskForDataResponse e) {
                datas.add(e.getData());
                return datas.toString();

            } catch (StopSetInfo e) {
                datas.add(e.getData());

            } catch (ErreurAliveScript e) {
                // si l'erreur lancee est de type ASErreur.ErreurExecution (Voir ASErreur.java),
                // on l'affiche et on arrete l'execution du programme
                datas.add(e.getAsData(this));
                arreterExecution();
                e.afficher(this);
                resultat = null;
                break;

            } catch (RuntimeException e) {
                // s'il y a une erreur, mais que ce n'est pas une erreur se trouvant dans ASErreur, c'est une
                // erreur de syntaxe, comme l'autre type d'erreur, on l'affiche et on arrete l'execution du programme
                e.printStackTrace();
                datas.add(new ErreurSyntaxe("Une erreur interne inconnue est survenue lors de l'ex\u00E9cution de la ligne, v\u00E9rifiez que la syntaxe est valide")
                        .getAsData(this));
                if (debug) System.out.println(coordRunTime);
                arreterExecution();
                resultat = null;
                break;
            }
            // on passe a la coordonnee suivante
            coordRunTime.plusUn();
        }
        return (ligneParsed instanceof Programme.ProgrammeFin || !executionActive || resultat == null) ? datas.toString() : resultat;
    }

    /**
     * fonction executant le scope principal ("main")
     */
    public JSONArray executerMain(boolean resume) {
        executionActive = true;
        // sert au calcul du temps qu'a pris le code pour etre execute
        LocalDateTime before = debug ? LocalDateTime.now() : null;

        if (obtenirCoordCompileDict().get("main").isEmpty()) {
            arreterExecution();
            String messageErreur = "Le code n'a pas \u00E9t\u00E9 compil\u00E9 avant l'ex\u00E9cution";
            var err = new ASErreur.ErreurScopeInexistant(messageErreur);
            datas.add(err.getAsData(this));
            arreterExecution();
            err.afficher(this);
            var result = new JSONArray(datas);
            datas.clear();
            return result;
        }

        Object resultat;

        if (!resume) {
            // cr??er scopeInstance globale
            ASScope.pushCurrentScopeInstance(ASScope.getCurrentScope().makeScopeInstance(null));
            resultat = executerScope("main", null, null);
        } else {
            executeurState.load();
            resultat = resumeExecution();
        }

        JSONArray returnData;
        try {
            returnData = new JSONArray(resultat.toString());
        } catch (JSONException err) {
            returnData = new JSONArray(datas);
        }
        /*
         * affiche si l'execution s'est deroulee sans probleme ou si elle a ete interrompue par une erreur
         * affiche le temps qu'a pris l'execution du programme (au complet ou jusqu'a l'interruption)
         */
        executeurState.save();
        if (coordRunTime.toString() == null || !executionActive) {
            if (debug && before != null) {
                System.out.println("execution " + (executionActive ? "done" : "interruped") + " in " +
                                   (LocalDateTime.now().toLocalTime().toNanoOfDay() - before.toLocalTime().toNanoOfDay()) / 10e9 + " seconds\n");
                System.out.println(datas);
            }
            // boolean servant a indique que l'execution est terminee
            executionActive = false;
            reset();
            returnData.put(Data.endOfExecution());
        }
        datas.clear();

        return returnData;
    }

    public String executerFonction(String nomFonction, ArrayList<ASObjet<?>> args) {
        executionActive = true;
        this.coordCompileDict.put("remote_func", new Hashtable<>(Map.of("<0>remote_func", new Programme() {
            @Override
            public Object execute() {
                var var = ASScope.getCurrentScopeInstance().getVariable(nomFonction);
                if (var == null) {
                    return new ASNul();
                }
                var valeur = var.getValeurApresGetter();
                if (valeur instanceof ASFonctionInterface fonction) {
                    return fonction.apply(args);
                }
                throw new ASErreur.ErreurTypePasAppelable("Un \u00E9l\u00E9ment de type '" + valeur.obtenirNomType() + "' ne peut pas \u00EAtre appel\u00E9");
            }
        })));
        setCoordRunTime("<0>remote_func");
        return executerMain(true).toString();
    }

    public String executerFonction(String nomFonction, JSONArray args) {
        return executerFonction(nomFonction, ASObjetConverter.fromJSON(args).getValue());
    }

    private Object resumeExecution() {
        Coordonnee coordActuel = obtenirCoordRunTime();
        return executerScope(coordActuel.getScope(), null, coordActuel.toString());
    }

    /**
     * reset tout a neuf pour la prochaine execution
     */
    private void reset() {
        ASScope.resetAllScope();
        // cr??er le scope global
        ASScope.makeNewCurrentScope();

        // supprime les variables, fonctions et iterateurs de la memoire
        datas.clear();

        ASFonctionManager.reset();

        asModuleManager.utiliserModuleBuiltins();
        //for (ASObjet.Fonction fonction : asModuleManager.getModuleBuiltins().getFonctions())
        //    FonctionManager.ajouterFonction(fonction);
        //for (ASObjet.Variable variable : asModuleManager.getModuleBuiltins().getVariables()) {
        //    Scope.getCurrentScope().declarerVariable(variable);
        //}
        Declarer.reset();
        DataVoiture.reset();

        // remet la coordonnee d'execution au debut du programme
//        coordRunTime.setCoord(debutCoord.toString());
        //if (ast instanceof ASAstExperimental) {
        //    ast = new ASAst();
        //}
    }

    @Override
    public String toString() {
        return "Executeur{" +
               "lexer=" + lexer + "\n" +
               ", coordRunTime=" + coordRunTime + "\n" +
               ", datas=" + datas + "\n" +
               ", dataResponse=" + dataResponse + "\n" +
               ", context=" + context + "\n" +
               ", anciennesLignes=" + Arrays.toString(anciennesLignes) + "\n" +
               '}';
    }
}










