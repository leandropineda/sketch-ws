package com.lpineda.dsketch;

/**
 * Created by leandro on 01/09/17.
 */

import com.lpineda.dsketch.api.DbConfig;
import com.lpineda.dsketch.api.SketchParameters;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class EventReceiverConfiguration extends Configuration {

    @NotNull
    private DbConfig dbConfig;

    @NotNull
    private SketchParameters sketchParameters;

    @JsonProperty("database")
    public DbConfig getDbConfig() { return this.dbConfig; }

    @JsonProperty("database")
    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @JsonProperty("sketchParameters")
    public SketchParameters getSketchParameters() {
        return this.sketchParameters;
    }

    @JsonProperty("sketchParameters")
    public void setSketchParameters(SketchParameters sketchParameters) { this.sketchParameters = sketchParameters; }

}
