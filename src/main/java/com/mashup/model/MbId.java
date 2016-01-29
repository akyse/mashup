package com.mashup.model;


import java.util.UUID;

public class MbId {

    private final UUID uuid;

    public MbId(String mbId) {
        this.uuid = UUID.fromString(mbId);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }
}
