package interpreteur.as.modules.builtins;

import interpreteur.as.lang.*;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.modules.core.ASModule;
import interpreteur.executeur.Executeur;

import java.util.*;

public class BuiltinsListeUtils {

    public static ASFonctionModule[] fonctions = new ASFonctionModule[]{
            /*
             * sep:
             * 		@param t:
             * 			-> type: texte
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour liste
             *
             * 		@return une liste où chaque élément est la lettre du string passé en paramètre
             */
            //liste
            new ASFonctionModule("modules.builtins.functions.list", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
            }, new ASTypeExpr("liste")) {
                @Override
                public ASObjet<?> executer() {
                    ASTexte texte = (ASTexte) this.getParamsValeursDict().get("txt");
                    return new ASListe(texte.arrayDeLettres());
                }
            },

            /*
             * inv: (inverser)
             * 		@param t:
             * 			-> type: lst
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour iterable
             *
             * 		@return un iterable où chaque élément est inversé
             */
            // inv
            new ASFonctionModule("modules.builtins.functions.reverse", new ASParametre[]{
                    new ASParametre("iter", ASTypeBuiltin.iterable.asType(), null),
            }, new ASTypeExpr("iterable")) {
                @Override
                public ASObjet<?> executer() {
                    ASIterable<?> element = (ASIterable<?>) this.getValeurParam("iter");
                    if (element instanceof ASListe) {
                        ASListe newListe = new ASListe();
                        for (int i = element.taille() - 1; i >= 0; i--) newListe.ajouterElement(element.get(i));
                        return newListe;
                    } else {
                        StringBuilder inv = new StringBuilder();
                        for (int i = element.taille() - 1; i >= 0; i--) inv.append(element.get(i).toString());
                        return new ASTexte(inv.toString());
                    }
                }
            },

