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

    @JsonProperty("keys")
    private final List<String> heavyKeys;
    @JsonProperty
    private final Integer counter;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
    private final Date detectionDate;

    public HeavyKeys(Date detectionDate, List<String> heavyKeys) {
        LOGGER.debug(MessageFormat.format("Initializing {0}", HeavyKeys.class.getName()));
        this.heavyKeys = heavyKeys;
        this.counter = this.heavyKeys.size();
        this.detectionDate = detectionDate;
    }

}
