package edu.eci.arsw.paintit.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PlayerSerializer extends JsonSerializer<Player> {

    @Override
    public void serialize(Player player, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("score", player.getScore().get());
        jsonGenerator.writeStringField("name", player.getName());
        jsonGenerator.writeNumberField("x", player.getX());
        jsonGenerator.writeNumberField("y", player.getY());
        jsonGenerator.writeObjectFieldStart("color");
        jsonGenerator.writeNumberField("red", player.getColor().getRed());
        jsonGenerator.writeNumberField("green", player.getColor().getGreen());
        jsonGenerator.writeNumberField("blue", player.getColor().getBlue());
        jsonGenerator.writeNumberField("alpha", player.getColor().getAlpha());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }

}
