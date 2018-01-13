package com.lpineda.dsketch;

/**
 * Created by leandro on 01/09/17.
 */

import com.lpineda.dsketch.api.DatabaseConfig;
import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.api.MessageBrokerConfig;
import com.lpineda.dsketch.api.SketchConfig;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class EventReceiverConfiguration extends Configuration {

    @NotNull
    private DatabaseConfig databaseConfig;

    @NotNull
    private MessageBrokerConfig messageBrokerConfig;

    @NotNull
    private SketchConfig sketchConfig;

    @NotNull
    private DetectionParameters detectionParameters;

    @JsonProperty("database")
    public DatabaseConfig getDatabaseConfig() { return this.databaseConfig; }

    @JsonProperty("database")
    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @JsonProperty("broker")
    public MessageBrokerConfig getMessageBrokerConfig() { return this.messageBrokerConfig; }

    @JsonProperty("broker")
    public void setMessageBrokerConfig(MessageBrokerConfig messageBrokerConfig) { this.messageBrokerConfig = messageBrokerConfig; }

    @JsonProperty("sketchConfig")
    public SketchConfig getSketchConfig() {
        return this.sketchConfig;
    }

    @JsonProperty("sketchConfig")
    public void setSketchConfig(SketchConfig sketchConfig) { this.sketchConfig = sketchConfig; }

    @JsonProperty("detectionParameters")
    public DetectionParameters getDetectionParameters() {
        return this.detectionParameters;
    }

    @JsonProperty("detectionParameters")
    public void setDetectionParameters(DetectionParameters detectionParameters) { this.detectionParameters = detectionParameters; }

}
