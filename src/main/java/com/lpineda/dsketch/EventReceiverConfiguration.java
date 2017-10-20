package com.lpineda.dsketch;

/**
 * Created by leandro on 01/09/17.
 */

import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.db.EventMapping;
import com.lpineda.dsketch.db.SketchFactory;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class EventReceiverConfiguration extends Configuration {

    @NotNull
    private EventMapping eventMapping;

    @NotNull
    private SketchFactory sketchFactory;

    @NotNull
    private DetectionParameters detectionParameters;


    @JsonProperty("redis")
    public EventMapping getEventMapping() { return this.eventMapping; }

    @JsonProperty("redis")
    public void setEventMapping(EventMapping eventMapping) {
        this.eventMapping = eventMapping;
    }

    @JsonProperty("sketch")
    public SketchFactory getSketchFactory() {
        return this.sketchFactory;
    }

    @JsonProperty("sketch")
    public void setSketchFactory(SketchFactory sketchFactory) { this.sketchFactory = sketchFactory; }

    @JsonProperty("detection_parameters")
    public DetectionParameters getDetectionParameters() { return this.detectionParameters; }

    @JsonProperty("detection_parameters")
    public void setDetectionParameters(DetectionParameters detectionParameters) { this.detectionParameters = detectionParameters; }

}
