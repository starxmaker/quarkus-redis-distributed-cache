package dev.leosanchez.interceptors;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.logging.Logger;

@Interceptor
@Priority(10000)
@CachedInvalidate(cacheName = "")
public class CachedInvalidateInterceptor {
    @Inject
    CachedService cachedService;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object invalidateCache(InvocationContext context) throws Exception {
        CachedInvalidate cachedAnnotation = context.getMethod().getAnnotation(CachedInvalidate.class);
        String generatedKey = cachedService.generateKey(cachedAnnotation.cacheName(), context.getParameters(),
                context.getMethod().getParameterAnnotations());
        if (cachedService.exists(generatedKey)) {
            LOG.info("Invalidating cache");
            cachedService.removeSingleEntry(generatedKey);
        }
        return context.proceed();
    }
}
