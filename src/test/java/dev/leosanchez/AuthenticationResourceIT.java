package dev.leosanchez;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import dev.leosanchez.resources.AuthenticationResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.vertx.core.json.JsonObject;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(AuthenticationResourceIT.TestProfile.class)
public class AuthenticationResourceIT {

    @Inject
    AuthenticationResource resource;

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);

    public static class TestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            redis.start();
            String containerUrl = "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort();
            return new HashMap<String, String>() {
                {
                    put("quarkus.redis.hosts", containerUrl);
                }
            };

        }
    }

    private String sendAuthenticationRequest(String username, String password) {
        JsonObject body = new JsonObject();
        body.put("username", username);
        body.put("password", password);
        return given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.encode())
                .post("/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    private void sendSimpleInvalidationRequest(String username, String password) {
        JsonObject body = new JsonObject();
        body.put("username", username);
        body.put("password", password);
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body.encode())
                .post("/authenticate/invalidate")
                .then()
                .statusCode(200);
    }

    private void sendCompleteInvalidation(){ 
        given()
                .when()
                .get("/authenticate/invalidate-all")
                .then()
                .statusCode(200);
    }


    @Test
    public void testCachedResponse() throws InterruptedException {
        String receivedPayload = sendAuthenticationRequest("leonel", "sanchez");
        String laterReceivedPayload = sendAuthenticationRequest("leonel", "sanchez");
        Assertions.assertEquals(receivedPayload, laterReceivedPayload);
    }

    @Test
    public void testNotCachedResponse() throws InterruptedException {
        String receivedPayload = sendAuthenticationRequest("carlos", "sanchez");
        String secondReceivedPayload = sendAuthenticationRequest("fernando", "sanchez");
        Assertions.assertNotEquals(receivedPayload, secondReceivedPayload);
    }

    @Test
    public void testSimpleInvalidation() throws InterruptedException {
        String receivedPayload = sendAuthenticationRequest("daniela", "sanchez");
        sendSimpleInvalidationRequest("daniela", "sanchez");
        String laterReceivedPayload = sendAuthenticationRequest("daniela", "sanchez");
        Assertions.assertNotEquals(receivedPayload, laterReceivedPayload);
    }

    @Test
    public void testAllInvalidation() throws InterruptedException {
        String receivedPayload = sendAuthenticationRequest("francisco", "sanchez");
        String secondReceivedPayload = sendAuthenticationRequest("juan", "sanchez");
        sendCompleteInvalidation();
        String laterReceivedPayload = sendAuthenticationRequest("francisco", "sanchez");
        String laterSecondReceivedPayload = sendAuthenticationRequest("juan", "sanchez");
        Assertions.assertNotEquals(receivedPayload, laterReceivedPayload);
        Assertions.assertNotEquals(secondReceivedPayload, laterSecondReceivedPayload);
    }

}
