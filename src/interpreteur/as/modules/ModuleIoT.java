package interpreteur.as.modules;

import interpreteur.as.lang.ASFonctionModule;
import interpreteur.as.lang.datatype.*;
import interpreteur.as.erreurs.ASErreur;
import interpreteur.as.lang.ASTypeBuiltin;
import interpreteur.as.modules.core.ASModule;
import interpreteur.data_manager.Data;
import interpreteur.executeur.Executeur;


public class ModuleIoT {

    static ASModule charger(Executeur executeurInstance) {

        /*
         * Module Iot:
         *
         * enleverEcouteur(ecouteurId: entier) -> rien
         *
         * ecouterEvenement(eventId: texte, callback: fonctionType, ecouteurId: entier | nulType = nul) -> entier # ecouteurId
         * envoyerEvenement(eventId: texte, donnees: dict) -> rien
         * enleverEcouteursEvenement(eventId: texte) -> rien
         *
         *
         * changerDoc(champs: dict) -> rien
         * ecouterDoc(champs: liste | texte, callback: fonctionType, ecouteurId: entier | nulType = nul) -> entier # ecouteurId
         * enleverEcouteursDoc(champs: liste | texte) -> rien
         */


        return new ASModule(new ASFonctionModule[]{
                new ASFonctionModule("update",
                        new ASParametre[]{
                                new ASParametre(
                                        "projectId", ASTypeBuiltin.texte.asType(),
                                        null
                                ),
                                new ASParametre(
                                        "id", ASTypeBuiltin.texte.asType(),
                                        null
                                ),
                                new ASParametre(
                                        "value", ASTypeBuiltin.tout.asType(),
                                        null
                                )
                        }, ASTypeBuiltin.nombre.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASTexte projectId = (ASTexte) this.getValeurParam("projectId");
                        ASTexte id = (ASTexte) this.getValeurParam("id");
                        ASObjet<?> valueAs = (ASObjet<?>) this.getValeurParam("value");

                       // executeurInstance.addData(new Data(Data.Id.UPDATE_COMPONENT).addParam(projectId).addParam(id).addParam(valueAs.getValue()));
                        return new ASNul();
                    }
                },
                new ASFonctionModule("get",
                        new ASParametre[]{
                                new ASParametre(
                                        "key", ASTypeBuiltin.texte.asType(),
                                        new ASNul()
                                )
                        }, ASTypeBuiltin.nombre.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        ASObjet<?> uncastedKey = (ASObjet<?>) this.getValeurParam("key");
                        if (uncastedKey instanceof ASNul) {
                            return new ASTexte(executeurInstance.getContext().toString());
                        }

                        ASTexte key = (ASTexte) uncastedKey;

                        Object obj = executeurInstance.getContext().get(key.toString());
                        if (obj == null) {
                            throw new ModuleIoT.KeyNotPresent("Erreur, la clé " + key + " n'est pas présente dans l'objet de réponse.");
                        }
                        if (obj instanceof String) {
                            return new ASTexte(obj);
                        }
                        if (obj instanceof Number num) {
                            return ASNombre.cast(num);
                        }
                        if (obj instanceof Boolean bool) {
                            return new ASBooleen(bool);
                        }
                        return new ASNul();
                    }
                },
                new ASFonctionModule("getComponentValue",
                        new ASParametre[]{
                                new ASParametre(
                                        "projectId", ASTypeBuiltin.texte.asType(),
                                        null
                                ),
                                new ASParametre(
                                        "id", ASTypeBuiltin.texte.asType(),
                                        null
                                )
                        }, ASTypeBuiltin.nombre.asType()) {
                    @Override
                    public ASObjet<?> executer() {
                        return new ASNul();
                    }
                }
        });
    }

    private static class KeyNotPresent extends ASErreur.ErreurAliveScript {
        public KeyNotPresent(String message) {
            super(message, "ErreurCleDansContexte");
        }
    }
}
