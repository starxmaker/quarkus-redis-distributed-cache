package dev.leosanchez.adapters.objectmapper;

import java.util.List;
import java.util.Map;

interface IObjectMapperAdapter {
    String serializeObject(Object object);
    <T> T deserializeObject(String serializedObject, Class<T> clazz);
    <T> List<T> deserializeList(String serializedObject, Class<T> clazz);
    <K,V> Map<K,V> deserializeMap(String serializedObject, Class<K> keyClass, Class<V> valueClass);
}
