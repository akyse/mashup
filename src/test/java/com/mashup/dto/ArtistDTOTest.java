package com.mashup.dto;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ArtistDTOTest {

    private ArtistDTO target;

    @Before
    public void before() {
        target = new ArtistDTO();
    }

    @Test
    public void verifyEmptyListWhenReleaseGroupsIsNull() {
        //given
        target.releaseGroups = null;
        //when
        List<ArtistDTO.ReleaseGroup> result = target.getReleaseGroups();
        //then
        assertNotNull(result);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void verifyNonExistingRelationsWillReturnEmptyString() {
        //given
        //when
        String result = target.getMetaDataUrl();
        //then
        assertThat(result, isEmptyOrNullString());
    }

    @Test
    public void verifyNonExistingWikipediaRelationWillReturnEmptyString() {
        //given
        target.relations = new ArrayList<>();
        //when
        String result = target.getMetaDataUrl();
        //then
        assertThat(result, isEmptyOrNullString());
    }

}