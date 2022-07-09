package dev.leosanchez.interceptors;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.logging.Logger;

import dev.leosanchez.adapters.cache.RedisAdapter;
import dev.leosanchez.adapters.objectmapper.JacksonAdapter;

@Interceptor
@Priority(1)
@Cached(cacheName = "")
public class CacheInterceptor {

    @Inject
    RedisAdapter redisAdapter;

    @Inject
    JacksonAdapter jacksonAdapter;

    @Inject
    Logger LOG;

    @AroundInvoke
    <T> Object checkCache(InvocationContext context) throws Exception {
        Cached cachedAnnotation = context.getMethod().getAnnotation(Cached.class);
        String cacheName = cachedAnnotation.cacheName();
        LOG.infov("nombre del cache: {0}", cacheName);
        Object response =  getMethodResponse(context);
        LOG.info(jacksonAdapter.serializeObject(response));
        return response;
    }
    
    private Object getMethodResponse(InvocationContext context) throws Exception{
        return context.proceed();
    }
}
