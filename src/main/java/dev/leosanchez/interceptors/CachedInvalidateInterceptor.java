package dev.leosanchez.interceptors;

import java.lang.annotation.Annotation;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Priority(1)
@CachedInvalidate(cacheName = "")
public class CachedInvalidateInterceptor {

    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object invalidateCache(InvocationContext context) throws Exception {
        CachedInvalidate cachedAnnotation = context.getMethod().getAnnotation(CachedInvalidate.class);
        String cacheName = cachedAnnotation.cacheName();
        Annotation[][] parameterAnnotations = context.getMethod().getParameterAnnotations();
        Object[] originalParameters = context.getParameters();
        Object[] filteredParameters = cachedService.filteredParameters(originalParameters, parameterAnnotations);
        String compositeKey = cachedService.generateCompositeKey(filteredParameters);
        String generatedKey = cachedService.generateKey(cacheName, compositeKey);
        Boolean exists = cachedService.exists(generatedKey);
        if (exists) {
            LOG.info("Invalidating cache");
            cachedService.removeSingleEntry(generatedKey);
        }
        return context.proceed();
    }
}
