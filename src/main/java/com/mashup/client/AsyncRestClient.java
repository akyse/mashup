package com.mashup.client;


import com.mashup.configuration.EhCacheConfiguration;
import com.netflix.hystrix.contrib.javanica.command.ObservableResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriTemplate;
import rx.Observable;

import java.net.URI;

@SuppressWarnings("all")
public class AsyncRestClient {
    static final String COMMAND_TIMEOUT = "10000";
    static final String THREADPOOL_SIZE = "10";
    static final String THREADPOOL_MAX_QUEUE_SIZE = "50";
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRestClient.class);

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Value("#{cacheManager.getCache('" + EhCacheConfiguration.CACHE_REQUEST + "')}")
    private Cache requestCache;

    @Value("#{cacheManager.getCache('" + EhCacheConfiguration.CACHE_FALLBACK + "')}")
    private Cache fallbackCache;

    protected <T> Observable<T> observe(final String url, final Class<T> returnType, final Object... urlVars) {
        return new ObservableResult<T>() {
            @Override
            public T invoke() {
                return observeInternal(url, returnType, urlVars);
            }
        };
    }

    protected <T> T fallback(String url, Class<T> returnType, final Object... urlVars) {
        URI uri = new UriTemplate(url).expand(urlVars);
        String key = uri.toString();
        Cache.ValueWrapper potentialCacheHit = fallbackCache.get(key);
        if (potentialCacheHit != null) {
            return (T) potentialCacheHit.get();
        }

        throw new RuntimeException("Hystrix command and fallback failed");
    }

    protected <T> T observeInternal(final String url, final Class<T> returnType, final Object... urlVars) {
        URI uri = new UriTemplate(url).expand(urlVars);
        String key = uri.toString();
        LOGGER.info("Request for {}", uri.toString(), urlVars);
        try {
            Cache.ValueWrapper potentialCacheHit = requestCache.get(uri.toString());
            if (potentialCacheHit != null) {
                LOGGER.info("Found cache hit for {}", key);
                return (T) potentialCacheHit.get();
            }
            //todo: figure out why pooled asyncresttemplate loses connection (closed by peer)
            T result = createAsyncRestTemplate().getForEntity(url, returnType, urlVars).get().getBody();
            requestCache.put(key, result);
            fallbackCache.put(key, result);
            return result;
        } catch (Exception e) {
            if (e.getCause() instanceof HttpClientErrorException &&
                    (((HttpClientErrorException) e.getCause()).getStatusCode().is4xxClientError())) {
                //todo: figure out how to make Observable.error(<notfound>)
                return null;
            }

            throw new RuntimeException(e);
        }
    }

    protected AsyncRestTemplate createAsyncRestTemplate() {
        return new AsyncRestTemplate();
    }
}

