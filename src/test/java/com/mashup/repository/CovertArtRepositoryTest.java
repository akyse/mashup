package com.mashup.repository;

import com.mashup.client.CovertArtArchiveClient;
import com.mashup.dto.CovertArtDTO;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CovertArtRepositoryTest {
    private CovertArtRepository target;
    private TestSubscriber<CovertArtDTO> testSubscriber;

    @Before
    public void before() {
        target = new CovertArtRepository();
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
    public void verifyParamIsEmptyWillReturnError() {
        //given
        //when
        target.observe(StringUtils.EMPTY).subscribe(testSubscriber);
        //then
        testSubscriber.assertError(IllegalArgumentException.class);
    }

    @Test
    public void verifyParamIsPassedToClient() {
        //given
        String mbId = "83b9cbe7-9857-49e2-ab8e-b57b01038103";
        CovertArtArchiveClient covertArtArchiveClient = mock(CovertArtArchiveClient.class);
        given(covertArtArchiveClient.observe(mbId)).willReturn(Observable.empty());
        ReflectionTestUtils.setField(target, "covertArtArchiveClient", covertArtArchiveClient);
        //when
        target.observe(mbId).subscribe(testSubscriber);
        //then
        verify(covertArtArchiveClient).observe(mbId);
    }
}