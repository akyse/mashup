package com.mashup.client;

import com.mashup.dto.MetaDataDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;
import rx.Observable;

@Component
public class WikipediaClient extends AsyncRestClient {

    private static final String URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts" +
            "&exintro=true&redirects=true&titles={title}";

    @HystrixCommand(
            fallbackMethod = "fallback",
            commandKey = "wikipediaCommand",
            groupKey = "wikipedia",
            threadPoolKey = "wikipediaPool",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = COMMAND_TIMEOUT)},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = THREADPOOL_SIZE),
                    @HystrixProperty(name = "maxQueueSize", value = THREADPOOL_MAX_QUEUE_SIZE)
            })
    public Observable<MetaDataDTO> observe(final String title) {
        return super.observe(URL, MetaDataDTO.class, title);
    }

    private MetaDataDTO fallback(final String title) {
        return super.fallback(URL, MetaDataDTO.class, title);
    }
}
