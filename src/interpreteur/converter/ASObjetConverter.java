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
        switch (asObjet) {
            case ASListe asListe -> {
                if (asListe.estDict()) {
                    return ASDictToJSON(asListe);
                } else {
                    return ASListeToJSON(asListe);
                }
            }
            case null, default -> throw new JSONException("Unexpected value: " + asObjet);
        }
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
    public static ASObjet<?> fromJSON(JSONObject data) {
        var result = new ASListe();

        for (var key : (Set<String>) data.keySet()) {
            var element = data.get(key);
            switch (element) {
                case JSONArray jsonArray -> result.ajouterElement(fromJSON(jsonArray));
                case JSONObject jsonObject -> result.ajouterElement(fromJSON(jsonObject));
                default -> result.ajouterElement(fromJavaObject(element));
            }
        }
        return result;
    }

    public static ASObjet<?> fromJSON(JSONArray data) {
        var result = new ASListe();

        for (int i = 0; i < data.length(); i++) {
            var element = data.get(i);
            switch (element) {
                case JSONArray jsonArray -> result.ajouterElement(fromJSON(jsonArray));
                case JSONObject jsonObject -> result.ajouterElement(fromJSON(jsonObject));
                default -> result.ajouterElement(fromJavaObject(element));
            }
        }
        return result;
    }


    public static ASObjet<?> fromJavaObject(Object object) throws ASObjetConversionException {
        return switch (object) {
            case Number num -> ASNombre.cast(num);
            case String s -> new ASTexte(s);
            case Boolean bool -> new ASBooleen(bool);
            case ArrayList<?> arrayList -> {
                var result = new ASListe();
                for (var element : arrayList) {
                    result.ajouterElement(fromJavaObject(element));
                }
                yield result;
            }
            case Map<?, ?> map -> {
                var result = new ASListe();
                for (var element : map.entrySet()) {
                    result.ajouterElement(fromJavaObject(element));
                }
                yield result;
            }
            case Map.Entry<?, ?> entry -> {
                var key = fromJavaObject(entry.getValue());
                if (!(key instanceof ASTexte asTexte)) {
                    throw new ASObjetConversionException("The key of an entry must be a String");
                }
                yield new ASPaire(asTexte, fromJavaObject(entry.getValue()));
            }
            case null -> new ASNul();
            default -> throw new ASObjetConversionException("The converted object must be " +
                                                            "a primitive, an ArrayList or a Map (recursively)");
        };
    }
}
