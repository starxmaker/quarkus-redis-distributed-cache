package dev.leosanchez.interceptors;

import java.lang.annotation.Annotation;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.config.ConfigProvider;

import dev.leosanchez.adapters.cache.ICacheAdapter;
import dev.leosanchez.adapters.objectmapper.IObjectMapperAdapter;

@ApplicationScoped
public class CachedService {

    @Inject
    ICacheAdapter cacheAdapter;

    @Inject
    IObjectMapperAdapter objectMapperAdapter;


    public void removeSingleEntry(String key) {
        cacheAdapter.delete(key);
    }

    public void removeAll(String cacheName) {
        cacheAdapter.deleteAllByPrefix(cacheName);
    }
    public Boolean exists (String key) {
        return cacheAdapter.check(key);
    }
    public Optional<String> retrieveCachedResponse(String key) {
        return cacheAdapter.get(key);
    }
    
    public String generateKey(String cacheName, String compositeKey) {
        return cacheName + ":" + compositeKey;
    }
    public void savingCachedValue(String generatedKey, Object response, Integer expirationTime) {
        String serializedObject = objectMapperAdapter.serializeObject(response);
        cacheAdapter.set(generatedKey, serializedObject);
        cacheAdapter.setExpire(generatedKey, expirationTime);
    }
    public Object deserialize(String serializedObject,  InvocationContext context) {
        Class<?> responseClass = context.getMethod().getReturnType();
        if (responseClass.equals(List.class)) {
            Class<?> subClass = context.getMethod().getParameterTypes()[0];
            return objectMapperAdapter.deserializeList(serializedObject, subClass);
        } else if (responseClass.equals(Map.class)) {
            Class<?> keyClass = context.getMethod().getParameterTypes()[0];
            Class<?> valueClass = context.getMethod().getParameterTypes()[1];
            return objectMapperAdapter.deserializeMap(serializedObject, keyClass, valueClass);
        } else {
            return objectMapperAdapter.deserializeObject(serializedObject, responseClass);
        }
    }

    public Integer getExpirationTime(String cacheName){
        String propertyKey = "cache." + cacheName + ".expiration";
        Optional<Integer> maybeCacheDuration = ConfigProvider.getConfig().getOptionalValue(propertyKey, Integer.class);
        if (maybeCacheDuration.isPresent()) {
            return maybeCacheDuration.get();
        } else {
            return 60 * 60; // 1 hour
        }
    }

    public Object[] filteredParameters (Object[] originalParameters, Annotation[][] parameterAnnotations) {
        List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < originalParameters.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof CachedKey) {
                    parameters.add(originalParameters[i]);
                }
            }
        }
        if (parameters.isEmpty()) {
            return originalParameters;
        } else {
            return parameters.toArray();
        }
    }

    public String generateCompositeKey(Object[] parameters) throws Exception {
        if (parameters.length == 0) {
            System.out.println("No parameters");
            return "0";
        } else {
            String concatenatedValues = "";
            for (Object parameter : parameters) {
                concatenatedValues += objectMapperAdapter.serializeObject(parameter);
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(concatenatedValues.getBytes("utf8"));
            String sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
            System.out.println("SHA1: " + sha1);
            return sha1;
        }
    }
}
