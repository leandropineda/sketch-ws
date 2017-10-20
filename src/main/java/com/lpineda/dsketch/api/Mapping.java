package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Created by leandro on 04/09/17.
 */
public class Mapping {
    private String event;

    private String mapping;

    public Mapping(String event, Integer mapping) {
        this.event = event;
        this.mapping = String.valueOf(mapping);
    }

    public Mapping(String event, String mapping) {
        this.event = event;
        this.mapping = mapping;
    }

    @JsonProperty
    public String getEvent() {
        return event;
    }

    @JsonProperty
    public String getMapping() {
        return mapping;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("event", event)
                .add("mapping", mapping)
                .toString();
    }
}
