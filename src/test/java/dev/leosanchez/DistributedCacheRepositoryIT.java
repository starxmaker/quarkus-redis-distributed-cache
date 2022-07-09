package dev.leosanchez;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import dev.leosanchez.repositories.DistributedCacheRepository;
import dev.leosanchez.resources.GreetingResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(DistributedCacheRepositoryIT.TestProfile.class)
public class DistributedCacheRepositoryIT {
    @Inject
    DistributedCacheRepository repository;

    @Inject
    GreetingResource resource;

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379);

    public static class TestProfile implements QuarkusTestProfile {
            @Override
            public Map<String, String> getConfigOverrides() {
                redis.start();
                String containerUrl = "http://" + redis.getHost() + ":" + redis.getFirstMappedPort();
                return new HashMap<String, String>() {
                    {
                        put("quarkus.redis.hosts", containerUrl);
                    }
                };
    
            }
    }

    @Test
    public void getValue(){
        repository.hola();
    }

    @Test
    public void getResponseValue() {
        resource.hello();
    }
}
