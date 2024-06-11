package br.com.samuel;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter implements JsonSerializer<Message> {
    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", message.getPayload().getClass().getName());
        jsonObject.add("payload", jsonSerializationContext.serialize(message.getPayload()));
        jsonObject.add("correlationId", jsonSerializationContext.serialize(message.getId()));
        return jsonObject;
    }
}
