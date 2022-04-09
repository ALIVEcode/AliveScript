package server.lintingApi;

import com.sun.net.httpserver.HttpExchange;
import interpreteur.as.modules.EnumModule;
import interpreteur.executeur.Executeur;
import interpreteur.generateurs.lexer.Regle;
import language.Language;
import org.json.JSONArray;
import org.json.JSONObject;
import server.BaseApi;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.utils.QueryUtils.getValueOfQuery;

public class ASLinterApi extends BaseApi {
    private final static Hashtable<Language, JSONObject> LINTER_INFO;
    private final static Hashtable<Language, String> LINTER_INFO_STRINGIFY;
    private static ArrayList<Regle> REGLES;
    private static Logger logger;

    static {
        LINTER_INFO = loadLinterInfo();
        LINTER_INFO_STRINGIFY = new Hashtable<>();
        LINTER_INFO.forEach(((language, jsonObject) -> LINTER_INFO_STRINGIFY.put(language, jsonObject.toString())));
    }

    public ASLinterApi(String CORS_ORIGIN) {
        super(CORS_ORIGIN);
    }

    public static void setLogger(Logger logger) {
        ASLinterApi.logger = logger;
    }


    private static JSONObject loadLanguage(Language language) {
        var executor = new Executeur(language);
        REGLES = executor.getLexer().getReglesAjoutees();
        var translator = executor.getTranslator();
        var functionAsCommands = List.of(
                translator.translate("modules.builtins.functions.print"),       // afficher
                translator.translate("modules.builtins.functions.notif"),       // notif
                translator.translate("modules.builtins.functions.notif_err"),   // notif_err
                translator.translate("modules.builtins.functions.wait"),        // attendre
                translator.translate("modules.builtins.functions.forward"),     // avancer
                translator.translate("modules.builtins.functions.backward"),    // reculer
                translator.translate("modules.builtins.functions.left"),        // gauche
                translator.translate("modules.builtins.functions.right"),       // droite
                translator.translate("modules.builtins.functions.stop")         // arreter
        );
        // adds all builtin functions
        List<String> fonctionsBuiltins = executor.getAsModuleManager().getModuleBuiltins().getNomsConstantesEtFonctions();
        functionAsCommands.forEach(fonctionsBuiltins::remove); // remove because they will be with command
        fonctionsBuiltins = fonctionsBuiltins
                .stream()
                .map(fct -> "\\b" + fct + "\\b")
                .toList();

        // adds all the name of the allowed modules
        List<String> modules = Stream.of(EnumModule.values())
                .map(module -> translator.translate(module.name()))
                .collect(Collectors.toList());
        modules.add("\\b\"experimental\"\\b");

        List<String> commands = getPatternsOfCategory("commandes");
        // adds the functions related to the motor to the commands
        functionAsCommands.forEach(func -> commands.add("\\b" + func + "\\b"));

        List<String> operators = getPatternsOfCategory("arithmetique");
        operators.addAll(getPatternsOfCategory("assignements"));
        operators.add(getReglePattern("FLECHE"));
        operators.add(getReglePattern("DEUX_POINTS"));
        operators.addAll(getPatternsOfCategory("comparaison"));

        return new JSONObject()
                .put("datatype", Map.ofEntries(getMembersOfCategory("type_de_donnees")
                        .map(regle -> Map.entry(regle.getNom().toLowerCase(), regle.getPattern()))
                        .toArray(Map.Entry[]::new))
                )
                .put("datatypes_names", getPatternsOfCategory("nom_type_de_donnees"))
                .put("modules", modules)
                .put("blocs", getPatternsOfCategory("blocs"))
                .put("commands", commands)
                .put("logiques", new JSONArray(getPatternsOfCategory("porte_logique"))
                        .put(getReglePattern("PAS"))
                )
                .put("fonctions", getPatternsOfCategory("fonctions"))
                .put("fin", getReglePattern("FIN"))
                .put("fonctions_builtin", fonctionsBuiltins)
                .put("control_flow", new JSONArray(getPatternsOfCategory("control_flow")))
                .put("const", getReglePattern("CONSTANTE"))
                .put("variable", "[a-zA-Z_\\u00a1-\\uffff][a-zA-Z\\d_\\u00a1-\\uffff]*")
                .put("operators", operators);
    }

    public static Hashtable<Language, JSONObject> loadLinterInfo() {
        var linterLanguageDict = new Hashtable<Language, JSONObject>();
        for (var language : Language.values()) {
            linterLanguageDict.put(language, loadLanguage(language));
        }

        return linterLanguageDict;
    }

    private static List<String> getPatternsOfCategory(String nomCategorie) {
        return getMembersOfCategory(nomCategorie).map(Regle::getPattern).collect(Collectors.toList());
    }

    private static List<String> getNamesOfCategory(String nomCategorie) {
        return getMembersOfCategory(nomCategorie).map(regle -> regle.getNom().toLowerCase()).collect(Collectors.toList());
    }

    private static Stream<Regle> getMembersOfCategory(String nomCategorie) {
        return REGLES.stream().filter(regle -> regle.getCategorie().equals(nomCategorie));
    }

    private static String getReglePattern(String regleName) {
        return REGLES.stream().filter(regle -> regle.getNom().equals(regleName)).findFirst().orElseThrow().getPattern();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);


        String requestParamValue;

        requestParamValue = switch (httpExchange.getRequestMethod().toUpperCase()) {
            case "GET" -> handleGetRequest(httpExchange);
            case "POST" -> "{}";
            default -> "";
        };
        handleResponse(httpExchange, requestParamValue);
    }

    private String handleGetRequest(HttpExchange httpExchange) {
        String lang = getValueOfQuery(httpExchange.getRequestURI().getQuery(), "lang");
        logger.info("Lang is " + lang);
        if (!Language.isSupportedLanguage(lang)) {
            logger.warning(
                    "Language's codeISO639_1 " +
                    (lang == null ? "unspecified" : "not supported (codeISO639_1: '" + lang + "'). Defaulting to French ('FR')."));
            lang = "FR";
        }
        logger.info("Collecting linter info...");
        logger.info("[SUCCESS] Linter info collected and sent successfully");
        return LINTER_INFO_STRINGIFY.get(Language.valueOf(lang.toUpperCase()));
    }
}




















