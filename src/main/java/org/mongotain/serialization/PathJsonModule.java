package org.mongotain.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.undercouch.bson4jackson.BsonGenerator;
import de.undercouch.bson4jackson.serializers.BsonSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathJsonModule extends SimpleModule {

    public PathJsonModule() {

        super("PathJsonModule");

        addSerializer(Path.class, new BsonSerializer<Path>() {
            @Override
            public void serialize(Path path, BsonGenerator bsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                if (path == null) {
                    serializerProvider.defaultSerializeNull(bsonGenerator);
                } else {
                    bsonGenerator.writeString(path.toString());
                }
            }
        });

        addDeserializer(Path.class, new JsonDeserializer<Path>() {
            @Override
            public Path deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                String path = (String) jp.getEmbeddedObject();
                return Paths.get(path);
            }
        });
    }
}