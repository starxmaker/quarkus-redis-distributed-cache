package dev.leosanchez.repositories;

import javax.enterprise.context.ApplicationScoped;

import dev.leosanchez.interceptors.Cached;

@ApplicationScoped
public class DistributedCacheRepository {
    
    @Cached(cacheName =  "cache-hola")
    public String hola() {
        return "hola";
    }
}
