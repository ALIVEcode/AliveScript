package interpreteur.as.modules.builtins;

import interpreteur.as.lang.ASConstante;
import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASTypeExpr;
import interpreteur.executeur.Executeur;

import java.util.Collections;
import java.util.List;

public class BuiltinsNombreUtils {

    public static ASFonctionModule[] fonctions = new ASFonctionModule[]{
            // entier
            new ASFonctionModule("modules.builtins.functions.int", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null),
                    new ASParametre("base", ASTypeBuiltin.entier.asType(), new ASEntier(10))
            }, new ASTypeExpr("entier")) {
                @Override
                public ASEntier executer() {
                    String valeur = this.getParamsValeursDict().get("txt").toString();
                    int base = (Integer) this.getParamsValeursDict().get("base").getValue();
                    try {
                        return new ASEntier(Integer.parseInt(valeur, base));
                    } catch (NumberFormatException ignored) {
                        throw new ASErreur.ErreurType("impossible de convertir '" + valeur + "' en nombre entier de base " + base);
                    }
                }
            },
            // abs
            new ASFonctionModule("modules.builtins.functions.abs", new ASParametre[]{
                    new ASParametre("x", new ASTypeExpr("nombre"), null)
            }, new ASTypeExpr("nombre")) {
                @Override
                public ASObjet<?> executer() {
                    return new ASDecimal(Math.abs(((Number) this.getValeurParam("x").getValue()).doubleValue()));
                }
            },
            // decimal
            new ASFonctionModule("modules.builtins.functions.float", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null)
            }, new ASTypeExpr("decimal")) {
                @Override
                public ASDecimal executer() {
                    try {
                        return new ASDecimal(Double.parseDouble(this.getParamsValeursDict().get("txt").toString()));
                    } catch (NumberFormatException ignored) {
                        throw new ASErreur.ErreurType("impossible de convertir '" + this.getParamsValeursDict().get("element").toString() + "' en nombre decimal");
                    }
                }
            },

            // nombre
            new ASFonctionModule("modules.builtins.functions.number", new ASParametre[]{
                    new ASParametre("txt", ASTypeBuiltin.texte.asType(), null)
            }, new ASTypeExpr("decimal")) {
                @Override
                public ASNombre executer() {
                    String nb = this.getParamsValeursDict().get("txt").toString();
                    if (!ASNombre.estNumerique(nb))
                        throw new ASErreur.ErreurType("Impossible de convertir " + nb + " en nombre entier ou d\u00E9cimal.");

                    boolean estDecimal = nb.contains(".");
                    if (estDecimal) return new ASDecimal(Double.parseDouble(nb));
                    else return new ASEntier(Integer.parseInt(nb));
                }
            },

            // bin
            new ASFonctionModule("modules.builtins.functions.bin", new ASParametre[]{
                    new ASParametre("nb", new ASTypeExpr("entier"), null)
            }, new ASTypeExpr("texte")) {
                @Override
                public ASTexte executer() {
                    return new ASTexte(Integer.toBinaryString((Integer) this.getValeurParam("nb").getValue()));
                }
            }
    };
    public static List<ASConstante> constantes = Collections.emptyList();

    public ASModule charger(Executeur executeurInstance) {
        return null;
    }
}

















