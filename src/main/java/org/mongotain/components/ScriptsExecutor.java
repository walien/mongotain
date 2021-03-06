package org.mongotain.components;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.mongotain.domain.Script;
import org.mongotain.domain.ScriptExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This component discovers all mongodb scripts that must run
 */

public class ScriptsExecutor {

    private static final HashFunction MD5 = Hashing.md5();
    private static final Logger logger = LoggerFactory.getLogger(ScriptsExecutor.class);

    private final DBConnector targetDBConnector;
    private final boolean allowMultipleScriptExecutions;

    public ScriptsExecutor(DBConnector targetDBConnector, boolean allowMultipleScriptExecutions) {
        this.targetDBConnector = targetDBConnector;
        this.allowMultipleScriptExecutions = allowMultipleScriptExecutions;
    }

    private boolean hasBeenExecuted(Script script) {
        return !script.getExecutions().isEmpty();
    }

    private boolean hasChanged(Script script) {
        String newHash = MD5.newHasher().putString(script.getContent(), Charsets.UTF_8).hash().toString();
        String oldHash = script.getMd5();
        script.setMd5(newHash);
        return oldHash != null && !newHash.equals(oldHash);
    }

    private Script execute(Script script) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            String scriptContent = new String(Files.readAllBytes(script.getPath()), StandardCharsets.UTF_8);
            targetDBConnector.eval(scriptContent);
            logger.info("[EXECUTION] - {} - execution done. Took {} ms", script.getPath().toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            markAsExecuted(script);
            return script;
        } catch (IOException e) {
            logger.error("[EXECUTION] - unable to read the content of the script " + script.getPath().toString(), e);
            markAsExecutedWithErrors(script, e.getMessage());
        }
        return null;
    }

    private void markAsExecuted(Script script) {
        script.getExecutions().add(new ScriptExecution().setTimestamp(Instant.now()).setStatus(ScriptExecution.Status.SUCCESS));
    }

    private void markAsExecutedWithErrors(Script script, String message) {
        script.getExecutions().add(new ScriptExecution().setStatus(ScriptExecution.Status.ERROR).setOutput(message));
    }

    private Script loadContent(Script script) {
        try {
            String scriptContent = new String(Files.readAllBytes(script.getPath()), StandardCharsets.UTF_8);
            return script.setContent(scriptContent);
        } catch (IOException e) {
            logger.error("Unable to read the content of the script " + script.getPath().toString(), e);
        }
        return script;
    }

    public List<Script> execute(List<Script> scripts) {
        List<Script> scriptsToExecute = scripts.stream()
                .map(this::loadContent)
                .filter(script -> {
                    if (hasChanged(script)) {
                        logger.warn("[EXECUTION] - Content of the script {} has changed. The script will not be re-executed", script.getPath());
                        return false;
                    }
                    return true;
                })
                .filter(script -> {
                    if (hasBeenExecuted(script) && !allowMultipleScriptExecutions) {
                        logger.info("[EXECUTION] - Script {} already executed and multi script executions feature is not enabled", script.getPath());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        scriptsToExecute.forEach(this::execute);
        return scriptsToExecute;
    }
}
