package dev.leosanchez;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.leosanchez.adapters.objectmapper.JacksonAdapter;
import dev.leosanchez.models.AuthenticationResponse;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class JacksonAdapterIT {
    @Inject
    JacksonAdapter jacksonAdapter;

    @Test
    public void testSerializeString() {
        String objectToMap = "Hola";
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("\"Hola\"", serializedObject);
    }

    @Test
    public void testDeserializeString() {
        String serializedObject = "\"Hola\"";
        String objectToMap = jacksonAdapter.deserializeObject(serializedObject, String.class);
        Assertions.assertEquals("Hola", objectToMap);
    }

    @Test
    public void testDeserializeDate() {
        Date now = new Date();
        String serializedObject = jacksonAdapter.serializeObject(now);
        Date objectToMap = jacksonAdapter.deserializeObject(serializedObject, Date.class);
        Assertions.assertEquals(now, objectToMap);
    }

    @Test
    public void testDeserializeUri() {
        URI uri = UriBuilder.fromUri("http://localhost:8080/").build();
        String serializedObject = jacksonAdapter.serializeObject(uri);
        URI objectToMap = jacksonAdapter.deserializeObject(serializedObject, URI.class);
        Assertions.assertEquals(uri, objectToMap);
    }

    @Test
    public void testSerializeInteger() {
        Integer objectToMap = 1;
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("1", serializedObject);
    }

    @Test
    public void testDeserializeInteger() {
        String serializedObject = "1";
        Integer objectToMap = jacksonAdapter.deserializeObject(serializedObject, Integer.class);
        Assertions.assertEquals(1, objectToMap);
    }

    @Test
    public void testSerializeBoolean() {
        Boolean objectToMap = true;
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("true", serializedObject);
    }

    @Test
    public void testDeserializeBoolean() {
        String serializedObject = "true";
        Boolean objectToMap = jacksonAdapter.deserializeObject(serializedObject, Boolean.class);
        Assertions.assertTrue(objectToMap);
    }

    @Test
    public void testSerializeList() {
        List<String> objectToMap = List.of("Hola", "Mundo");
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("[\"Hola\",\"Mundo\"]", serializedObject);
    }

    @Test
    public void testDeserializeList() {
        String serializedObject = "[\"Hola\",\"Mundo\"]";
        List<String> objectToMap = jacksonAdapter.deserializeList(serializedObject, String.class);
        Assertions.assertEquals(List.of("Hola", "Mundo"), objectToMap);
    }

    @Test
    public void testSerializeMap() {
        Map<String, String> objectToMap = Map.of("Hola", "Mundo");
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("{\"Hola\":\"Mundo\"}", serializedObject);
    }

    @Test
    public void testDeserializeMap() {
        String serializedObject = "{\"Hola\":\"Mundo\"}";
        Map<String, String> objectToMap = (Map<String, String>) jacksonAdapter.deserializeMap(serializedObject, String.class, String.class);
        Assertions.assertEquals(Map.of("Hola", "Mundo"), objectToMap);
    }

    @Test
    public void testDeserializeObject(){
        AuthenticationResponse objectToMap = AuthenticationResponse.generate("leonel");
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        AuthenticationResponse deserializedObject = jacksonAdapter.deserializeObject(serializedObject, AuthenticationResponse.class);
        Assertions.assertEquals(objectToMap.getToken(), deserializedObject.getToken());
        Assertions.assertEquals(objectToMap.getDateIssued(), deserializedObject.getDateIssued());
        Assertions.assertEquals(objectToMap.getUsername(), deserializedObject.getUsername());
    }

}
