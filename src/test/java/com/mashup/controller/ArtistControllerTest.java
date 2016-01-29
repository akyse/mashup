package com.mashup.controller;

import com.mashup.model.Artist;
import com.mashup.model.MbId;
import com.mashup.service.ArtistService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class ArtistControllerTest {

    private ArtistController target;
    private ArtistService artistService;
    private MbId mbId;

    @Before
    public void before() {
        target = new ArtistController();

        artistService = mock(ArtistService.class);
        ReflectionTestUtils.setField(target, "artistService", artistService);

        mbId = new MbId(ArtistController.MBID_PEARL_JAM);
    }

    @Test
    public void verify200() {
        //given
        Artist artist = mock(Artist.class);
        given(artistService.observe(mbId)).willReturn(Observable.just(artist));
        //when
        DeferredResult<Artist> result = target.get(mbId);
        //then
        Artist artistResult = (Artist) result.getResult();
        assertThat(artistResult, is(artist));
    }

    @Test
    public void verifyException() {
        //given
        given(artistService.observe(mbId)).willReturn(Observable.error(new RuntimeException()));
        //when
        DeferredResult<Artist> result = target.get(mbId);
        //then
        assertNull(result.getResult());
    }
}