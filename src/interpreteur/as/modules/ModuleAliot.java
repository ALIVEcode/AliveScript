package interpreteur.as.modules;

import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.ASType;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.lang.ASVariable;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.modules.core.ASModule;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;
import org.json.JSONArray;

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
                new ASFonctionModule("ecouterDocChange", new ASParametre[]{
                        ASParametre.obligatoire("champs", ASTypeBuiltin.iterable.asType()),
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> champs = getValeurParam("champs");
                        ASObjet<?> callback = getValeurParam("callback");
                        String funcName = callback instanceof ASFonctionModule fonctionModule
                                ? fonctionModule.getNom()
                                : callback instanceof ASFonction fonction
                                ? fonction.getNom()
                                : null;

                        if (champs instanceof ASListe liste) {
                            if (liste.getValue().stream().anyMatch(el -> !(el instanceof ASTexte))) {
                                throw new ASErreur.ErreurAppelFonction("La liste doit \u00EAtre une liste d'\u00E9l\u00E9ments de type texte");
                            }
                            var champsStringList = liste.getValue().stream().map(el -> (String) el.getValue()).toList();
                            executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER).addParam(new JSONArray(champsStringList)).addParam(funcName));
                            return new ASNul();
                        }

                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER).addParam(new JSONArray().put(champs.getValue())).addParam(funcName));
                        return new ASNul();
                    }
                },

                // arreterEcoute
                // TODO
                new ASFonctionModule("arreterEcoute", new ASParametre[]{
                        ASParametre.obligatoire("ecouteurId", ASTypeBuiltin.entier.asType())
                }, ASTypeBuiltin.booleen.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASNul();
                    }
                },

                //----------------- envoyeur -----------------//

                // envoyerAction
                // TODO
                new ASFonctionModule("envoyerAction", new ASParametre[]{
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER));
                        return new ASNul();
                    }
                },

                // changerDoc
                // TODO
                new ASFonctionModule("changerDoc", new ASParametre[]{
                        ASParametre.obligatoire("callback", ASTypeBuiltin.fonctionType.asType())
                }, ASTypeBuiltin.entier.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        executeurInstance.addData(new Data(Data.Id.SUBSCRIBE_LISTENER));
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
        };

        ASVariable[] variables = {

        };

        return new ASModule(fonctionModules, variables);
    }
}
