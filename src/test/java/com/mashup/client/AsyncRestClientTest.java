package com.mashup.client;

import com.mashup.dto.ArtistDTO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AsyncRestClientTest {

    private static final String ID = "id";
    private static final String URL = "http://{ID}";
    private AsyncRestClient target;
    private Cache requestCache;
    private Cache fallbackCache;
    private AsyncRestTemplate asyncRestTemplate;
    private ArtistDTO artistDTO;

    @Before
    public void before() throws ExecutionException, InterruptedException {
        target = spy(new AsyncRestClient());

        requestCache = mock(Cache.class);
        given(requestCache.get(any())).willReturn(null);
        ReflectionTestUtils.setField(target, "requestCache", requestCache);

        fallbackCache = mock(Cache.class);
        ReflectionTestUtils.setField(target, "fallbackCache", fallbackCache);

        asyncRestTemplate = mock(AsyncRestTemplate.class);
        doReturn(asyncRestTemplate).when(target).createAsyncRestTemplate();

        ListenableFuture listenableFuture = mock(ListenableFuture.class);
        given(asyncRestTemplate.getForEntity(URL, ArtistDTO.class, ID)).willReturn(listenableFuture);

        ResponseEntity responseEntity = mock(ResponseEntity.class);
        given(listenableFuture.get()).willReturn(responseEntity);

        given(responseEntity.getBody()).willReturn(artistDTO);

        artistDTO = mock(ArtistDTO.class);

    }

    @Test
    public void verifyHitOnRequestCache() {
        //given
        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        given(valueWrapper.get()).willReturn(artistDTO);

        given(requestCache.get(any())).willReturn(valueWrapper);
        //when
        ArtistDTO result = target.observeInternal(URL, ArtistDTO.class, ID);
        //then
        assertThat(result, is(artistDTO));
        verifyZeroInteractions(asyncRestTemplate);
    }

    @Test
    public void verifyThatResponseIsPutInFallbackAndRequestCache() {
        //given
        //when
        target.observeInternal(URL, ArtistDTO.class, ID);
        //then
        verify(requestCache).put(any(), any());
        verify(fallbackCache).put(any(), any());
    }

    @Test
    public void verifyThat404ExceptionIsReturnedWithNull() {
        //given
        RuntimeException runtimeException = new RuntimeException(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        doThrow(runtimeException).when(asyncRestTemplate).getForEntity(URL, ArtistDTO.class, ID);
        //when
        ArtistDTO result = target.observeInternal(URL, ArtistDTO.class, ID);
        //then
        assertNull(result);
    }

    @Test(expected = RuntimeException.class)
    public void verifyNon404Error() {
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)).when(asyncRestTemplate).getForEntity(URL, ArtistDTO.class, ID);
        //when
        target.observeInternal(URL, ArtistDTO.class, ID);
        //then
    }
}