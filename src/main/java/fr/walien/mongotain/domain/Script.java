package fr.walien.mongotain.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Script {

    public static final String COLLECTION = "scripts";

    @MongoId
    @MongoObjectId
    private String key;

    private Path path;

    private List<ScriptExecution> executions = new ArrayList<>();

    private byte[] md5;

    @JsonIgnore
    private String content;

    public String getKey() {
        return key;
    }

    public Path getPath() {
        return path;
    }

    public List<ScriptExecution> getExecutions() {
        return executions;
    }

    public byte[] getMd5() {
        return md5;
    }

    public String getContent() {
        return content;
    }

    public Script setKey(final String key) {
        this.key = key;
        return this;
    }

    public Script setPath(final Path path) {
        this.path = path;
        return this;
    }

    public Script setExecutions(final List<ScriptExecution> executions) {
        this.executions = executions;
        return this;
    }

    public Script setMd5(final byte[] md5) {
        this.md5 = md5;
        return this;
    }

    public Script setContent(final String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Script script = (Script) o;

        return path != null ? path.equals(script.path) : script.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
