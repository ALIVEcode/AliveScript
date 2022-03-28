package interpreteur.as.modules;

import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASType;
import interpreteur.data_manager.Data;
import interpreteur.data_manager.DataVoiture;
import interpreteur.executeur.Executeur;
import org.json.JSONObject;

import java.util.List;

public class ModuleVoiture {

    private static Object getDataVoiture(String parametre) {
        JSONObject dataVoiture = DataVoiture.getDataVoiture();
        if (dataVoiture == null) {
            DataVoiture.requestDataVoiture();
            throw new ASErreur.AskForDataResponse(new Data(Data.Id.GET).addParam("car"));
        } else {
            return dataVoiture.get(parametre);
        }
    }

    static ASModule charger(Executeur executeurInstance) {
        var fonctions = new ASFonctionModule[]{
                // x
                new ASFonctionModule("modules.car.functions.x", new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("x")).doubleValue());
                    }
                },
                // y
                new ASFonctionModule("modules.car.functions.y", new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("y")).doubleValue());
                    }
                },
                // getDistAvant
                new ASFonctionModule("modules.car.functions.getFrontDist", new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dA")).doubleValue());
                    }
                },
                // getDistGauche
                new ASFonctionModule("modules.car.functions.getLeftDist", new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dG")).doubleValue());
                    }
                },
                // getDistDroite
                new ASFonctionModule("modules.car.functions.getRightDist", new ASType("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dD")).doubleValue());
                    }
                },
                // rouler
                new ASFonctionModule("modules.car.functions.wheel", new ASParametre[]{
                        new ASParametre("vitesseGauche", new ASType("entier"), null),
                        new ASParametre("vitesseDroite", new ASType("entier"), null)
                }, new ASType("nulType")) {
                    @Override
                    public ASObjet<?> executer() {
                        throw new ASErreur.StopSetInfo(new Data(Data.Id.ROULER)
                                .addParam(this.getValeurParam("vitesseGauche"))
                                .addParam(this.getValeurParam("vitesseDroite")));
                    }
                }
        };
        var variables = new ASVariable[]{
                // vitesse
                new ASVariable("modules.car.variables.speed", new ASEntier(10), new ASType("tout"))
                        .setGetter(() -> new ASDecimal(((Number) getDataVoiture("speed")).doubleValue()))
                        .setSetter((valeur) -> {
                            throw new ASErreur.StopSetInfo(new Data(Data.Id.SET_CAR_SPEED).addParam(valeur));
                        }
                ),
                // distAvant
                new ASVariable("modules.car.variables.frontDist", new ASEntier(10), new ASType("tout"))
                        .setGetter(() -> new ASDecimal(((Number) getDataVoiture("dA")).doubleValue()))
                        .setReadOnly()
        };

        var translator = executeurInstance.getTranslator();
        List.of(fonctions).forEach(f -> f.setNom(translator.translate(f.getNom())));
        List.of(variables).forEach(v -> v.setNom(translator.translate(v.obtenirNom())));

        return new ASModule(fonctions, variables);
    }
}
