package dev.leosanchez;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import dev.leosanchez.resources.StockResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(StockResourceIT.TestProfile.class)
public class StockResourceIT {

    @Inject
    StockResource resource;

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

    private String checkProduct(String productName) {
        return given()
                .when()
                .get("/product/"+productName)
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    private void purchaseProduct(String productName, int quantity) {
        given()
                .when()
                .post("/product/purchase?product="+productName+"&quantity="+quantity)
                .then()
                .statusCode(200);
    }

    private void sendCompleteInvalidation(){ 
        given()
                .when()
                .get("/product/invalidate-all")
                .then()
                .statusCode(200);
    }


    @Test
    public void testCachedResponse() throws InterruptedException {
        String receivedPayload = checkProduct("APPLE");
        Thread.sleep(2000);
        String laterReceivedPayload = checkProduct("APPLE");
        Assertions.assertEquals(receivedPayload, laterReceivedPayload);
    }

    @Test
    public void testNotCachedResponse() throws InterruptedException {
        String receivedPayload = checkProduct("GRAPES");
        String secondReceivedPayload = checkProduct("BANANA");
        Assertions.assertNotEquals(receivedPayload, secondReceivedPayload);
    }

    @Test
    public void testSimpleInvalidation() throws InterruptedException {
        String receivedPayload = checkProduct("APPLE");
        purchaseProduct("APPLE", 2);
        String laterReceivedPayload = checkProduct("APPLE");
        Assertions.assertNotEquals(receivedPayload, laterReceivedPayload);
    }

    @Test
    public void testAllInvalidation() throws InterruptedException {
        String receivedPayload = checkProduct("BANANA");
        String secondReceivedPayload = checkProduct("GRAPES");
        sendCompleteInvalidation();
        String laterReceivedPayload = checkProduct("BANANA");
        String laterSecondReceivedPayload = checkProduct("GRAPES");
        Assertions.assertNotEquals(receivedPayload, laterReceivedPayload);
        Assertions.assertNotEquals(secondReceivedPayload, laterSecondReceivedPayload);
    }

}
