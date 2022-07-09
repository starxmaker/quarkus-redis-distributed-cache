package dev.leosanchez.adapters.objectmapper.resources;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.core.NewCookie;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class NewCookieDeserializer extends StdDeserializer<NewCookie> {

    public NewCookieDeserializer() {
        this(null);
    }
    public NewCookieDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public NewCookie deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException{
        JsonNode node = jp.getCodec().readTree(jp);
        String name = node.get("name").asText();
        String value = node.get("value").asText();
        String path = node.get("path").asText();
        String domain = node.get("domain").asText();
        Integer version = node.get("version").asInt();
        String comment = node.get("comment").asText();
        Integer maxAge = node.get("maxAge").asInt();
        Date expiry = jp.getCodec().treeToValue(node.get("expiry"), Date.class);
        Boolean secure = node.get("secure").asBoolean();
        Boolean httpOnly = node.get("httpOnly").asBoolean();
        NewCookie cookie =  new NewCookie(name, value, path, domain, version, comment, maxAge, expiry, secure, httpOnly);
        return cookie;
    }
}