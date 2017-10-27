package com.lpineda.dsketch;

/**
 * Created by leandro on 01/09/17.
 */

import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.db.EventMapping;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class EventReceiverConfiguration extends Configuration {

    @NotNull
    private EventMapping eventMapping;

    @NotNull
    private SketchParameters sketchParameters;

    @JsonProperty("redis")
    public EventMapping getEventMapping() { return this.eventMapping; }

    @JsonProperty("redis")
    public void setEventMapping(EventMapping eventMapping) {
        this.eventMapping = eventMapping;
    }

    @JsonProperty("sketchParameters")
    public SketchParameters getSketchParameters() {
        return this.sketchParameters;
    }

    @JsonProperty("sketchParameters")
    public void setSketchParameters(SketchParameters sketchParameters) { this.sketchParameters = sketchParameters; }

}
