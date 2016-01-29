package com.mashup.dto;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNull;


public class CovertArtDTOTest {

    private CovertArtDTO target;

    @Before
    public void before() {
        target = new CovertArtDTO();
    }

    @Test
    public void verifyImagesIsNull() {
        //given
        //when
        String result = target.getImageUrl();
        //then
        assertNull(result);
    }

    @Test
    public void verifyImagesIsEmpty() {
        //given
        target.images = new ArrayList<>();
        //when
        String result = target.getImageUrl();
        //then
        assertNull(result);
    }
}