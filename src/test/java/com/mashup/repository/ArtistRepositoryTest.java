package com.mashup.repository;

import com.mashup.client.MusicBrainzClient;
import com.mashup.dto.ArtistDTO;
import com.mashup.model.MbId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ArtistRepositoryTest {

    private ArtistRepository target;
    private TestSubscriber<ArtistDTO> testSubscriber;

    @Before
    public void before() {
        target = new ArtistRepository();
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void verifyParamIsNullWillReturnError() {
        //given
        //when
        target.observe(null).subscribe(testSubscriber);
        //then
        testSubscriber.assertError(IllegalArgumentException.class);
    }

    @Test
    public void verifyParamIsPassedToClient() {
        //given
        MbId mbId = new MbId("83b9cbe7-9857-49e2-ab8e-b57b01038103");
        MusicBrainzClient musicBrainzClient = mock(MusicBrainzClient.class);
        given(musicBrainzClient.observe(mbId)).willReturn(Observable.empty());
        ReflectionTestUtils.setField(target, "musicBrainzClient", musicBrainzClient);
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        verify(musicBrainzClient).observe(mbId);
    }
}