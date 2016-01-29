package com.mashup.client;

import com.mashup.dto.ArtistDTO;
import com.mashup.model.MbId;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.UUID;

@Component
public class MusicBrainzClient extends AsyncRestClient {

    private static final String URL = "http://musicbrainz.org/ws/2/artist/{mbId}?&fmt=json&inc=url-rels+release-groups";

    @HystrixCommand(
            fallbackMethod = "fallback",
            commandKey = "musicBrainzCommand",
            groupKey = "musicBrainz",
            threadPoolKey = "musicBrainzPool",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = COMMAND_TIMEOUT)},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = THREADPOOL_SIZE),
                    @HystrixProperty(name = "maxQueueSize", value = THREADPOOL_MAX_QUEUE_SIZE)
            })
    public Observable<ArtistDTO> observe(final MbId mbId) {
        return super.observe(URL, ArtistDTO.class, mbId.toString());
    }

    private ArtistDTO fallback(final UUID mbId) {
        return super.fallback(URL, ArtistDTO.class, mbId.toString());
    }
}
