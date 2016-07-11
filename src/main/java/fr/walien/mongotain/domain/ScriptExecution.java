package fr.walien.mongotain.domain;

import java.time.Instant;

public class ScriptExecution {

    public enum Status {
        SUCCESS, ERROR
    }

    private Instant timestamp;

    private Status status;

    private String output;

    public String getOutput() {
        return output;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ScriptExecution setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public ScriptExecution setStatus(final Status status) {
        this.status = status;
        return this;
    }

    public ScriptExecution setOutput(final String output) {
        this.output = output;
        return this;
    }
}
