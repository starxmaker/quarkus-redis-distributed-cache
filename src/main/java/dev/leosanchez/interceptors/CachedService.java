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

import dev.leosanchez.adapters.cache.ICacheAdapter;
import dev.leosanchez.adapters.objectmapper.IObjectMapperAdapter;

@ApplicationScoped
public class CachedService {

    @Inject
    ICacheAdapter cacheAdapter;

    @Inject
    IObjectMapperAdapter objectMapperAdapter;

    public void saveCachedResponse(String generatedKey, Object response, Integer expirationTime) {
        String serializedObject = objectMapperAdapter.serializeObject(response);
        cacheAdapter.set(generatedKey, serializedObject);
        cacheAdapter.setExpire(generatedKey, expirationTime);
    }
    public Optional<Object> retrieveCachedResponse(String key, Class<?> returnType, Class<?>[] parameterTypes) {
        if (exists(key)){
            Optional<String> serializedObject = cacheAdapter.get(key);
            if (serializedObject.isPresent()) {
                Object response = deserialize(serializedObject.get(), returnType, parameterTypes);
                return Optional.of(response); 
            }
        }
        return Optional.empty();
    }
    
    public String generateKey(String cacheName, Object[] parameters, Annotation[][] annotations) throws Exception {
        Object[] filteredParameters = filterParameters(parameters, annotations);
        String compositeKey = generateCompositeKey(filteredParameters);
        return cacheName + ":" + compositeKey;
    }

    public Boolean exists (String key) {
        return cacheAdapter.check(key);
    }

    public void removeSingleEntry(String key) {
        cacheAdapter.delete(key);
    }
    public void removeAll(String cacheName) {
        cacheAdapter.deleteAllByPrefix(cacheName);
    }
    
    private String generateCompositeKey(Object[] parameters) throws Exception {
        if (parameters.length == 0) {
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
            return sha1;
        }
    }

    private Object[] filterParameters (Object[] originalParameters, Annotation[][] parameterAnnotations) {
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

    private Object deserialize(String serializedObject, Class<?> returnType, Class<?>[] parameterTypes) {
        if (returnType.equals(List.class)) {
            Class<?> subClass = parameterTypes[0];
            return objectMapperAdapter.deserializeList(serializedObject, subClass);
        } else if (returnType.equals(Map.class)) {
            Class<?> keyClass =  parameterTypes[0];
            Class<?> valueClass = parameterTypes[1];
            return objectMapperAdapter.deserializeMap(serializedObject, keyClass, valueClass);
        } else {
            return objectMapperAdapter.deserializeObject(serializedObject, returnType);
        }
    }
    
}
