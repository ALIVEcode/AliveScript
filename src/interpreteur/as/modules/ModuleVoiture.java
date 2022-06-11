package interpreteur.as.modules;

import interpreteur.as.lang.datatype.module.ASFonctionModule;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.modules.core.ASModule;
import interpreteur.as.lang.ASTypeExpr;
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

                new ASFonctionModule("x", new ASTypeExpr("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("x")).doubleValue());
                    }
                },

                new ASFonctionModule("y", new ASTypeExpr("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("y")).doubleValue());
                    }
                },

                new ASFonctionModule("getDistAvant", new ASTypeExpr("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dA")).doubleValue());
                    }
                },
                new ASFonctionModule("getDistGauche", new ASTypeExpr("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dG")).doubleValue());
                    }
                },
                new ASFonctionModule("getDistDroite", new ASTypeExpr("decimal")) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASDecimal(((Number) getDataVoiture("dD")).doubleValue());
                    }
                },

                new ASFonctionModule("rouler", new ASParametre[]{
                        new ASParametre("vitesseGauche", new ASTypeExpr("entier"), null),
                        new ASParametre("vitesseDroite", new ASTypeExpr("entier"), null)
                }, new ASTypeExpr("nulType")) {
                    @Override
                    public ASObjet<?> executer() {
                        throw new ASErreur.StopSetInfo(new Data(Data.Id.ROULER)
                                .addParam(this.getValeurParam("vitesseGauche"))
                                .addParam(this.getValeurParam("vitesseDroite")));
                    }
                }
        };
        var variables = new ASVariable[]{
                new ASVariable("vitesse", new ASEntier(10), new ASTypeExpr("tout"))
                        .setGetter(() -> new ASDecimal(((Number) getDataVoiture("speed")).doubleValue()))
                        .setSetter((valeur) -> {
                            throw new ASErreur.StopSetInfo(new Data(Data.Id.SET_CAR_SPEED).addParam(valeur));
                        }
                ),
                new ASVariable("distAvant", new ASEntier(10), new ASTypeExpr("tout"))
                        .setGetter(() -> new ASDecimal(((Number) getDataVoiture("dA")).doubleValue()))
                        .setReadOnly()
        };

        var translator = executeurInstance.getTranslator();
        List.of(fonctions).forEach(f -> f.setNom(translator.translate(f.getNom())));
        List.of(variables).forEach(v -> v.setNom(translator.translate(v.getNom())));

        return new ASModule(fonctions, variables);
    }
}
