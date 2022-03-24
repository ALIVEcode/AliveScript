package interpreteur.as.modules;

import interpreteur.as.lang.*;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.modules.builtins.BuiltinsListeUtils;
import interpreteur.as.modules.builtins.BuiltinsNombreUtils;
import interpreteur.as.modules.builtins.BuiltinsTexteUtils;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASType;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import language.Translator;

import java.util.*;
import java.util.function.Supplier;

public class ModuleBuiltins {
    private static final Supplier<ASObjet<?>> getVarsLocales = () -> {
        List<ASVariable> variableList = new ArrayList<>(ASScope.getCurrentScopeInstance().getVariableStack());
        return new ASListe(variableList.stream().map(var -> new ASTexte(var.obtenirNom())).toArray(ASTexte[]::new));
    };
    private static final Supplier<ASObjet<?>> getVarsGlobales = () -> {
        List<ASVariable> variableList = new ArrayList<>(ASScope.getScopeInstanceStack().firstElement().getVariableStack());
        return new ASListe(variableList.stream().map(var -> new ASTexte(var.obtenirNom())).toArray(ASTexte[]::new));
    };
    private static final Supplier<ASObjet<?>> getVarListe = () -> {
        HashSet<ASVariable> variables = new HashSet<>();
        ASScope.getScopeInstanceStack().forEach(scopeInstance -> variables.addAll(scopeInstance.getVariableStack()));
        return new ASListe(variables.stream().map(var -> new ASTexte(var.obtenirNom())).toArray(ASTexte[]::new));
    };

    /*
     * Module builtins: contient toutes les fonctions utiliser par defaut dans le langage
     */
    //public static List<ASObjet.Fonction> fonctions =
    public static ASVariable[] variables = new ASVariable[]{
            new ASConstante("bob", new ASTexte("(~°3°)~")),
            // finl
            new ASConstante("modules.builtins.constants.endl", new ASTexte("\n")),
            // varLocales
            new ASVariable("modules.builtins.variables.localVar", new ASListe(), ASTypeBuiltin.liste.asType()).setGetter(getVarsLocales).setReadOnly(),
            // varGlobales
            new ASVariable("modules.builtins.variables.globalVar", new ASListe(), ASTypeBuiltin.liste.asType()).setGetter(getVarsGlobales).setReadOnly(),
            // varListe
            new ASVariable("modules.builtins.variables.listVar", new ASListe(), ASTypeBuiltin.liste.asType()).setGetter(getVarListe).setReadOnly(),
    };

