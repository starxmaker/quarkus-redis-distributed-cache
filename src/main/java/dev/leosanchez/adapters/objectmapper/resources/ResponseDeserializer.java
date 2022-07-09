package dev.leosanchez.adapters.objectmapper.resources;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ResponseDeserializer extends StdDeserializer<Response> {

    public ResponseDeserializer() {
        this(null);
    }
    public ResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Response deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException{
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = jp.getCodec().readTree(jp);
        Integer statusCode = node.get("status").asInt();
        ResponseBuilder responseBuilder = Response.status(statusCode);
        String entityClass = node.get("entityClass").asText();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Object entity = mapper.treeToValue(node.get("entity"), clazz);
        responseBuilder.entity(entity);
        if (node.get("cookies") != null && !node.get("cookies").isNull()) {
            List<NewCookie> cookies = new ArrayList<>();
            for (JsonNode cookieNode : node.get("cookies")) {
                cookies.add(mapper.treeToValue(cookieNode, NewCookie.class));
            }
            if (cookies.size() > 0) {
                NewCookie[] cookiesArray = cookies.toArray(new NewCookie[cookies.size()]);
                responseBuilder.cookie(cookiesArray);
            }
        }
        if (node.get("headers") != null && !node.get("headers").isNull()) {
            Map<String, Object[]> rawHeaders = mapper.readValue(node.get("headers").toString(), new TypeReference<Map<String, Object[]>>() {});
            Map<String,Object> filteredHeaders = new HashMap<String,Object>();
            for (Map.Entry<String, Object[]> entry : rawHeaders.entrySet()) {
                if (!entry.getKey().equals("Set-Cookie")){
                    filteredHeaders.put(entry.getKey(), entry.getValue().length == 1 ?  entry.getValue()[0] : entry.getValue());
                }
                
            }
            for ( Map.Entry<String, Object> entry : filteredHeaders.entrySet()) {
                responseBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        if (node.get("allowedMethods") != null && !node.get("allowedMethods").isNull() ) {
            String[] allowedMethods = mapper.treeToValue(node.get("allowedMethods"), String[].class);
            if (allowedMethods.length > 0) {
                responseBuilder.allow(allowedMethods);
            }
        }

        if (node.get("location") != null && !node.get("location").isNull()) {
            String location = node.get("location").asText();
            responseBuilder.location(URI.create(location));
        }
        Response builtResponse = responseBuilder.build();

        System.out.println(mapper.writeValueAsString(builtResponse));
        return builtResponse;
    }
}
