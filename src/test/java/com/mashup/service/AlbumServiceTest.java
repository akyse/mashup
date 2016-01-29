package com.mashup.service;

import com.mashup.dto.ArtistDTO;
import com.mashup.dto.CovertArtDTO;
import com.mashup.model.Album;
import com.mashup.repository.CovertArtRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class AlbumServiceTest {

    private static final String ID = "1";
    private static final String TITLE = "title";
    private static final String IMAGE = "image";
    private AlbumService target;
    private ArtistDTO artistDTO;
    private TestSubscriber<List<Album>> testSubscriber;
    private CovertArtRepository covertArtRepository;
    private List<ArtistDTO.ReleaseGroup> releaseGroups;

    @Before
    public void before() {
        target = new AlbumService();
        testSubscriber = new TestSubscriber<>();

        CovertArtDTO covertArtDTO = mock(CovertArtDTO.class);
        given(covertArtDTO.getImageUrl()).willReturn(IMAGE);

        covertArtRepository = mock(CovertArtRepository.class);
        given(covertArtRepository.observe(ID)).willReturn(Observable.just(covertArtDTO));
        ReflectionTestUtils.setField(target, "covertArtRepository", covertArtRepository);

        ArtistDTO.ReleaseGroup releaseGroup = mock(ArtistDTO.ReleaseGroup.class);
        given(releaseGroup.getId()).willReturn(ID);
        given(releaseGroup.getTitle()).willReturn(TITLE);

        artistDTO = mock(ArtistDTO.class);
        releaseGroups = Arrays.asList(releaseGroup, releaseGroup);
        given(artistDTO.getReleaseGroups()).willReturn(releaseGroups);
    }

    @Test
    public void verifyAlbums() {
        //given
        //when
        target.observe(artistDTO).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        List<Album> albums = testSubscriber.getOnNextEvents().stream().findFirst().get();

        Album album = albums.stream().findFirst().get();
        assertThat(album.getId(), is(ID));
        assertThat(album.getTitle(), is(TITLE));
        assertThat(album.getImage(), is(IMAGE));
    }

    @Test
    public void verifyNrOfAlbums() {
        //given
        //when
        target.observe(artistDTO).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        List<Album> albums = testSubscriber.getOnNextEvents().stream().findFirst().get();
        assertThat(albums.size(), is(releaseGroups.size()));
    }

    @Test
    public void verifyNoReleaseGroupsWillReturnEmptyList() {
        //given
        given(artistDTO.getReleaseGroups()).willReturn(Collections.EMPTY_LIST);
        //when
        target.observe(artistDTO).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        List<Album> albums = testSubscriber.getOnNextEvents().stream().findFirst().get();
        assertThat(albums.isEmpty(), is(true));
    }

    @Test
    public void verifyNoCovertArt() {
        //given
        given(covertArtRepository.observe(ID)).willReturn(Observable.just(null));
        //when
        target.observe(artistDTO).subscribe(testSubscriber);
        //then
        testSubscriber.assertNoErrors();
        List<Album> albums = testSubscriber.getOnNextEvents().stream().findFirst().get();

        Album album = albums.stream().findFirst().get();
        assertThat(album.getId(), is(ID));
        assertThat(album.getTitle(), is(TITLE));
        assertThat(album.getImage(), isEmptyOrNullString());
    }
}