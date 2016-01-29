package com.mashup.repository;

import com.mashup.client.CovertArtArchiveClient;
import com.mashup.dto.CovertArtDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;

@Component
public class CovertArtRepository {

    @Autowired
    private CovertArtArchiveClient covertArtArchiveClient;

    public Observable<CovertArtDTO> observe(final String mbId) {
        if (StringUtils.isBlank(mbId)) {
            return Observable.error(new IllegalArgumentException());
        }

        return covertArtArchiveClient.observe(mbId);
    }
}
