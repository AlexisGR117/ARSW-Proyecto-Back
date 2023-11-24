package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class WildcardSerializer extends JsonSerializer<Wildcard> {

    @Override
    public void serialize(Wildcard wildcard, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", wildcard.getClass().getSimpleName());
        jsonGenerator.writeEndObject();
    }
}