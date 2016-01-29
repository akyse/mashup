package com.mashup.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CovertArtDTO {
    public List<Image> images = new ArrayList<>();

    public String getImageUrl() {
        if (Objects.isNull(images)) {
            return null;
        }

        return images.stream().findFirst().orElse(new Image()).url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        @JsonProperty("image")
        public String url;
    }
}
