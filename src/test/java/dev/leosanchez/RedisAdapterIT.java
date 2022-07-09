package dev.leosanchez;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


import dev.leosanchez.adapters.cache.RedisAdapter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(RedisAdapterIT.TestProfile.class)
public class RedisAdapterIT {
    
    @Inject
    RedisAdapter redisAdapter;

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

    @Test
    public void testSetValue() {
        redisAdapter.set("llaveA", "valorA");
        Optional<String> obtainedValue = redisAdapter.get("llaveA");
        Assertions.assertEquals("valorA", obtainedValue.get());
    }

    @Test
    public void testDeleteValue() {
        redisAdapter.set("llaveB", "valorB");
        redisAdapter.delete("llaveB");
        Optional<String> obtainedValue = redisAdapter.get("llaveB");
        Assertions.assertTrue(obtainedValue.isEmpty());
    }

    @Test
    public void getKeysByPrefix() {
        redisAdapter.set("prefijoA:llaveC", "valorC");
        redisAdapter.set("prefijoA:llaveD", "valorD");
        List<String> keys = redisAdapter.obtainKeysByPrefix("prefijoA:");
        Assertions.assertEquals(2, keys.size());
        Assertions.assertTrue(keys.contains("prefijoA:llaveC"));
        Assertions.assertTrue(keys.contains("prefijoA:llaveD"));
    }

    @Test
    public void testDeleteAllByPrefix() {
        redisAdapter.set("prefijoB:llaveE", "valorE");
        redisAdapter.set("prefijoB:llaveF", "valorF");
        redisAdapter.deleteAllByPrefix("prefijoB:");
        Optional<String> obtainedValue = redisAdapter.get("prefijoB:llaveE");
        Assertions.assertTrue(obtainedValue.isEmpty());
        obtainedValue = redisAdapter.get("prefijoB:llaveF");
        Assertions.assertTrue(obtainedValue.isEmpty());
    }

    @Test
    public void testCheckNotExistantKeys() {
        Assertions.assertFalse(redisAdapter.check("llaveG"));
    }

    @Test
    public void testExistantKey() {
        redisAdapter.set("llaveH", "valorH");
        Assertions.assertTrue(redisAdapter.check("llaveH"));
    }

    @Test
    public void testExpire() throws Exception {
        redisAdapter.set("llaveI", "valorI");
        redisAdapter.setExpire("llaveI", 5);
        Optional<String> obtainedValue = redisAdapter.get("llaveI");
        Assertions.assertTrue(obtainedValue.isPresent());
        Thread.sleep(5000);
        obtainedValue = redisAdapter.get("llaveI");
        Assertions.assertTrue(obtainedValue.isEmpty());
    }
}
