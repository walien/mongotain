package org.mongotain.components;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongotain.serialization.BsonJSR310Module;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;
import org.slf4j.Logger;

import java.net.UnknownHostException;

import static org.slf4j.LoggerFactory.getLogger;

public class DBConnector {

    private static final Logger logger = getLogger(DBConnector.class);

    private final String dbUri;
    private final String dbName;
    private Jongo jongo;
    private DB db;

    public DBConnector(String dbUri, String dbName) {
        this.dbUri = dbUri;
        this.dbName = dbName;
    }

    public DBConnector connect() {
        try {
            this.db = new MongoClient(new MongoClientURI(dbUri)).getDB(dbName);
            this.jongo = new Jongo(db, buildObjectMapper());
        } catch (UnknownHostException e) {
            logger.error(String.format("Error occured during connection to the mongo database " +
                    "(uri = %s, db = %s)", dbUri, dbName), e);
            throw new RuntimeException(e);
        }
        return this;
    }

    private static Mapper buildObjectMapper() {
        return new JacksonMapper.Builder()
                .registerModule(new BsonJSR310Module())
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    public MongoCollection getCollection(String name) {
        return jongo.getCollection(name);
    }

    public Object eval(String script) {
        return this.db.eval(script);
    }
}