            /*
             * map:
             * 		@param f:
             * 			-> type: fonction
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@param l:
             * 			-> type: liste
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour liste
             *
             * 		@return la liste formee suite a l'application de la fonction sur chaque element de la liste
             */
            // map
            new ASFonctionModule("modules.builtins.functions.map", new ASParametre[]{
                    new ASParametre("f", new ASTypeExpr("fonction"), null),
                    new ASParametre("lst", new ASTypeExpr("liste"), null)
            }, new ASTypeExpr("liste")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    ASObjet<?> f = this.getParamsValeursDict().get("f");
                    return new ASListe(liste.getValue().stream().map(element -> ((ASFonctionInterface) f)
                                    .apply(new ArrayList<>(List.of((ASObjet<?>) element))))
                            .toArray(ASObjet[]::new));
                }
            },

            /*
             * filtrer:
             * 		@param f:
             * 			-> type: fonction (doit retourner un booleen)
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@param l:
             * 			-> type: liste
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour liste
             *
             * 		@return la liste formee des elements de la liste initiale pour lesquels la fonction f a retourne vrai
             */
            // filtrer
            new ASFonctionModule("modules.builtins.functions.filter", new ASParametre[]{
                    new ASParametre("f", ASTypeBuiltin.fonctionType.asType(), null),
                    new ASParametre("lst", ASTypeBuiltin.liste.asType(), null)
            }, new ASTypeExpr("liste")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    ASObjet<?> f = this.getParamsValeursDict().get("f");
                    return new ASListe(liste.getValue().stream().filter(element -> ((ASFonctionInterface) f)
                                    .apply(new ArrayList<>(List.of((ASObjet<?>) element)))
                                    .boolValue())
                            .toArray(ASObjet[]::new));
                }
            },

            /*
             * joindre:
             * 		@param lst:
             * 			-> type: liste
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@param separateur:
             * 			-> type: texte
             * 			-> valeur par defaut: " "
             *
             * 		@type_retour texte
             *
             * 		@return le texte forme en joignant chaque elements de la liste initiale avec le separateur entre chaque element
             */
            // joindre
            new ASFonctionModule("modules.builtins.functions.join", new ASParametre[]{
                    new ASParametre("lst", ASTypeBuiltin.liste.asType(), null),
                    new ASParametre("separateur", ASTypeBuiltin.texte.asType(), new ASTexte(""))
            }, new ASTypeExpr("texte")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    ASTexte separateur = (ASTexte) this.getParamsValeursDict().get("separateur");
                    return new ASTexte(
                            String.join(
                                    separateur.getValue(),
                                    liste.getValue().stream().map(Object::toString).toArray(String[]::new)
                            )
                    );
                }
            },
            // somme
            new ASFonctionModule("modules.builtins.functions.sum", new ASParametre[]{
                    new ASParametre("lst", new ASTypeExpr("liste"), null)
            }, new ASTypeExpr("nombre")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    double somme = liste.getValue().stream().mapToDouble(e -> ((Number) e.getValue()).doubleValue()).sum();
                    return new ASDecimal(somme);
                }
            },
            // max
            new ASFonctionModule("modules.builtins.functions.max", new ASParametre[]{
                    new ASParametre("lst", new ASTypeExpr("liste"), null)
            }, new ASTypeExpr("nombre")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    OptionalDouble somme = liste.getValue().stream().mapToDouble(e -> ((Number) e.getValue()).doubleValue()).max();
                    if (somme.isEmpty()) {
                        throw new ASErreur.ErreurComparaison("tous les \u00E9l\u00E9ments de la liste doivent être des nombres pour pouvoir obtenir le maximum");
                    }
                    return new ASDecimal(somme.getAsDouble());
                }
            },
            // min
            new ASFonctionModule("modules.builtins.functions.min", new ASParametre[]{
                    new ASParametre("lst", new ASTypeExpr("liste"), null)
            }, new ASTypeExpr("nombre")) {
                @Override
                public ASObjet<?> executer() {
                    ASListe liste = (ASListe) this.getParamsValeursDict().get("lst");
                    OptionalDouble somme = liste.getValue().stream().mapToDouble(e -> ((Number) e.getValue()).doubleValue()).min();
                    if (somme.isEmpty()) {
                        throw new ASErreur.ErreurComparaison("tous les \u00E9l\u00E9ments de la liste doivent être des nombres pour pouvoir obtenir le minimum");
                    }
                    return new ASDecimal(somme.getAsDouble());
                }
            },

            /*
             * tailleDe:
             * 		@param objet:
             * 			-> type: liste ou texte
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour entier
             *
             * 		@return -> si "choix" est de type liste: le nombre d'element dans la liste
             * 				-> si "choix" est de type texte: le nombre de caractere dans le texte
             */
            // tailleDe
            new ASFonctionModule("modules.builtins.functions.length", new ASParametre[]{
                    new ASParametre("iter", ASTypeBuiltin.iterable.asType(), null)
            }, new ASTypeExpr("entier")) {
                @Override
                public ASObjet<?> executer() {
                    ASIterable<?> val = (ASIterable<?>) this.getParamsValeursDict().get("iter");
                    return new ASEntier(val.taille());
                }
            },
            // indexDe
            new ASFonctionModule("modules.builtins.functions.index", new ASParametre[]{
                    new ASParametre("valeur", ASTypeBuiltin.tout.asType(), null),
                    new ASParametre("iter", ASTypeBuiltin.iterable.asType(), null)
            }, new ASTypeExpr("entier")) {
                @Override
                public ASObjet<?> executer() {
                    ASIterable<?> iter = (ASIterable<?>) this.getParamsValeursDict().get("iter");
                    ASObjet<?> val = this.getParamsValeursDict().get("valeur");
                    int idx;
                    if (iter instanceof ASTexte txt && val instanceof ASTexte txtVal) {
                        idx = txt.getValue().indexOf(txtVal.getValue());
                    } else if (iter instanceof ASListe lst) {
                        idx = lst.getValue().indexOf(val);
                    } else {
                        throw new ASErreur.ErreurType("La valeur doit \u00EAtre de type texte lorsque l'on recherche " +
                                                      "l'index d'un \u00E9l\u00E9ment de type texte");
                    }
                    return idx != -1 ? new ASEntier(idx) : new ASNul();
                }
            }
    };


    public static List<ASConstante> constantes = Collections.emptyList();


    public ASModule charger(Executeur executeurInstance) {
        return null;
    }
}














