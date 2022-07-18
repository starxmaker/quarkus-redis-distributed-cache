package dev.leosanchez.interceptors;

import java.lang.annotation.Annotation;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import dev.leosanchez.adapters.cache.ICacheAdapter;
import dev.leosanchez.utils.ResponseDeserializer;
import dev.leosanchez.utils.ResponseSerializer;

@ApplicationScoped
public class CachedService {

    @Inject
    ICacheAdapter cacheAdapter;

    ObjectMapper objectMapper;

    public CachedService(){
        objectMapper = new ObjectMapper();
        // enable default typing
        // NOTE: never enable this configuration (Basetype: Object) to deserialize json data from external sources,
        // because someone could send a json string with an exploitable type which could lead to remote
        // code execution. We are enabling it because we will deserialize only json data serialized by us and it is not
        // accesible for external sources.
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
            .builder()
            .allowIfBaseType(Object.class)
            .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Response.class, new ResponseSerializer());
        module.addDeserializer(Response.class, new ResponseDeserializer());
        objectMapper.registerModule(module);
        
    }

    public void saveCachedResponse(String generatedKey, Object response, Integer expirationTime) throws JsonProcessingException{
        String serializedObject = objectMapper.writeValueAsString(response);
        cacheAdapter.set(generatedKey, serializedObject);
        cacheAdapter.setExpire(generatedKey, expirationTime);
    }
    
    public Optional<Object> retrieveCachedResponse(String key) throws JsonMappingException, JsonProcessingException{
        if (exists(key)){
            Optional<String> serializedObject = cacheAdapter.get(key);
            if (serializedObject.isPresent()) {
                Object response = deserialize(serializedObject.get());
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
                concatenatedValues += objectMapper.writeValueAsString(parameter);
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

    private Object deserialize(String serializedObject) throws JsonProcessingException, JsonMappingException {
        return objectMapper.readValue(serializedObject, Object.class);
    }
    
}
