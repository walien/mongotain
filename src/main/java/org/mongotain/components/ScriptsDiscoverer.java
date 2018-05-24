package org.mongotain.components;

import org.mongotain.domain.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This component discovers all mongodb scripts that must run
 */

public class ScriptsDiscoverer {

    private static final Logger logger = LoggerFactory.getLogger(ScriptsDiscoverer.class);

    private final Path scriptsPath;
    private final DBConnector dbConnector;
    private final ScriptsSorter sorter;

    public ScriptsDiscoverer(Path scriptsPath, DBConnector dbConnector) {
        this.scriptsPath = scriptsPath;
        this.dbConnector = dbConnector;
        this.sorter = new ScriptsSorter();
    }

    private Optional<Script> findScriptByPath(Path path) {
        return Optional.ofNullable(dbConnector.getCollection(Script.COLLECTION)
                .findOne("{ path: # }", path.toAbsolutePath().toString())
                .as(Script.class));
    }

    public List<Script> discover() {
        try {
            List<Script> discoveredScripts = StreamSupport.stream(Files.newDirectoryStream(scriptsPath).spliterator(), false)
                    .map(scriptPath -> findScriptByPath(scriptPath).orElse(new Script().setPath(scriptPath)))
                    .sorted(sorter::sortByName)
                    .collect(Collectors.toList());
            logger.info("[DISCOVER] - {} scripts found in {}", discoveredScripts.size(), scriptsPath.toString());
            return discoveredScripts;
        } catch (IOException e) {
            logger.error("Unable to read the content of the scripts directory (provided " +
                    "path = " + scriptsPath.toString() + ")", e);
            throw new RuntimeException(e);
        }
    }
}
