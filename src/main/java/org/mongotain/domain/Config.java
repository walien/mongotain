package org.mongotain.domain;

import java.nio.file.Path;

public class Config {

    private Path scriptsPath;
    private String configDbName;
    private String configDbUri;
    private String targetDbName;
    private String targetDbUri;

    private boolean allowMultipleScriptExecutions;

    public Path getScriptsPath() {
        return scriptsPath;
    }

    public Config setScriptsPath(Path scriptsPath) {
        this.scriptsPath = scriptsPath;
        return this;
    }

    public String getConfigDbName() {
        return configDbName;
    }

    public Config setConfigDbName(String configDbName) {
        this.configDbName = configDbName;
        return this;
    }

    public String getConfigDbUri() {
        return configDbUri;
    }

    public Config setConfigDbUri(String configDbUri) {
        this.configDbUri = configDbUri;
        return this;
    }

    public String getTargetDbName() {
        return targetDbName;
    }

    public Config setTargetDbName(String targetDbName) {
        this.targetDbName = targetDbName;
        return this;
    }

    public String getTargetDbUri() {
        return targetDbUri;
    }

    public Config setTargetDbUri(String targetDbUri) {
        this.targetDbUri = targetDbUri;
        return this;
    }

    public boolean isAllowMultipleScriptExecutions() {
        return allowMultipleScriptExecutions;
    }

    public Config setAllowMultipleScriptExecutions(boolean allowMultipleScriptExecutions) {
        this.allowMultipleScriptExecutions = allowMultipleScriptExecutions;
        return this;
    }

    @Override
    public String toString() {
        return String.format("[ \n - configDB = %s/%s,\n - targetDB = %s/%s,\n - scriptsPath = %s,\n - multipleScriptExecs = %s \n]",
                configDbUri, configDbName, targetDbUri, targetDbName, scriptsPath, allowMultipleScriptExecutions);
    }
}
