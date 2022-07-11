package dev.leosanchez.interceptors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE })
public @interface CachedInvalidateAll {
    @Nonbinding String cacheName();
}
