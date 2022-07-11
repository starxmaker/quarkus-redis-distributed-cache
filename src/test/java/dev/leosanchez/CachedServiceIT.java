package dev.leosanchez;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import dev.leosanchez.interceptors.Cached;
import dev.leosanchez.interceptors.CachedInvalidate;
import dev.leosanchez.interceptors.CachedInvalidateAll;
import dev.leosanchez.interceptors.CachedKey;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(CachedServiceIT.TestProfile.class)
public class CachedServiceIT {

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
                        put("cache.cache-quicklyexpired.expiration", "2");
                    }
                };
    
            }
    }

    @Test
    public void testCheckCachedResponse(){
        String response = noArguments();
        String response2 = noArguments();
        Assertions.assertEquals(response, response2);
    }

    @Test
    public void testCachedResponseExpiration() throws Exception{
        String response = quicklyExpired();
        Thread.sleep(3000);
        String response2 = quicklyExpired();
        Assertions.assertNotEquals(response, response2);
    }

    @Test
    public void testCacheDifferentKeyValues(){
        String firstResponse = oneArgument("A.First");
        String secondResponse = oneArgument("A.Second");
        Assertions.assertNotEquals(firstResponse, secondResponse);
    }

    @Test
    public void testSingleCacheInvalidation() {
        String firstResponse = oneArgument("D.First");
        String secondResponse = oneArgument("D.Second");
        invalidateSingleCache("D.First");
        String lateFirstResponse = oneArgument("D.First");
        String lateSecondResponse = oneArgument("D.Second");
        Assertions.assertNotEquals(firstResponse, lateFirstResponse);
        Assertions.assertEquals(secondResponse, lateSecondResponse);
    }

    @Test
    public void testAllCacheInvalidation() {
        String firstResponse = oneArgument("D.First");
        String secondResponse = oneArgument("D.Second");
        invalidateAllCache();
        String lateFirstResponse = oneArgument("D.First");
        String lateSecondResponse = oneArgument("D.Second");
        Assertions.assertNotEquals(firstResponse, lateFirstResponse);
        Assertions.assertNotEquals(secondResponse, lateSecondResponse);
    }

    @Test
    public void testCacheDifferentParameterTypes() {
        String response = differentArgumentsInt(1);
        String response2 = differentArgumentsString("1");
        Assertions.assertNotEquals(response, response2);
    }

    @Test
    public void testCacheKey(){
        String firstResponse = twoArgumentWithOneCacheKey("B.First", "B.Second");
        String secondResponse = oneArgumentNoCacheKey("B.First");
        Assertions.assertEquals(firstResponse, secondResponse);
    }

    @Test
    public void testCacheDifferentOrder(){
        String firstResponse = twoArgumentWithOneCacheKey("C.First", "C.Second");
        String secondResponse = twoArgumentWithOneCacheKey("C.Second", "C.First");
        Assertions.assertNotEquals(firstResponse, secondResponse);
    }


    // dummy methods
    // Cache sin argumentos
    @Cached(cacheName =  "cache-noarguments")
    public String noArguments() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // Cache con expiración rápida
    @Cached(cacheName =  "cache-quicklyexpired")
    public String quicklyExpired() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // Caché con un solo argumento
    @Cached(cacheName =  "cache-oneargument")
    public String oneArgument(String argument) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @CachedInvalidate(cacheName = "cache-oneargument")
    public void invalidateSingleCache(String argument) {
    }

    @CachedInvalidateAll(cacheName = "cache-oneargument")
    public void invalidateAllCache() {
    }

    // Caché de métodos con parámetros de diferente tipo
    @Cached(cacheName = "cache-differentargumenttypes")
    public String differentArgumentsInt(Integer argument) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    @Cached(cacheName = "cache-differentargumenttypes")
    public String differentArgumentsString(String argument) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // Caché con clave especifica
    @Cached(cacheName = "cache-onecachekey")
    public String twoArgumentWithOneCacheKey(@CachedKey String argument, String secondArgument) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Cached(cacheName = "cache-onecachekey")
    public String oneArgumentNoCacheKey(String argument) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    // Caché de dos argumentos
    @Cached(cacheName = "cache-twoarguments")
    public String differentOrder(String argument, Integer argument2) {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
