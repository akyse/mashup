package com.mashup.configuration;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EhCacheConfiguration implements CachingConfigurer {

    public static final String CACHE_REQUEST = "request";
    public static final String CACHE_FALLBACK = "fallback";

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(requestCache());
        config.addCache(fallbackCache());
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    public CacheConfiguration requestCache() {
        return new net.sf.ehcache.config.CacheConfiguration()
                .name(CACHE_REQUEST)
                .timeToLiveSeconds(360)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .maxEntriesLocalHeap(10000);
    }

    @Bean
    public CacheConfiguration fallbackCache() {
        return new net.sf.ehcache.config.CacheConfiguration()
                .name(CACHE_FALLBACK)
                .timeToLiveSeconds(360 * 24)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .maxEntriesLocalHeap(10000);
    }

    @Override
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Override
    @Bean
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(cacheManager());
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}
