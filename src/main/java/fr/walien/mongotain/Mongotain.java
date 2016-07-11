package fr.walien.mongotain;

import fr.walien.mongotain.components.DBConnector;
import fr.walien.mongotain.components.ScriptsDiscoverer;
import fr.walien.mongotain.components.ScriptsExecutor;
import fr.walien.mongotain.domain.Script;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class Mongotain {

    private static final Logger logger = getLogger(Mongotain.class);

    private final Path scriptsPath;
    private final String configDbName;
    private final String configDbUri;
    private final String targetDbName;
    private final String targetDbUri;

    private Mongotain(Path scriptsPath, String configDbUri, String configDbName, String targetDbUri, String targetDbName) {
        this.scriptsPath = scriptsPath;
        this.configDbUri = configDbUri;
        this.configDbName = configDbName;
        this.targetDbUri = targetDbUri;
        this.targetDbName = targetDbName;
    }

    public static MongotainBuilder builder() {
        return new MongotainBuilder();
    }

    public Mongotain init() {
        logger.info("[STARTUP] - Mongotain is starting with [ configDB = {}/{}, targetDB = {}/{}, " +
                "scriptsPath = {} ]", configDbUri, configDbName, targetDbUri, targetDbName, scriptsPath);
        DBConnector configDBConnector = new DBConnector(configDbUri, configDbName).connect();
        DBConnector targetDBConnector = new DBConnector(targetDbUri, targetDbName).connect();
        List<Script> availableScripts = new ScriptsDiscoverer(scriptsPath, configDBConnector).discover();
        List<Script> executedScripts = new ScriptsExecutor(targetDBConnector).execute(availableScripts);
        executedScripts.stream().forEach(script -> configDBConnector.getCollection(Script.COLLECTION).save(script));
        return this;
    }

    public static class MongotainBuilder {

        private Path scriptsPath;
        private String configDbName;
        private String configDbUri;
        private String targetDbName;
        private String targetDbUri;

        public Mongotain build() {
            return new Mongotain(scriptsPath, configDbUri, configDbName, targetDbUri, targetDbName);
        }

        private IllegalArgumentException missingMandatoryParam(String paramName) {
            return new IllegalArgumentException("mandatory param " + paramName + "is missing");
        }

        public MongotainBuilder configDB(Optional<String> uri, String dbName) {
            this.configDbUri = uri.orElse("mongodb://localhost");
            this.configDbName = Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("configDbName"));
            return this;
        }

        public MongotainBuilder targetDB(Optional<String> uri, String dbName) {
            this.targetDbUri = uri.orElse("mongodb://localhost");
            this.targetDbName = Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("targetDbName"));
            return this;
        }

        public MongotainBuilder scriptsPath(Path scriptsPath) {
            this.scriptsPath = Optional.ofNullable(scriptsPath).orElseThrow(() -> missingMandatoryParam("scriptsPath"));
            return this;
        }
    }
}
