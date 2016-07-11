package fr.walien.mongotain.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.undercouch.bson4jackson.BsonGenerator;
import de.undercouch.bson4jackson.serializers.BsonSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public class BsonJSR310Module extends SimpleModule {

    public BsonJSR310Module() {

        super("BsonJSR310Module");

        addSerializer(Instant.class, new BsonSerializer<Instant>() {
            @Override
            public void serialize(Instant date, BsonGenerator bsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                if (date == null) {
                    serializerProvider.defaultSerializeNull(bsonGenerator);
                } else {
                    bsonGenerator.writeDateTime(new Date(date.toEpochMilli()));
                }
            }
        });

        addDeserializer(Instant.class, new JsonDeserializer<Instant>() {
            @Override
            public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                Date date = (Date) jp.getEmbeddedObject();
                return Instant.ofEpochMilli(date.getTime());
            }
        });

        addSerializer(LocalDate.class, new BsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate date, BsonGenerator bsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                if (date == null) {
                    serializerProvider.defaultSerializeNull(bsonGenerator);
                } else {
                    bsonGenerator.writeDateTime(new Date(date.toEpochDay()));
                }
            }
        });

        addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                Date date = (Date) jp.getEmbeddedObject();
                return LocalDate.ofEpochDay(date.getTime());
            }
        });
    }
}