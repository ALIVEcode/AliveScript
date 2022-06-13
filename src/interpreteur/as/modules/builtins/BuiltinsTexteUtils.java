package interpreteur.as.modules.builtins;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.datatype.fonction.ASParametre;
import interpreteur.as.lang.datatype.fonction.ASFonctionModule;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.executeur.Executeur;

import java.util.Iterator;

public class BuiltinsTexteUtils {
    public static ASFonctionModule[] fonctions = new ASFonctionModule[]{
            // texte
            new ASFonctionModule("modules.builtins.functions.string", new ASParametre[]{
                    new ASParametre("element", ASTypeBuiltin.tout.asType(), null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getValeurParam("element").toString());
                }
            },
            // maj
            new ASFonctionModule("modules.builtins.functions.upper", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null)
            }, ASTypeBuiltin.texte.asType()) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getParamsValeursDict().get("txt").getValue().toString().toUpperCase());
                }
            },
            // minus
            new ASFonctionModule("modules.builtins.functions.lower", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null)
            }, new ASTypeExpr("texte")) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(this.getParamsValeursDict().get("txt").getValue().toString().toLowerCase());
                }
            },
            // remplacer
            new ASFonctionModule("modules.builtins.functions.replace", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("sequence", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("remplacement", ASTypeBuiltin.texte.asType(), null)
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
            new ASFonctionModule("modules.builtins.functions.replaceReg", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("pattern", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("remplacement", ASTypeBuiltin.texte.asType(), null)
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
            new ASFonctionModule("modules.builtins.functions.match", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("pattern", ASTypeBuiltin.texte.asType(), null)
            }, ASTypeBuiltin.booleen.asType()) {
                @Override
                public ASBooleen executer() {
                    String txt = this.getParamsValeursDict().get("txt").getValue().toString();
                    String pattern = this.getParamsValeursDict().get("pattern").getValue().toString();
                    return new ASBooleen(txt.matches(pattern));
                }
            },
            // estNumerique
            new ASFonctionModule("modules.builtins.functions.isNumeric", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null)
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
            new ASFonctionModule("modules.builtins.functions.format", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("valeurs", ASTypeBuiltin.liste.asType(), null)
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

















