package com.mashup.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaDataDTO {
    public Query query;

    public String getDescription() {
        if (Objects.isNull(query)) {
            return null;
        }

        return query.pages.fields.stream().findFirst().orElseGet(HashMap::new).getOrDefault("extract", null);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Query {
        public Pages pages;
    }

    public static class Pages {
        public List<Map<String, String>> fields = new ArrayList<Map<String, String>>();

        @JsonAnySetter
        public void setDynamicProperty(String name, Map<String, String> map) {
            fields.add(map);
        }
    }
}

