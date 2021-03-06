package interpreteur.as.modules;

import interpreteur.as.lang.ASConstante;
import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.ASDecimal;
import interpreteur.as.lang.datatype.ASEntier;
import interpreteur.as.lang.datatype.ASObjet;
import interpreteur.as.lang.datatype.ASParametre;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASType;
import interpreteur.executeur.Executeur;


public class ModuleMath {
    static ASModule charger(Executeur executeurInstance) {
        return new ASModule(new ASFonctionModule[]{
                // rad
                new ASFonctionModule("modules.math.functions.rad", new ASParametre[]{
                        new ASParametre("x", new ASType("nombre"), null)
                }, new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        double angle = ((Number) this.getValeurParam("x").getValue()).doubleValue();
                        return new ASDecimal(Math.toRadians(angle));
                    }
                },
                // deg
                new ASFonctionModule("modules.math.functions.deg", new ASParametre[]{
                        new ASParametre("x", new ASType("nombre"), null)
                }, new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        double angle = ((Number) this.getValeurParam("x").getValue()).doubleValue();
                        return new ASDecimal(Math.toDegrees(angle));
                    }
                },
                // sin
                new ASFonctionModule("modules.math.functions.sin", new ASParametre[]{
                        new ASParametre("x", new ASType("nombre"), null)
                }, new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        double angle = ((Number) this.getValeurParam("x").getValue()).doubleValue();
                        return new ASDecimal(Math.sin(Math.toRadians(angle)));
                    }
                },
                // cos
                new ASFonctionModule("modules.math.functions.cos", new ASParametre[]{
                        new ASParametre("x", new ASType("nombre"), null)
                }, new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        double angle = ((Number) this.getValeurParam("x").getValue()).doubleValue();
                        return new ASDecimal(Math.cos(Math.toRadians(angle)));
                    }
                },
                // tan
                new ASFonctionModule("modules.math.functions.tan", new ASParametre[]{
                        new ASParametre("x", new ASType("nombre"), null)
                }, new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        double angle = ((Number) this.getValeurParam("x").getValue()).doubleValue();
                        return new ASDecimal(Math.tan(Math.toRadians(angle)));
                    }
                },
                // arrondir
                new ASFonctionModule("modules.math.functions.round", new ASParametre[]{
                        new ASParametre("n", new ASType("nombre"), null),
                        new ASParametre("nbSignificatifs", new ASType("entier"), new ASEntier(0)),
                }, new ASType("nombre")) {
                    @Override
                    public ASObjet<?> executer() {
                        double n = ((Number) this.getValeurParam("n").getValue()).doubleValue();
                        double shift = Math.pow(10, (Integer) this.getValeurParam("nbSignificatifs").getValue());
                        return new ASDecimal(Math.round(n * shift) / shift);
                    }
                },
        }, new ASVariable[]{
                // PI
                new ASConstante("modules.math.constants.pi", new ASDecimal(Math.PI)),
                // E
                new ASConstante("modules.math.constants.e", new ASDecimal(Math.E))
        });
    }
}
