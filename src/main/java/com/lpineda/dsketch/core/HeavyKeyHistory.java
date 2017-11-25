package com.lpineda.dsketch.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lpineda.dsketch.api.HeavyKeysList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class HeavyKeyHistory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyHistory.class);

    @JsonIgnore
    private final Long historyMaxLength;
    @JsonProperty
    private final LinkedList<HeavyKeysList> heavyKeysHistory;

    public HeavyKeyHistory(Long historyMaxLength) {
        if (historyMaxLength <= 0)
            throw new NegativeArraySizeException("History length must be greater than zero.");
        this.historyMaxLength = historyMaxLength;
        this.heavyKeysHistory = new LinkedList<>();
    }

    public void addHeavyKeys(HeavyKeysList heavyHitters) {
        try {
            if (this.heavyKeysHistory.size() > this.historyMaxLength) {
                throw new ArrayIndexOutOfBoundsException("Heavy hitters history size is bigger than historyMaxLength.");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        if (this.heavyKeysHistory.size() == this.historyMaxLength)
            this.heavyKeysHistory.removeLast();

        this.heavyKeysHistory.addFirst(heavyHitters);
    }

    public List<HeavyKeysList> getHeavyKeysHistory() {
        return heavyKeysHistory;
    }
    public List<HeavyKeysList> getHeavyKeysHistory(Integer count) {
        if (heavyKeysHistory.size() < count) {
            return heavyKeysHistory;
        }
        return heavyKeysHistory.subList(0,count);
    }
}
