package com.mashup.exception;

import com.mashup.model.MbId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Artist with provided mbid could not be found")
public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException(MbId mbId) {
        super(String.format("Artist with mbId '%s' could not be found", mbId.toString()));
    }
}
