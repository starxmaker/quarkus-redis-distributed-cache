package dev.leosanchez.adapters.objectmapper;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.quarkus.arc.lookup.LookupIfProperty;

@ApplicationScoped
@LookupIfProperty(name = "objectmapper.provider", stringValue = "jackson")
public class JacksonAdapter implements IObjectMapperAdapter {
    private ObjectMapper objectMapper;

    public JacksonAdapter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public String serializeObject(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserializeObject(String serializedObject, Class<T> clazz) {
        try {
            return objectMapper.readValue(serializedObject, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> deserializeList(String serializedObject, Class<T> clazz) {
        try {
            return objectMapper.readValue(serializedObject, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <K, V> Map<K, V> deserializeMap(String serializedObject, Class<K> keyClass, Class<V> valueClass) {
        try {
            return objectMapper.readValue(serializedObject, TypeFactory.defaultInstance().constructMapType(Map.class, keyClass, valueClass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
