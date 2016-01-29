package com.mashup.service;

import com.mashup.dto.MetaDataDTO;
import com.mashup.exception.ArtistNotFoundException;
import com.mashup.model.Album;
import com.mashup.model.Artist;
import com.mashup.model.MbId;
import com.mashup.repository.ArtistRepository;
import com.mashup.repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.List;
import java.util.Objects;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private MetaDataRepository metaDataRepository;

    @Autowired
    private AlbumService albumService;

    public Observable<Artist> observe(final MbId mbId) {
        return artistRepository.observe(mbId)
                .onErrorResumeNext(Observable.empty())
                .filter(Objects::nonNull)
                .switchIfEmpty(Observable.error(new ArtistNotFoundException(mbId)))
                .flatMap(a -> metaDataRepository.observe(a.getMetaDataUrl())
                        .onErrorResumeNext(Observable.empty())
                        .filter(Objects::nonNull)
                        .defaultIfEmpty(new MetaDataDTO())
                        .flatMap(m -> {
                            Artist artist = new Artist();
                            artist.setMbId(a.getId());
                            artist.setName(a.getName());
                            artist.setDescription(m.getDescription());

                            Observable<List<Album>> albumsObservable = albumService.observe(a);

                            return Observable.just(artist).zipWith(albumsObservable, (art, alb) -> {
                                art.setAlbums(alb);
                                return art;
                            });
                        })
                );
    }
}

