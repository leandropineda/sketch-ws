package com.lpineda.dsketch.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

public class HeavyKeys {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeys.class);

    @JsonProperty("heavyHitters")
    private final List<String> heavyHitters;
    @JsonProperty("heavyChangers")
    private final List<String> heavyChangers;
    @JsonProperty
    private final Integer heavyHittersCounter;
    @JsonProperty
    private final Integer heavyChangersCounter;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
    private final Date detectionDate;

    public HeavyKeys(Date detectionDate, List<String> heavyHitters, List<String> heavyChangers) {
        LOGGER.debug(MessageFormat.format("Initializing {0}", HeavyKeys.class.getName()));
        this.heavyHitters = heavyHitters;
        this.heavyChangers = heavyChangers;
        this.heavyHittersCounter = this.heavyHitters.size();
        this.heavyChangersCounter = this.heavyChangers.size();
        this.detectionDate = detectionDate;
    }

    public List<String> getHeavyHitters() {
        return this.heavyHitters;
    }

    public List<String> getHeavyChangers() {
        return heavyChangers;
    }
}
