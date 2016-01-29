package com.mashup.service;


import com.mashup.dto.ArtistDTO;
import com.mashup.dto.MetaDataDTO;
import com.mashup.exception.ArtistNotFoundException;
import com.mashup.model.Album;
import com.mashup.model.Artist;
import com.mashup.model.MbId;
import com.mashup.repository.ArtistRepository;
import com.mashup.repository.MetaDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

public class ArtistServiceTest {

    private static final String ID = "5b11f4ce-a62d-471e-81fc-a69a8278c7da";
    private static final String NAME = "name";
    private static final String META_DATA_URL = "http://www.wikipedia.com";
    private static final String DESCRIPTION = "description";
    private ArtistService target;
    private MbId mbId;
    private ArtistRepository artistRepository;
    private TestSubscriber<Artist> testSubscriber;
    private MetaDataRepository metaDataRepository;
    private ArtistDTO artistDTO;
    private AlbumService albumService;

    @Before
    public void before() {
        target = new ArtistService();
        testSubscriber = new TestSubscriber<>();

        artistRepository = mock(ArtistRepository.class);
        ReflectionTestUtils.setField(target, "artistRepository", artistRepository);

        metaDataRepository = mock(MetaDataRepository.class);
        given(metaDataRepository.observe(META_DATA_URL)).willReturn(Observable.empty());
        ReflectionTestUtils.setField(target, "metaDataRepository", metaDataRepository);

        albumService = mock(AlbumService.class);
        given(albumService.observe(any())).willReturn(Observable.just(new ArrayList<>()));
        ReflectionTestUtils.setField(target, "albumService", albumService);

        mbId = new MbId(ID);

        artistDTO = mock(ArtistDTO.class);
        given(artistDTO.getId()).willReturn(ID);
        given(artistDTO.getName()).willReturn(NAME);
        given(artistDTO.getMetaDataUrl()).willReturn(META_DATA_URL);

    }

    @Test
    public void verifyANullArtistDTOTriggersArtistNotFoundException() {
        //given
        given(artistRepository.observe(mbId)).willReturn(Observable.just(null));
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        testSubscriber.assertError(ArtistNotFoundException.class);
    }

    @Test
    public void verifytArtistWithOutMetaData() {
        //given
        given(artistRepository.observe(mbId)).willReturn(Observable.just(artistDTO));
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        Artist artist = testSubscriber.getOnNextEvents().stream().findFirst().get();
        assertThat(artist.getMbId(), is(ID));
        assertThat(artist.getName(), is(NAME));
        assertThat(artist.getDescription(), isEmptyOrNullString());
    }

    @Test
    public void verifyArtistWithMetaData() {
        //given
        given(artistRepository.observe(mbId)).willReturn(Observable.just(artistDTO));
        MetaDataDTO metaDataDTO = mock(MetaDataDTO.class);
        given(metaDataDTO.getDescription()).willReturn(DESCRIPTION);
        given(metaDataRepository.observe(META_DATA_URL)).willReturn(Observable.just(metaDataDTO));
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        Artist artist = testSubscriber.getOnNextEvents().stream().findFirst().get();
        assertThat(artist.getMbId(), is(ID));
        assertThat(artist.getName(), is(NAME));
        assertThat(artist.getDescription(), is(DESCRIPTION));
    }

    @Test
    public void verifyArtistWithAlbums() {
        //given
        given(artistRepository.observe(mbId)).willReturn(Observable.just(artistDTO));
        List<Album> albums = new ArrayList<Album>() {{
            add(mock(Album.class));
        }};
        given(albumService.observe(artistDTO)).willReturn(Observable.just(albums));
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        Artist artist = testSubscriber.getOnNextEvents().stream().findFirst().get();
        assertThat(artist.getMbId(), is(ID));
        assertThat(artist.getName(), is(NAME));
        assertThat(artist.getAlbums(), is(albums));
    }
}