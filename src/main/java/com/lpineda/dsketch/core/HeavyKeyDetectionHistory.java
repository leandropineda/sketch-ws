package com.lpineda.dsketch.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lpineda.dsketch.api.HeavyKeysList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HeavyKeyDetectionHistory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetectionHistory.class);

    private final HeavyKeyHistory heavyHitters;
    private final HeavyKeyHistory heavyChangers;

    public HeavyKeyDetectionHistory(Long historyMaxLength) {
        this.heavyHitters = new HeavyKeyHistory(historyMaxLength);
        this.heavyChangers = new HeavyKeyHistory(historyMaxLength);
    }

    public void addHeavyKeys(HeavyKeysList heavyHitters, HeavyKeysList heavyChangers) {
        this.heavyHitters.addHeavyKeys(heavyHitters);
        this.heavyChangers.addHeavyKeys(heavyChangers);
    }

    public List<HeavyKeysList> getHeavyHitters(Integer count) {
        return heavyHitters.getHeavyKeysHistory(count);
    }

    public List<HeavyKeysList> getHeavyChangers(Integer count) {
        return heavyChangers.getHeavyKeysHistory(count);
    }
}