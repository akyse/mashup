package com.mashup.client;

import com.mashup.dto.CovertArtDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;
import rx.Observable;

@Component
public class CovertArtArchiveClient extends AsyncRestClient {

    private static final String URL = "http://coverartarchive.org/release-group/{mbId}";

    @HystrixCommand(
            fallbackMethod = "fallback",
            commandKey = "covertArtArchiveCommand",
            groupKey = "covertArtArchive",
            threadPoolKey = "covertArtArchivePool",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = COMMAND_TIMEOUT)},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = THREADPOOL_SIZE),
                    @HystrixProperty(name = "maxQueueSize", value = THREADPOOL_MAX_QUEUE_SIZE)
            })
    public Observable<CovertArtDTO> observe(final String mbId) {
        return super.observe(URL, CovertArtDTO.class, mbId);
    }

    private CovertArtDTO fallback(final String mbId) {
        return super.fallback(URL, CovertArtDTO.class, mbId);
    }
}
