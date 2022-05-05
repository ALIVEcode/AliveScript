package server.executionApi;

import interpreteur.executeur.Executeur;
import interpreteur.data_manager.Data;
import language.Language;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AliveScriptService {
    private final static Hashtable<UUID, AliveScriptService> runningServices = new Hashtable<>();
    private static double maxServiceLifeSpan = 0;
    private static Logger logger;
    private static Logger executionLogger = null;
    private final UUID idToken;
    private final Executeur executeur;
    private boolean resume = false;
    private boolean compiled = false;
    /**
     * the time since the service has been updated
     */
    private double sinceUpdate = 0;
    private int nbOfUpdates = 0;


    private AliveScriptService(UUID idToken, Language language) {
        this.idToken = idToken;
        this.executeur = new Executeur(language);
        try {
            setupLogger();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setMaxServiceLifeSpan(double maxServiceLifeSpan) {
        AliveScriptService.maxServiceLifeSpan = maxServiceLifeSpan;
    }

    public static void setLogger(Logger logger) {
        AliveScriptService.logger = logger;
    }

    public synchronized static void updateAndCleanUp() {
        for (var service : new HashSet<>(runningServices.values())) {
            if (service.sinceUpdate > maxServiceLifeSpan) {
                service.destroy();
                logger.info("Service " + service + " destroyed");
            } else
                service.sinceUpdate += 1;
        }
    }

    public static JSONObject noAliveScriptServiceWithToken() {
        return new JSONObject()
                .put("status", ResponseStatus.FAILED)
                .put("message", "There is no instance of alivescript with that token");
    }

    public static Hashtable<UUID, AliveScriptService> getRunningServices() {
        return runningServices;
    }

    public static AliveScriptService get(UUID idToken) {
        return runningServices.get(idToken);
    }

    public static AliveScriptService create(Language language) {
        UUID idToken = UUID.randomUUID();
        logger.info("New session: " + idToken);
        AliveScriptService aliveScriptService = new AliveScriptService(idToken, language);
        runningServices.put(idToken, aliveScriptService);
        return aliveScriptService;
    }

    public static AliveScriptService destroy(UUID idToken) {
        return runningServices.remove(idToken);
    }

    synchronized private void setupLogger() throws IOException {
        if (executionLogger != null) return;
        var logger = Logger.getLogger(AliveScriptService.class.getName());
        if (Files.notExists(Path.of("./log/"))) {
            Files.createDirectory(Path.of("./log/"));
        }
        FileHandler fileHandler = new FileHandler("./log/executionLogger.log", true);
        logger.setUseParentHandlers(false);
        logger.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
        executionLogger = logger;
    }

    public synchronized void update() {
        this.sinceUpdate = 0;
        this.nbOfUpdates++;
    }

    public AliveScriptService destroy() {
        logger.info("End of session: " + idToken);
        return runningServices.remove(this.idToken);
    }

    public UUID getIdToken() {
        return idToken;
    }

    public JSONArray compile(String[] lines) {
        return compile(lines, null);
    }

    public JSONArray compile(String[] lines, JSONObject context) {
        resume = false;
        executeur.setContext(context);
        JSONArray result = executeur.compiler(lines, true);
        executionLogger.info("Session: " + idToken + "\n"
                             + "Lines:\n" + String.join("\n", lines) + "\n"
                             + "Compilation result:\n" + result);
        compiled = true;
        return result;
    }

    public void pushDataToExecuteur(JSONArray responseData) {
        for (int i = 0; i < responseData.length(); i++)
            executeur.pushDataResponse(responseData.get(i));
    }

    public boolean isCompiled() {
        return compiled;
    }

    public JSONObject notCompiledError() {
        return new JSONObject()
                .put("status", ResponseStatus.FAILED)
                .put("message", "Le code doit \u00EAtre compil\u00E9 avant d'\u00EAtre ex\u00E9cut\u00E9");
    }

    public String execute() {
        JSONObject returnData = new JSONObject();
        JSONArray result = executeur.executerMain(resume);
        ResponseStatus status;
        // if the method was to be called later, resume would be true
        if (!resume) resume = true;

        try {
            boolean executionFinished;
            int index = result.length() - 1;
            if (index < 0) {
                executionFinished = true;
                result.put(Data.endOfExecution());
            } else {
                executionFinished = result.getJSONObject(index).getInt("id") == 0;
            }

            // if the execution is finished, we remove the object from memory
            if (executionFinished) {
                status = ResponseStatus.COMPLETE;
                returnData.put("status", status);
                destroy();
            } else {
                status = ResponseStatus.ONGOING;
                returnData.put("status", status);
                returnData.put("idToken", getIdToken());
            }
            returnData.put("result", result);

        } catch (JSONException err) {
            status = ResponseStatus.FAILED;
            returnData.put("status", status);
            returnData.put("message", "failed to execute du to internal error");
            err.printStackTrace();
        }

        executionLogger.info("Session: " + idToken + "\n"
                             + "Execution result:\n" + result + "\n"
                             + "Status: " + status);

        return returnData.toString();
    }

    @Override
    public String toString() {
        return "server.executionApi.AliveScriptService{" +
               "idToken=" + idToken +
               ", executeur=" + executeur +
               ", resume=" + resume +
               '}';
    }

    enum ResponseStatus {
        COMPLETE,
        ONGOING,
        FAILED;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
}





















