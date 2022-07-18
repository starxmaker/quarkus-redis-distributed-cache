package dev.leosanchez.interceptors;

import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

@Interceptor
@Priority(10000)
@Cached(cacheName = "")
public class CachedInterceptor {

    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object checkCache(InvocationContext context) throws Exception {
        // retrieve the annotation to retrieve the cache name
        Cached cachedAnnotation = context.getMethod().getAnnotation(Cached.class);
        // generate the key based on the cache name, parameters and parameter annotations
        String generatedKey = cachedService.generateKey(cachedAnnotation.cacheName(), context.getParameters(),
                context.getMethod().getParameterAnnotations());
        LOG.info("Returning cached response");
        // look up for a saved response
        Optional<Object> cachedValue = cachedService.retrieveCachedResponse(generatedKey);
        if (cachedValue.isPresent()) {
            // return response if exists
            return cachedValue.get();
        } else {
            // continue the flow if not
            LOG.info("No cache found, generating");
            Object response = context.proceed();
            // retrieve the expirationTime
            Integer expirationTime = getExpirationTime(cachedAnnotation.cacheName());
            // save the generated response
            cachedService.saveCachedResponse(generatedKey, response, expirationTime);
            //return the response
            return response;
        }
    }

    private Integer getExpirationTime(String cacheName){
        String propertyKey = "cache." + cacheName + ".expiration";
        Optional<Integer> maybeCacheDuration = ConfigProvider.getConfig().getOptionalValue(propertyKey, Integer.class);
        if (maybeCacheDuration.isPresent()) {
            return maybeCacheDuration.get();
        } else {
            return 60 * 60; // 1 hour
        }
    }
}
