package com.lpineda.dsketch;

/**
 * Created by leandro on 01/09/17.
 */

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class EventReceiverConfiguration extends Configuration {

    @NotNull
    private Map<String, String> sketchConfiguration = Collections.emptyMap();

    @NotNull
    private Map<String, String> redisConfiguration = Collections.emptyMap();

    @JsonProperty("sketchConfiguration")
    public Map<String, String> getSketchConfiguration() {
        return sketchConfiguration;
    }

    @JsonProperty("redis")
    public Map<String, String> getRedisConfiguration() { return redisConfiguration; }


}
