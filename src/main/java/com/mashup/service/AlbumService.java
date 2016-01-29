package com.mashup.service;


import com.mashup.dto.ArtistDTO;
import com.mashup.dto.CovertArtDTO;
import com.mashup.model.Album;
import com.mashup.repository.CovertArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AlbumService {
    @Autowired
    private CovertArtRepository covertArtRepository;

    public Observable<List<Album>> observe(ArtistDTO artistDTO) {
        return Observable.from(artistDTO.getReleaseGroups())
                .concatMap(r -> covertArtRepository.observe(r.getId())
                        .onErrorResumeNext(Observable.empty())
                        .filter(Objects::nonNull)
                        .defaultIfEmpty(new CovertArtDTO())
                        .flatMap(c -> {
                            Album album = new Album();
                            album.setId(r.getId());
                            album.setTitle(r.getTitle());
                            album.setImage(c.getImageUrl());
                            return Observable.just(album);
                        })
                ).toList()
                .defaultIfEmpty(new ArrayList<>());
    }
}
