package dev.leosanchez.adapters.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;

@ApplicationScoped
@LookupIfProperty(name = "cache.provider", stringValue = "redis")
public class RedisAdapter implements ICacheAdapter {
    @Inject
    RedisClient redisClient;

    @Override
    public void set(String key, String value) {
        redisClient.set(List.of(key, value));
        
    }

    @Override
    public Optional<String> get(String key) {
        Response response = redisClient.get(key);
        if(Objects.isNull(response)) {
            return Optional.empty();
        } else {
            return Optional.of(response.toString());
        }
    }

    @Override
	public List<String> obtainKeysByPrefix(String prefix) {
		List<String> keys = new ArrayList<>();
        Response response = redisClient.keys(prefix+"*");
        if(Objects.isNull(response)) {
            return keys;
        } else {
            String[] keysArray = response.toString()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            for (String key : keysArray) {
                keys.add(key.trim());
            }
            return keys;
        }
	}

    @Override
    public void delete(String key) {
        redisClient.del(List.of(key));
    }

    @Override
    public void deleteAllByPrefix(String prefix) {
        List<String> keys = obtainKeysByPrefix(prefix);
        redisClient.del(keys);
    }

    @Override
    public void setExpire(String key, long seconds) {
        redisClient.expire(key, Long.toString(seconds));
    }

    @Override
    public boolean check(String key) {
        Response response = redisClient.exists(List.of(key));
        return response.toString().equals("1");
    }
    
}
