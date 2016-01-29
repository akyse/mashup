package com.mashup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDTO {
    public static final String WIKIPEDIA = "wikipedia";
    public String name;
    public String id;
    public List<Relation> relations;
    @JsonProperty("release-groups")
    public List<ReleaseGroup> releaseGroups;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return Objects.isNull(releaseGroups) ? new ArrayList<>() : releaseGroups;
    }

    public String getMetaDataUrl() {
        if (Objects.isNull(relations)) {
            return StringUtils.EMPTY;
        }

        Optional<Relation> relation = relations.stream().filter(r -> WIKIPEDIA.equalsIgnoreCase(r.type)).findFirst();
        if (!relation.isPresent() || Objects.isNull(relation.get().url)) {
            return StringUtils.EMPTY;
        }

        return relation.get().url.resource;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Relation {
        public Url url;
        public String type;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Url {
        public String resource;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReleaseGroup {
        public String id;
        public String title;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}
