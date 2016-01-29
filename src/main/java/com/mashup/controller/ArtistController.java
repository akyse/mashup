package com.mashup.controller;

import com.mashup.model.Artist;
import com.mashup.model.MbId;
import com.mashup.service.ArtistService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observer;
import rx.schedulers.Schedulers;

@Api(value = "Artist", description = "Artist")
@RestController
public class ArtistController {

    public static final String MBID_PEARL_JAM = "83b9cbe7-9857-49e2-ab8e-b57b01038103";
    private static final long TIMEOUT = 60000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistController.class);
    @Autowired
    private ArtistService artistService;

    @ApiOperation(value = "artist", nickname = "artist", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Artist.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    @RequestMapping(value = "/artist/{mbId}", method = RequestMethod.GET)
    public DeferredResult<Artist> get(@ApiParam(defaultValue = MBID_PEARL_JAM, name = "mbId",
            required = true) @PathVariable final MbId mbId) {
        final DeferredResult<Artist> result = new DeferredResult<>(TIMEOUT);

        artistService.observe(mbId).subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Artist>() {

                    private Artist artist;

                    @Override
                    public void onCompleted() {
                        result.setResult(artist);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LOGGER.error(String.format("Request could not be completed {mbid:%s}", mbId), throwable);
                        result.setErrorResult(throwable);
                    }

                    @Override
                    public void onNext(Artist artist) {
                        this.artist = artist;
                    }
                });

        return result;
    }

}
