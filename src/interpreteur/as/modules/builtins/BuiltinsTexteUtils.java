package interpreteur.as.modules.builtins;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.datatype.ASBooleen;
import interpreteur.as.lang.datatype.ASListe;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.lang.datatype.ASTexte;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASType;
import interpreteur.executeur.Executeur;

import java.util.Iterator;

public class BuiltinsTexteUtils {
    public static ASFonctionModule[] fonctions = new ASFonctionModule[]{
            // texte
            new ASFonctionModule("modules.builtins.functions.string", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.tout.asType(), "element", null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getValeurParam("element").toString());
                }
            },
            // maj
            new ASFonctionModule("modules.builtins.functions.upper", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getParamsValeursDict().get("txt").getValue().toString().toUpperCase());
                }
            },
            // minus
            new ASFonctionModule("modules.builtins.functions.lower", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null)
            }, new ASType("texte")) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getParamsValeursDict().get("txt").getValue().toString().toLowerCase());
                }
            },
            // remplacer
            new ASFonctionModule("modules.builtins.functions.replace", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "sequence", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "remplacement", null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    String txt = this.getParamsValeursDict().get("txt").getValue().toString();
                    String pattern = this.getParamsValeursDict().get("sequence").getValue().toString();
                    String remplacement = this.getParamsValeursDict().get("remplacement").getValue().toString();
                    return new ASTexte(txt.replace(pattern, remplacement));
                }
            },
            // remplacerRe
            new ASFonctionModule("modules.builtins.functions.replaceReg", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "pattern", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "remplacement", null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    String txt = this.getParamsValeursDict().get("txt").getValue().toString();
                    String pattern = this.getParamsValeursDict().get("pattern").getValue().toString();
                    String remplacement = this.getParamsValeursDict().get("remplacement").getValue().toString();
                    return new ASTexte(txt.replaceAll(pattern, remplacement));
                }
            },
            // match
            new ASFonctionModule("modules.builtins.functions.match", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "pattern", null)
            }, ASTypeBuiltin.booleen.asType()) {
                @Override
                public ASBooleen executer() {
                    String txt = this.getParamsValeursDict().get("txt").getValue().toString();
                    String pattern = this.getParamsValeursDict().get("pattern").getValue().toString();
                    return new ASBooleen(txt.matches(pattern));
                }
            },
            // estNumerique
            new ASFonctionModule("modules.builtins.functions.isNumeric", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null)
            }, ASTypeBuiltin.booleen.asType()) {
                @Override
                public ASBooleen executer() {
                    try {
                        Integer.parseInt(this.getParamsValeursDict().get("txt").getValue().toString());
                        return new ASBooleen(true);
                    } catch (NumberFormatException ignored) {
                    }
                    return new ASBooleen(false);
                }
            },

            /*
             * format:
             * 		@params t:
             * 			-> type: texte
             * 			-> valeur par defaut: null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             *      @params valeurs:
             *          -> type: liste
             *          -> valeur par defaut : null (n'en a pas, il est donc obligatoire de lui en donner une lors de l'appel de la fonction)
             *
             * 		@type_retour texte
             *
             * 		@return un texte où les {} sont remplacés par les valeurs dans la liste
             */
            // format
            new ASFonctionModule("modules.builtins.functions.format", new ASFonctionModule.Parametre[]{
                    new ASFonctionModule.Parametre(ASTypeBuiltin.texte.asType(), "txt", null),
                    new ASFonctionModule.Parametre(ASTypeBuiltin.liste.asType(), "valeurs", null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASObjet<?> executer() {
                    String texte = ((ASTexte) this.getValeurParam("txt")).getValue();
                    Iterator<ASObjet<?>> valeurs = ((ASListe) this.getValeurParam("valeurs")).getValue().iterator();

                    while (texte.contains("{}")) {
                        if (valeurs.hasNext()) {
                            texte = texte.replaceFirst("[{][}]", valeurs.next().toString());
                        } else {
                            throw new ASErreur.ErreurFormatage("Le nombre de {} doit etre egal au nombre de valeur dans la liste");
                        }
                    }

                    if (texte.contains("{}"))
                        throw new ASErreur.ErreurFormatage("Le nombre de {} doit etre egal au nombre de valeur dans la liste");

                    return new ASTexte(texte);
                }
            }
    };

    public static ASModule charger(Executeur executeurInstance) {
        return new ASModule(fonctions);
    }
}

















