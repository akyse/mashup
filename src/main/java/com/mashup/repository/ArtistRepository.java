package com.mashup.repository;


import com.mashup.client.MusicBrainzClient;
import com.mashup.dto.ArtistDTO;
import com.mashup.model.MbId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Objects;

@Service
public class ArtistRepository {

    @Autowired
    private MusicBrainzClient musicBrainzClient;

    public Observable<ArtistDTO> observe(final MbId mbId) {
        if (Objects.isNull(mbId)) {
            return Observable.error(new IllegalArgumentException());
        }

        return musicBrainzClient.observe(mbId);
    }
}
