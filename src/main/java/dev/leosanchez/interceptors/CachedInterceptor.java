package dev.leosanchez.interceptors;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Priority(1)
@Cached(cacheName = "")
public class CachedInterceptor {

    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object checkCache(InvocationContext context) throws Exception {
        Cached cachedAnnotation = context.getMethod().getAnnotation(Cached.class);
        String cacheName = cachedAnnotation.cacheName();
        Annotation[][] parameterAnnotations = context.getMethod().getParameterAnnotations();
        Object[] originalParameters = context.getParameters();
        Object[] filteredParameters = cachedService.filteredParameters(originalParameters, parameterAnnotations);
        String compositeKey = cachedService.generateCompositeKey(filteredParameters);
        String generatedKey = cachedService.generateKey(cacheName, compositeKey);
        Boolean exists = cachedService.exists(generatedKey);
        if (exists) {
            LOG.info("Returning cached response");
            Optional<String> cachedValue = cachedService.retrieveCachedResponse(generatedKey);
            if (cachedValue.isPresent()) {
                return cachedService.deserialize(cachedValue.get(), context);
            }
        }
        LOG.info("No cache found, generating");
        Object response =  context.proceed();
        Integer expirationTime = cachedService.getExpirationTime(cacheName);
        cachedService.savingCachedValue(generatedKey, response, expirationTime);
        return response;
    }
}
