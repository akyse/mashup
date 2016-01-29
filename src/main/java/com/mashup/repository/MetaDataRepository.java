package com.mashup.repository;

import com.mashup.client.WikipediaClient;
import com.mashup.dto.MetaDataDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
public class MetaDataRepository {

    @Autowired
    private WikipediaClient wikipediaClient;

    public Observable<MetaDataDTO> observe(final String metaDataUrl) {
        if (StringUtils.isBlank(metaDataUrl)) {
            return Observable.error(new IllegalArgumentException());
        }

        return wikipediaClient.observe(StringUtils.substringAfterLast(metaDataUrl, "/"));

    }
}
