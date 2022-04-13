package interpreteur.converter;


import interpreteur.as.lang.datatype.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ASObjetConverter {

    /**
     * @param asObjet
     * @return Object -> can be JSONObjet or JSONArray
     */
    public static Object toJSON(ASObjet<?> asObjet) throws JSONException {
        if (asObjet instanceof ASListe asListe) {
            if (asListe.estDict()) {
                return ASDictToJSON(asListe);
            } else {
                return ASListeToJSON(asListe);
            }
        }
        throw new JSONException("Unexpected value: " + asObjet);
    }

    public static JSONObject ASDictToJSON(ASListe asDict) {
        var jsonObject = new JSONObject();
        for (var asObjet : asDict.getValue()) {
            if (!(asObjet instanceof ASPaire paire))
                continue;

            String asKey = paire
                    .getValue()
                    .getKey()
                    .getValue();

            ASObjet<?> asValue = paire.getValue().getValue();
            if (asValue instanceof ASListe asListe) {
                if (asListe.estDict())
                    jsonObject.put(asKey, ASDictToJSON(asListe));
                else
                    jsonObject.put(asKey, ASListeToJSON(asListe));
            } else {
                jsonObject.put(asKey, asValue.getValue());
            }
        }
        return jsonObject;
    }

    public static JSONArray ASListeToJSON(ASListe asListe) {
        var elements = new JSONArray();
        for (var element : asListe.getValue()) {
            if (element instanceof ASListe asListe1) {
                if (asListe1.estDict()) {
                    elements.put(ASDictToJSON(asListe1));
                    continue;
                }
                elements.put(ASListeToJSON(asListe1));
                continue;
            }
            elements.put(element.getValue());
        }
        return elements;
    }

    @SuppressWarnings("unchecked")
    public static ASListe fromJSON(JSONObject data) {
        var result = new ASListe();

        for (var key : (Set<String>) data.keySet()) {
            var asKey = new ASTexte(key);
            if (data.isNull(key)) {
                result.ajouterElement(new ASPaire(asKey, new ASNul()));
                continue;
            }
            var element = data.get(key);
            ASPaire pair;
            if (element instanceof JSONArray jsonArray) {
                pair = new ASPaire(asKey, fromJSON(jsonArray));
            } else if (element instanceof JSONObject jsonObject) {
                pair = new ASPaire(asKey, fromJSON(jsonObject));
            } else {
                pair = new ASPaire(asKey, fromJavaObject(element));
            }
            result.ajouterElement(pair);
        }
        return result;
    }

    public static ASListe fromJSON(JSONArray data) {
        var result = new ASListe();

        for (int i = 0; i < data.length(); i++) {
            if (data.isNull(i)) {
                result.ajouterElement(new ASNul());
                continue;
            }
            var element = data.get(i);
            if (element instanceof JSONArray jsonArray) {
                result.ajouterElement(fromJSON(jsonArray));
            } else if (element instanceof JSONObject jsonObject) {
                result.ajouterElement(fromJSON(jsonObject));
            } else {
                result.ajouterElement(fromJavaObject(element));
            }
        }
        return result;
    }


    public static ASObjet<?> fromJavaObject(Object object) throws ASObjetConversionException {
        if (object instanceof Number num) {
            return ASNombre.cast(num);

        } else if (object instanceof String s) {
            return new ASTexte(s);

        } else if (object instanceof Boolean bool) {
            return new ASBooleen(bool);

        } else if (object instanceof ArrayList<?> arrayList) {
            var result = new ASListe();
            for (var element : arrayList) {
                result.ajouterElement(fromJavaObject(element));
            }
            return result;

        } else if (object instanceof Map<?, ?> map) {
            var result = new ASListe();
            for (var element : map.entrySet()) {
                result.ajouterElement(fromJavaObject(element));
            }
            return result;

        } else if (object instanceof Map.Entry<?, ?> entry) {
            var key = fromJavaObject(entry.getValue());
            if (!(key instanceof ASTexte asTexte)) {
                throw new ASObjetConversionException("The key of an entry must be a String");
            }
            return new ASPaire(asTexte, fromJavaObject(entry.getValue()));

        } else if (object == null) {
            return new ASNul();
        }

        throw new ASObjetConversionException("The converted object must be " +
                                             "a primitive, an ArrayList or a Map (recursively), " +
                                             "not an object ( " + object + " ) of type "
                                             + object.getClass());
    }

    public static ASObjet<?> fromJavaObjectOrJSON(Object object) throws ASObjetConversionException {
        if (object instanceof JSONObject jsonObject) {
            return fromJSON(jsonObject);
        } else if (object instanceof JSONArray jsonArray) {
            return fromJSON(jsonArray);
        }
        return fromJavaObject(object);
    }
}