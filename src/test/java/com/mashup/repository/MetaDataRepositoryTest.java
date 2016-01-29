package com.mashup.repository;

import com.mashup.client.WikipediaClient;
import com.mashup.dto.MetaDataDTO;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MetaDataRepositoryTest {
    private MetaDataRepository target;
    private TestSubscriber<MetaDataDTO> testSubscriber;

    @Before
    public void before() {
        target = new MetaDataRepository();
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
        String band = "_band";
        String url = "url/" + band;
        WikipediaClient wikipediaClient = mock(WikipediaClient.class);
        given(wikipediaClient.observe(band)).willReturn(Observable.empty());
        ReflectionTestUtils.setField(target, "wikipediaClient", wikipediaClient);
        //when
        target.observe(url).subscribe(testSubscriber);
        //then
        verify(wikipediaClient).observe(band);
    }
}