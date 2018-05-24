package org.mongotain;

import org.mongotain.components.DBConnector;
import org.mongotain.components.ScriptsDiscoverer;
import org.mongotain.components.ScriptsExecutor;
import org.mongotain.domain.Script;
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

    public Mongotain start() {
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
            return new IllegalArgumentException("mandatory param " + paramName + " is missing");
        }

        public MongotainBuilder configDB(String dbName, String dbUri) {
            this.configDbName = Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("configDbName"));
            this.configDbUri = Optional.ofNullable(dbUri).orElseThrow(() -> missingMandatoryParam("configDbUri"));
            return this;
        }

        public MongotainBuilder configDB(String dbName) {
            return configDB(dbName, "mongodb://localhost");
        }

        public MongotainBuilder targetDB(String dbName, String dbUri) {
            this.targetDbName = Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("targetDbName"));
            this.targetDbUri = Optional.ofNullable(dbUri).orElseThrow(() -> missingMandatoryParam("targetDbUri"));
            return this;
        }

        public MongotainBuilder targetDB(String dbName) {
            return targetDB(dbName, "mongodb://localhost");
        }

        public MongotainBuilder scriptsPath(Path scriptsPath) {
            this.scriptsPath = Optional.ofNullable(scriptsPath).orElseThrow(() -> missingMandatoryParam("scriptsPath"));
            return this;
        }
    }
}
