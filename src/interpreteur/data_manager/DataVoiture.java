package interpreteur.data_manager;

/*----------------------------- Gestion des Datas de la voiture -----------------------------*/

import org.json.JSONObject;

import javax.swing.text.BadLocationException;

/**
 *
 */
public class DataVoiture {
    public static JSONObject dataVoiture = null;
    public static boolean request = false;

    public static void requestDataVoiture() {
        request = true;
    }

    /*
    !!!!!!!!! Utilisé par le serveur, ne pas utiliser dans l'interpréteur !!!!!!!!!
     */
    public static void setDataVoiture(String dataVoiture) throws BadLocationException {
        DataVoiture.dataVoiture = new JSONObject(dataVoiture);
        System.out.println("Data de la voiture depuis Java: " + dataVoiture);
        request = false;
    }

    public static void reset() {
        dataVoiture = null;
        request = false;
    }

    public static void dataVoitureHasChanged() {
        dataVoiture = null;
    }

    public static JSONObject getDataVoiture() {
        return dataVoiture;
    }
}