package com.lpineda.dsketch.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leandro on 25/10/17.
 */
public final class SketchHistoryQueue {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchHistoryQueue.class);

    @JsonIgnore
    private final Integer historyQueueMaxLength;
    @JsonProperty
    private final NavigableMap<Integer, Sketch> sketchHistoryQueue;

    public SketchHistoryQueue(Integer historyQueueMaxLength) {
        LOGGER.info(MessageFormat.format("Initializing {0}", SketchHistoryQueue.class.getName()));

        if (historyQueueMaxLength <= 0)
            throw new NegativeArraySizeException("History length must be greater than zero.");
        this.historyQueueMaxLength = historyQueueMaxLength;
        LOGGER.info(MessageFormat.format("Will maintain a history of {0} object(s).", this.historyQueueMaxLength));

        this.sketchHistoryQueue = new TreeMap<>();
    }

    public void addSketch(final Sketch sketch, final Integer epoch) {
        try {
            if (this.sketchHistoryQueue.size() > this.getHistoryQueueMaxLength()) {
                throw new ArrayIndexOutOfBoundsException("Sketch history size is bigger than allowed max.");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        if (this.sketchHistoryQueue.size() == this.getHistoryQueueMaxLength())
            this.sketchHistoryQueue.pollFirstEntry();

        this.sketchHistoryQueue.put(epoch, sketch);
    }

    public Integer getHistoryQueueMaxLength() {
        return historyQueueMaxLength;
    }

    private Integer getEpochFromIndex(Integer idx) {
        Integer lastEpoch = this.sketchHistoryQueue.lastEntry().getKey();
        return lastEpoch - idx;
    }

    public Sketch getSketch(Integer idx) {
        return this.sketchHistoryQueue.get(this.getEpochFromIndex(idx));
    }

    public Integer size() {
        return this.sketchHistoryQueue.size();
    }

    public NavigableMap<Integer, Sketch> getSketchHistoryQueue(Integer count) {
        if (count > sketchHistoryQueue.size() || count == 0)
            return sketchHistoryQueue;
        Integer higherEpoch = sketchHistoryQueue.lastEntry().getKey();
        return sketchHistoryQueue.subMap(higherEpoch - count, false, higherEpoch, true);
    }

}
