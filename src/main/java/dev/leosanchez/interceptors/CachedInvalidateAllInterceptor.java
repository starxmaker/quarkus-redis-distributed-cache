package dev.leosanchez.interceptors;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Priority(1)
@CachedInvalidateAll(cacheName = "")
public class CachedInvalidateAllInterceptor {

    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object invalidateCache(InvocationContext context) throws Exception {
        CachedInvalidateAll cachedAnnotation = context.getMethod().getAnnotation(CachedInvalidateAll.class);
        String cacheName = cachedAnnotation.cacheName();
        cachedService.removeAll(cacheName);
        LOG.info("all cache entries removed for cache: " + cacheName);
        return context.proceed();
    }
}
