package interpreteur.as.modules;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.*;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.modules.core.ASModule;
import interpreteur.converter.ASObjetConverter;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModuleAliot {
    /*
     * Module Aliot:
     *
     * ecouterAction(actionId: texte | entier, callback: fonction<dict> -> rien) -> entier
     * ecouterAnnonces(callback: fonction<dict> -> rien) -> entier
     * ecouterErreur(callback: fonction<texte> -> rien) -> entier
     * ecouterDocChange(champs: liste | texte, callback: fonction<tout> -> rien) -> entier
     * arreterEcoute(ecouteurId: entier) -> booleen
     *
     * envoyerAction(actionId: texte, donnees: dict) -> rien
     * changerDoc(champs: dict) -> rien
     *
     * annoncer(donnees: dict) -> rien
     *
     */

    public static ASModule charger(Executeur executeurInstance) {
        ASFonctionModule[] fonctionModules = {
                //----------------- écouteurs -----------------//

                // ecouterAnnonces
                // TODO
                new ASFonctionModule("ecouterAnnonces", new ASParametre[]{
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER));
                        return new ASNul();
                    }
                },

                // ecouterErreur
                new ASFonctionModule("ecouterErreur", new ASParametre[]{
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASNul();
                    }
                },

                // ecouterDocChange
                // TODO
                new ASFonctionModule("ecouterDoc", new ASParametre[]{
                        ASParametre.obligatoire("champs", ASTypeBuiltin.iterable.asType()),
                        ASParametre.obligatoire("ecouteur", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> champs = getValeurParam("champs");
                        ASObjet<?> callback = getValeurParam("ecouteur");
                        String funcName = callback instanceof ASFonctionInterface fonction
                                ? fonction.getNom()
                                : null;

                        if (champs instanceof ASListe liste) {
                            if (liste.getValue().stream().anyMatch(el -> !(el instanceof ASTexte))) {
                                throw new ASErreur.ErreurAppelFonction("La liste doit \u00EAtre une liste d'\u00E9l\u00E9ments de type texte");
                            }
                            var champsStringList = liste.getValue().stream().map(el -> (String) el.getValue()).toList();
                            executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER)
                                    .addParam(new JSONArray(champsStringList))
                                    .addParam(funcName));
                            return new ASNul();
                        }

                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER).addParam(new JSONArray().put(champs.getValue())).addParam(funcName));
                        return new ASNul();
                    }
                },

                // arreterEcoute
                // TODO
                new ASFonctionModule("enleverEcouteur", new ASParametre[]{
                        ASParametre.obligatoire("ecouteur", new ASType("texte|fonctionType"))
                }, ASTypeBuiltin.booleen.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> func = getValeurParam("ecouteur");
                        String funcName = func instanceof ASFonctionInterface fonction
                                ? fonction.getNom()
                                : (String) func.getValue();
                        executeurInstance.addData(new Data(Data.Id.UNSUBSCRIBE_LISTENER).addParam(funcName));
                        return new ASNul();
                    }
                },

                //----------------- envoyeur -----------------//

                // envoyerAction
                // TODO retourner un ASEntier
                new ASFonctionModule("envoyerAction", new ASParametre[]{
                        ASParametre.obligatoire("actionId", ASTypeBuiltin.entier.asType()),
                        new ASParametre("data", ASTypeBuiltin.dict.asType(), new ASDict()),
                        new ASParametre("targetId", ASTypeBuiltin.texte.asType(), new ASNul())
                }, ASTypeBuiltin.nulType.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        var data = getValeurParam("data");
                        var actionId = getValeurParam("actionId");
                        var target = getValeurParam("targetId");
                        if (target instanceof ASNul) {
                            executeurInstance.addData(new Data(Data.Id.SEND_ACTION)
                                    .addParam(actionId.getValue())
                                    .addParam(ASObjetConverter.toJSON(data)));
                        } else {
                            executeurInstance.addData(new Data(Data.Id.SEND_ACTION)
                                    .addParam(actionId.getValue())
                                    .addParam(ASObjetConverter.toJSON(data))
                                    .addParam(target.getValue()));
                        }
                        return new ASNul();
                    }
                },

                // changerDoc
                // TODO
                new ASFonctionModule("changerDoc", new ASParametre[]{
                        ASParametre.obligatoire("data", ASTypeBuiltin.dict.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        var data = getValeurParam("data");
                        executeurInstance.addData(new Data(Data.Id.UPDATE_DOC).addParam(ASObjetConverter.toJSON(data)));
                        return new ASNul();
                    }
                },

                // annoncer
                new ASFonctionModule("annoncer", new ASParametre[]{
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER));
                        return new ASNul();
                    }
                },

                new ASFonctionModule("getDoc", new ASParametre[]{
                        new ASParametre("champs", ASTypeBuiltin.texte.asType(), new ASNul())
                }, ASTypeBuiltin.dict.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        if (executeurInstance.getDataResponse().isEmpty()) {
                            ASObjet<?> obj = getValeurParam("champs");
                            if (obj instanceof ASNul) {
                                throw new ASErreur.AskForDataResponse(new Data(Data.Id.GET_DOC));
                            }
                            throw new ASErreur.AskForDataResponse(new Data(Data.Id.GET_FIELD)
                                    .addParam(obj.getValue()));
                        }
                        Object response = executeurInstance.getDataResponse().pop();
                        return ASObjetConverter.fromJavaObjectOrJSON(response);
                    }
                },
        };

        ASVariable[] variables = {

        };

        return new ASModule(fonctionModules, variables);
    }
}
