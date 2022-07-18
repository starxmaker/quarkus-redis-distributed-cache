package dev.leosanchez;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import dev.leosanchez.dto.StockResponse;
import dev.leosanchez.utils.ResponseDeserializer;
import dev.leosanchez.utils.ResponseSerializer;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ResponseSerializationTest {
    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void beforeAll() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Response.class, new ResponseSerializer());
        module.addDeserializer(Response.class, new ResponseDeserializer());
        mapper.registerModule(module);
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
        .builder()
        .allowIfBaseType(Object.class)
        .build();
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING);
    }

    @Test
    public void testBasicResponseSerialization() throws Exception {
        Response response = Response.status(200).build();
        String className = "javax.ws.rs.core.Response";
        String json = mapper.writeValueAsString(response);
        JsonArray jsonArray = new JsonArray(json);
        Assertions.assertEquals(className, jsonArray.getString(0));
        Assertions.assertEquals(200, jsonArray.getJsonObject(1).getInteger("status"));
    }

    @Test
    public void testPrimitiveEntitySerialization() throws Exception {
        Response response = Response.status(200).entity("Hello World").build();
        String json = mapper.writeValueAsString(response);
        JsonArray jsonArray = new JsonArray(json);
        Assertions.assertEquals("Hello World", jsonArray.getJsonObject(1).getString("entity"));
    }
    
    @Test
    public void testNonPrimitiveEntitySerialization() throws Exception {
        StockResponse entity = new StockResponse("product1",10);
        Response response = Response.status(200).entity(entity).build();
        String json = mapper.writeValueAsString(response);
        JsonArray jsonArray = new JsonArray(json);
        JsonArray entityArray = jsonArray.getJsonObject(1).getJsonArray("entity");
        JsonObject entityJson = entityArray.getJsonObject(1);
        Assertions.assertEquals(entity.getClass().getName(), entityArray.getString(0));
        Assertions.assertEquals(entity.getProduct(), entityJson.getString("product"));
        Assertions.assertEquals(entity.getAvailableStock(), entityJson.getInteger("availableStock"));
    }

    @Test
    public void testBasicResponseDeserialization() throws Exception {
        Response response = Response.status(400).build();
        String json = mapper.writeValueAsString(response);
        Response deserializedResponse = (Response) mapper.readValue(json, Object.class);
        Assertions.assertEquals(400, deserializedResponse.getStatus());
    }

    @Test
    public void testPrivimiteEntityResponseDeserialization() throws Exception {
        String entity = "Hello World";
        Response response = Response.status(400).entity(entity).build();
        String json = mapper.writeValueAsString(response);
        Response deserializedResponse = (Response) mapper.readValue(json, Object.class);
        Assertions.assertEquals(400, deserializedResponse.getStatus());
        Assertions.assertEquals(entity, deserializedResponse.getEntity());
    }
    

    @Test
    public void testNonPrimitiveEntityResponseDeserialization() throws Exception {
        StockResponse entity = new StockResponse("product1",10);
        Response response = Response.status(400).entity(entity).build();
        String json = mapper.writeValueAsString(response);
        Response deserializedResponse = (Response) mapper.readValue(json, Object.class);
        Assertions.assertEquals(400, deserializedResponse.getStatus());
        StockResponse deserializedEntity = (StockResponse) deserializedResponse.getEntity();
        Assertions.assertEquals(entity.getProduct(), deserializedEntity.getProduct());
        Assertions.assertEquals(entity.getAvailableStock(), deserializedEntity.getAvailableStock());
        Assertions.assertEquals(entity.getLastUpdate(), deserializedEntity.getLastUpdate());
    }


}
