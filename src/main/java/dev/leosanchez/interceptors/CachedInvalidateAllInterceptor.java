package dev.leosanchez.interceptors;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Priority(10000)
@CachedInvalidateAll(cacheName = "")
public class CachedInvalidateAllInterceptor {

    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object invalidateCache(InvocationContext context) throws Exception {
        CachedInvalidateAll cachedAnnotation = context.getMethod().getAnnotation(CachedInvalidateAll.class);
        cachedService.removeAll(cachedAnnotation.cacheName());
        LOG.info("all cache entries removed for cache: " + cachedAnnotation.cacheName());
        return context.proceed();
    }
}
