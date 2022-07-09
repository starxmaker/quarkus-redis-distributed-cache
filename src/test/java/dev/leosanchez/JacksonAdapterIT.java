package dev.leosanchez;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.leosanchez.adapters.objectmapper.JacksonAdapter;
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
    public void testSerializeObject() {
        DummyClass objectToMap = new DummyClass("Hola", 1, List.of("Mundo"));
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        Assertions.assertEquals("{\"name\":\"Hola\",\"age\":1,\"hobbies\":[\"Mundo\"]}", serializedObject);
    }

    @Test
    public void testDeserializeObject(){
        DummyClass objectToMap = new DummyClass("Hola", 1, List.of("Mundo"));
        String serializedObject = jacksonAdapter.serializeObject(objectToMap);
        DummyClass deserializedObject = jacksonAdapter.deserializeObject(serializedObject, DummyClass.class);
        Assertions.assertEquals(objectToMap.getName(), deserializedObject.getName());
        Assertions.assertEquals(objectToMap.getAge(), deserializedObject.getAge());
        Assertions.assertEquals(objectToMap.getHobbies(), deserializedObject.getHobbies());
    }

    @Test
    public void testDeserializeResponse(){
        NewCookie cookie = new NewCookie("name", "value");
        Date now = new Date();
        String[] values = {"value1", "value2"};
        Response response = Response
        .ok("Hi")
        .cookie(cookie)
        .header("hola", "chao")
        .header("hol2a", values)
        .allow("GET", "POST")
        .expires(now)
        .location(URI.create("http://localhost:8080/hello"))
        .encoding("UTF-8")
        .language("es")
        .lastModified(now)
        .build();
        String serializedObject = jacksonAdapter.serializeObject(response);

        System.out.println(serializedObject);
        Response deserializedObject = jacksonAdapter.deserializeObject(serializedObject, Response.class);
        Assertions.assertEquals(response.getStatus(), deserializedObject.getStatus());
    }

}