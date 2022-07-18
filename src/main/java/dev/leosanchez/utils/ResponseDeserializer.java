package dev.leosanchez.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


public class ResponseDeserializer extends StdDeserializer<Response> { 

    public ResponseDeserializer() { 
        this(null); 
    } 

    public ResponseDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public Response deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        // we define a map where we will store the values of the JSON object
        Map<String, Object> map = new HashMap<>();
        // we analize each token of the object until we reach the end object token
        while(!jp.getCurrentToken().equals(JsonToken.END_OBJECT)) {
            // if the current token is the start of the object, there is nothing to read, so we continue 
            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
                jp.nextToken();
            }
            // if the current token is a field name, we read this key, and the we read the value
            if (jp.currentToken() == JsonToken.FIELD_NAME) {
                // we extract the key
                String fieldName = jp.currentName();
                // we move to the following token
                jp.nextToken();
                // we extract the value as object (we delegate the parsing to Jackson)
                Object value = jp.readValueAs(Object.class);
                // we put the retrieved data to our map
                map.put(fieldName, value);
            }
            // we move to the next token
            jp.nextToken();
        }
        // we built our response from the map
        // status - compulsory
        Integer status = (Integer) map.get("status");
        ResponseBuilder responseBuilder = Response.status(status);
        // entity - optional
        if (map.containsKey("entity") && Objects.nonNull(map.get("entity"))) {
            Object entity = map.get("entity");
            responseBuilder.entity(entity);
        }
        // we build the response and send it back
        return responseBuilder.build();
    }
}