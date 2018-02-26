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
    private final NavigableMap<Integer, HeavyKeys> heavyKeys;

    public HeavyKeysHistoryQueue(Integer heavyKeysHistoryQueueMaxLength) {
        if (heavyKeysHistoryQueueMaxLength <= 0)
            throw new NegativeArraySizeException("History length must be greater than zero.");
        this.heavyKeysHistoryQueueMaxLength = heavyKeysHistoryQueueMaxLength;

        this.heavyKeys = new TreeMap<>();
    }

    public void addHeavyKeys(HeavyKeys heavyKeys, Integer epoch) {
        try {
            if (this.heavyKeys.size() > this.heavyKeysHistoryQueueMaxLength) {
                throw new ArrayIndexOutOfBoundsException("Heavy keys history size is bigger than allowed max.");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        if (this.heavyKeys.size() == this.heavyKeysHistoryQueueMaxLength) {
            this.heavyKeys.pollFirstEntry();
        }
        this.heavyKeys.put(epoch, heavyKeys);
    }

    public NavigableMap<Integer, HeavyKeys> getHeavyKeys(Integer count) {
        if (count > this.heavyKeys.size() || count == 0)
            return this.heavyKeys;
        Integer higherEpoch = this.heavyKeys.lastEntry().getKey();
        return this.heavyKeys.subMap(higherEpoch - count, false, higherEpoch, true);
    }

}
