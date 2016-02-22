package com.gatehillsoftware.tools.simplehealthcheck.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Pete Cornish {@literal <outofcoffee@gmail.com>}
 */
public class ServiceFactory {
    private static LoadingCache<Class<?>, Object> implCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Object>() {
        @Override
        public Object load(Class<?> key) throws Exception {
            return Class.forName(key.getCanonicalName() + "Impl").newInstance();
        }
    });

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz) {
        try {
            return (T) implCache.get(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
