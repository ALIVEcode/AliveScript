package interpreteur.data_manager;


import org.json.JSONArray;
import org.json.JSONObject;

public class Data extends JSONObject {
    /*----------------------------- ID de data -----------------------------*/

    /**
     * {
     * "id": @(id de la data),
     * "m": @(valeur de la methode de la data),
     * "d": @(temps en secondes pendant lesquels le programme va s'arreter),
     * "p": [ @(valeur du param1) , @(valeur du param2), ...]
     * }
     */
    public Data(Id id) {
        this.put("id", id.getId());
        this.put("d", 0.0);
        this.put("p", new JSONArray());
    }

    public static Data endOfExecution() {
        Data endOfExecutionData = new Data(Id.ARRETER);
        endOfExecutionData.put("id", 0);
        return endOfExecutionData;
    }

    public Data addParam(Object val) {
        this.getJSONArray("p").put(val);
        return this;
    }

    public Data addDodo(double dodo) {
        this.put("d", dodo);
        return this;
    }

    /**
     * MOTEUR = 1xx
     * SENSEUR = 2xx
     * UTILITAIRE = 3xx
     * ERREUR = 400 à 449: erreur execution
     * 450 à 499: erreur compilation
     */
    public enum Id {
        ARRETER(Categorie.MOTEUR),          // 0
        AVANCER(Categorie.MOTEUR),          // 1
        RECULER(Categorie.MOTEUR),          // 2
        TOURNER(Categorie.MOTEUR),          // 3
        TOURNER_DROITE(Categorie.MOTEUR),   // 4
        TOURNER_GAUCHE(Categorie.MOTEUR),   // 5
        ROULER(Categorie.MOTEUR),           // 6
        TOURNER_ABS(Categorie.MOTEUR),      // 7

        AFFICHER(Categorie.UTILITAIRE),  // 0
        ATTENDRE(Categorie.UTILITAIRE),  // 1
        NOTIF_INFO(Categorie.UTILITAIRE),     // 2
        NOTIF_ERR(Categorie.UTILITAIRE),     // 2

        ERREUR(Categorie.ERREUR),

        GET(Categorie.GET),

        SET(Categorie.SET),
        SET_CAR_SPEED(Categorie.SET),

        CONSEIL(Categorie.TIPS),
        AVERTISSEMENT(Categorie.TIPS),

        CREER_REGRESSION(Categorie.AI),     // 0
        OPTIMISER_REGRESSION(Categorie.AI), // 1
        AFFICHER_NUAGE(Categorie.AI),       // 2
        GET_EVALUER(Categorie.AI),          // 3
        FONCTION_COUT(Categorie.AI),        // 4
        TEST_RESEAU_NEURONES(Categorie.AI), // 5
        VALEUR_COLONNE(Categorie.AI),       // 6
        CREER_MODELE(Categorie.AI),         // 7
        ONE_HOT(Categorie.AI),              // 8
        NORMALISER(Categorie.AI),           // 9

        // IoT Data Ids
        // document related
        UPDATE_DOC(Categorie.IOT),              // 0
        SUBSCRIBE_LISTENER(Categorie.IOT),      // 1
        UNSUBSCRIBE_LISTENER(Categorie.IOT),    // 2
        // misc events
        SEND_ACTION(Categorie.IOT),             // 3
        SEND_BROADCAST(Categorie.IOT),          // 4
        SEND_ROUTE(Categorie.IOT),              // 5
        RECEIVE_BROADCAST(Categorie.IOT),       // 6
        // http requests
        GET_DOC(Categorie.IOT),                 // 7
        GET_FIELD(Categorie.IOT),               // 8
        // send iot error
        SEND_IOT_ERROR(Categorie.IOT),          // 9
        ;

        private final int id;
        private final Categorie categorie;

        Id(Categorie categorie) {
            this.categorie = categorie;
            this.id = this.categorie.getNext();
        }

        Id(Categorie categorie, int manualId) {
            this.categorie = categorie;
            categorie.getNext();
            this.id = manualId;
        }


        public static Id dataIdFromId(int id) {
            for (Id dataId : Id.values()) if (dataId.getId() == id) return dataId;
            return null;
        }

        public Categorie getCategorie() {
            return this.categorie;
        }


        public int getId() {
            return this.id;
        }

        /* ----------------------------- Categorie d'ID -----------------------------*/
        private enum Categorie {
            MOTEUR,
            SENSEUR,
            UTILITAIRE,
            ERREUR,
            GET,
            SET,
            TIPS,
            AI,
            IOT;

            private int count = 0;

            public int getNext() {
                return (this.ordinal() + 1) * 100 + this.count++;
            }

            public int getCount() {
                return this.count;
            }

            public int getCategorieId() {
                return (this.ordinal() + 1) * 100;
            }
        }
    }
}





















