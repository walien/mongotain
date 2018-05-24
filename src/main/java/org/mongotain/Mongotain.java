package org.mongotain;

import org.mongotain.components.DBConnector;
import org.mongotain.components.ScriptsDiscoverer;
import org.mongotain.components.ScriptsExecutor;
import org.mongotain.domain.Config;
import org.mongotain.domain.Script;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class Mongotain {

    private static final Logger logger = getLogger(Mongotain.class);
    private final Config config;

    private Mongotain(Config config) {
        this.config = config;
    }

    public static MongotainBuilder builder() {
        return new MongotainBuilder();
    }

    public Mongotain start() {
        logger.info("[STARTUP] - Mongotain is starting with provided config {}", config);
        DBConnector configDBConnector = new DBConnector(config.getConfigDbUri(), config.getConfigDbName()).connect();
        DBConnector targetDBConnector = new DBConnector(config.getTargetDbUri(), config.getTargetDbName()).connect();
        List<Script> availableScripts = new ScriptsDiscoverer(config.getScriptsPath(), configDBConnector).discover();
        List<Script> executedScripts = new ScriptsExecutor(targetDBConnector, config.isAllowMultipleScriptExecutions()).execute(availableScripts);
        executedScripts.forEach(script -> configDBConnector.getCollection(Script.COLLECTION).save(script));
        return this;
    }

    public static class MongotainBuilder {

        private final Config config = new Config();

        public Mongotain build() {
            return new Mongotain(config);
        }

        private IllegalArgumentException missingMandatoryParam(String paramName) {
            return new IllegalArgumentException("mandatory param " + paramName + " is missing");
        }

        public MongotainBuilder configDB(String dbName, String dbUri) {
            config.setConfigDbName(Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("configDbName")));
            config.setConfigDbUri(Optional.ofNullable(dbUri).orElseThrow(() -> missingMandatoryParam("configDbUri")));
            return this;
        }

        public MongotainBuilder configDB(String dbName) {
            return configDB(dbName, "mongodb://localhost");
        }

        public MongotainBuilder targetDB(String dbName, String dbUri) {
            config.setTargetDbName(Optional.ofNullable(dbName).orElseThrow(() -> missingMandatoryParam("targetDbName")));
            config.setTargetDbUri(Optional.ofNullable(dbUri).orElseThrow(() -> missingMandatoryParam("targetDbUri")));
            return this;
        }

        public MongotainBuilder targetDB(String dbName) {
            return targetDB(dbName, "mongodb://localhost");
        }

        public MongotainBuilder scriptsPath(Path scriptsPath) {
            config.setScriptsPath(Optional.ofNullable(scriptsPath).orElseThrow(() -> missingMandatoryParam("scriptsPath")));
            return this;
        }

        public MongotainBuilder allowMultipleScriptExecutions() {
            config.setAllowMultipleScriptExecutions(true);
            return this;
        }
    }
}
