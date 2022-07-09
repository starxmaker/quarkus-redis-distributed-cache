package dev.leosanchez.adapters.cache;

import java.util.List;
import java.util.Optional;

interface ICacheAdapter {
    void set(String key, String value);
    Optional<String> get(String key);
    void delete(String key);
    void deleteAllByPrefix(String prefix);
    void setExpire(String key, long seconds);
    boolean check(String key);
    List<String> obtainKeysByPrefix(String prefix);
}