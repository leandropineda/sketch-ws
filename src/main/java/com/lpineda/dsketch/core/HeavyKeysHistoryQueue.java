package com.lpineda.dsketch.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HeavyKeysHistoryQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeysHistoryQueue.class);

    @JsonIgnore
    private final Integer heavyKeysHistoryQueueMaxLength;

    @JsonProperty
    private final NavigableMap<Integer, HeavyKeys> heavyHitters;
    @JsonProperty
    private final NavigableMap<Integer, HeavyKeys> heavyChangers;

    public HeavyKeysHistoryQueue(Integer heavyKeysHistoryQueueMaxLength) {
        if (heavyKeysHistoryQueueMaxLength <= 0)
            throw new NegativeArraySizeException("History length must be greater than zero.");
        this.heavyKeysHistoryQueueMaxLength = heavyKeysHistoryQueueMaxLength;

        this.heavyHitters = new TreeMap<>();
        this.heavyChangers = new TreeMap<>();
    }

    public void addHeavyKeys(HeavyKeys heavyHitters, HeavyKeys heavyChangers, Integer epoch) {
        try {
            if (this.heavyHitters.size() > this.heavyKeysHistoryQueueMaxLength ||
                    this.heavyChangers.size() > this.heavyKeysHistoryQueueMaxLength) {
                throw new ArrayIndexOutOfBoundsException("Heavy keys history size is bigger than allowed max.");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        if (this.heavyHitters.size() == this.heavyKeysHistoryQueueMaxLength &&
                this.heavyChangers.size() == this.heavyKeysHistoryQueueMaxLength) {
            this.heavyHitters.pollFirstEntry();
            this.heavyChangers.pollFirstEntry();
        }
        this.heavyHitters.put(epoch, heavyHitters);
        this.heavyChangers.put(epoch, heavyChangers);
    }

    private NavigableMap<Integer, HeavyKeys> getHeavyKeys(NavigableMap<Integer, HeavyKeys> heavyKeys, Integer count) {
        if (count > heavyKeys.size() || count == 0)
            return heavyKeys;
        Integer higherEpoch = heavyKeys.lastEntry().getKey();
        return heavyKeys.subMap(higherEpoch - count, false, higherEpoch, true);
    }

    public NavigableMap<Integer, HeavyKeys> getHeavyHitters(Integer count) {
        return this.getHeavyKeys(this.heavyHitters, count);
    }

    public NavigableMap<Integer, HeavyKeys> getHeavyChangers(Integer count) {
        return this.getHeavyKeys(this.heavyChangers, count);
    }
}