    static ASModule charger(Executeur executeurInstance) {
        ASFonctionModule[] fonctions = new ASFonctionModule[]{
                // afficher
                new ASFonctionModule("modules.builtins.functions.print", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(new ASType("tout"), "element", new ASTexte(""))
                }, ASTypeBuiltin.rien.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> element = this.getValeurParam("element");
                        executeurInstance.addData(new Data(Data.Id.AFFICHER).addParam(element.toString()));
                        executeurInstance.ecrire(element.toString());
                        return new ASNul();
                    }
                },
                // attendre
                new ASFonctionModule("modules.builtins.functions.wait", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(new ASType("nombre"), "duree", new ASEntier(0))
                }, ASTypeBuiltin.rien.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> duree = this.getValeurParam("duree");
                        executeurInstance.addData(new Data(Data.Id.ATTENDRE).addParam(((Number) duree.getValue()).doubleValue()));
                        return new ASNul();
                    }
                },

                /*
                 * aleatoire:
                 * 		@param choix:
                 * 			-> type: liste ou texte
                 * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
                 *
                 * 		@type_retour null (aucune contrainte sur le type retourne)
                 *
                 * 		@return -> si "choix" est de type liste: un element aleatoirement choisi dans la liste
                 * 				-> si "choix" est de type texte: une lettre aleatoirement choisi dans le texte
                 */
                // aleatoire
                new ASFonctionModule("modules.builtins.functions.random", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(new ASType("iterable"), "choix", null)
                }, new ASType("tout")) {
                    @Override
                    public ASObjet<?> executer() {
                        if (this.getParamsValeursDict().get("choix") instanceof ASListe liste) {
                            return liste.get((int) (Math.random() * liste.taille()));
                        } else {
                            ASTexte texte = (ASTexte) this.getParamsValeursDict().get("choix");
                            return new ASTexte(texte.getValue().charAt((int) (Math.random() * texte.taille())));
                        }
                    }
                },

                /*
                 * typeDe:
                 * 		@param objet:
                 * 			-> type: null (aucune contrainte sur le type)
                 * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
                 *
                 * 		@type_retour texte
                 *
                 * 		@return le nom du type de l'objet passe en parametre dans un "texte"
                 */
                // typeDe
                new ASFonctionModule("modules.builtins.functions.typeOf", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(new ASType("tout"), "element", null)
                }, new ASType("texte")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASTexte(this.getParamsValeursDict().get("element").obtenirNomType());
                    }
                },
                //booleen
                new ASFonctionModule("modules.builtins.functions.boolean", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.tout.asType(), "element", null)
                }, ASTypeBuiltin.booleen.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASBooleen(this.getParamsValeursDict().get("element").boolValue());
                    }
                },
                // auto
                new ASFonctionModule("modules.builtins.functions.auto", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null)
                }, ASTypeBuiltin.tout.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        var txt = ((ASTexte) this.getValeurParam("txt")).getValue().trim();
                        if (ASNombre.estNumerique(txt)) {
                            ASNombre.parse(this.getValeurParam("txt"));
                        } else if (ASBooleen.estBooleen(txt)) {
                            return new ASBooleen(txt);
                        }
                        return new ASTexte(txt);
                    }
                },
                // clef
                new ASFonctionModule("modules.builtins.functions.key", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.paire.asType(), "_paire", null)
                }, ASTypeBuiltin.texte.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return ((ASPaire) getValeurParam("_paire")).clef();
                    }
                },
                // val
                new ASFonctionModule("modules.builtins.functions.val", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.paire.asType(), "_paire", null)
                }, ASTypeBuiltin.texte.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return ((ASPaire) getValeurParam("_paire")).valeur();
                    }
                },


                /*
                 * affiche le commentaire entre les symboles
                 * (-:
                 *  -
                 * :-)
                 * dans la fonction passée en paramètre
                 *
                 */
                // info
                new ASFonctionModule("modules.builtins.functions.info", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.tout.asType(), "element", null)
                }, new ASType("tout")) {
                    @Override
                    public ASObjet<?> executer() {
                        return this.getParamsValeursDict().get("element");
                    }
                },
                // getVar
                new ASFonctionModule("modules.builtins.functions.getVar", new ASFonctionModule.Parametre[]{
                        new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "nomVariable", null)
                }, new ASType("tout")) {
                    @Override
                    public ASObjet<?> executer() {
                        String nomVar = (String) this.getValeurParam("nomVariable").getValue();
                        ASVariable var = ASScope.getCurrentScopeInstance().getVariable(nomVar);
                        if (var == null) {
                            throw new ASErreur.ErreurVariableInconnue("La variable '" + nomVar + "' n'est pas d\u00E9clar\u00E9e dans ce scope.");
                        }
                        return var.getValeurApresGetter();
                    }
                }
        };

        var fonctionsBuiltins = new ArrayList<>(List.of(fonctions));
        fonctionsBuiltins.addAll(List.of(BuiltinsListeUtils.fonctions));
        fonctionsBuiltins.addAll(List.of(BuiltinsTexteUtils.fonctions));
        fonctionsBuiltins.addAll(List.of(BuiltinsNombreUtils.fonctions));

        Translator translator = executeurInstance.getTranslator();
        fonctionsBuiltins.forEach(f -> f.setNom(translator.translate(f.getNom())));
        Arrays.stream(variables).forEach(v -> v.setNom(translator.translate(v.obtenirNom())));

        return new ASModule(fonctionsBuiltins.toArray(ASFonctionModule[]::new), variables);
    }
}
















