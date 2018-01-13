package com.lpineda.dsketch.core;

import com.lpineda.dsketch.jobs.HeavyKeysList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

public class HeavyKeyDetectionHistory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetectionHistory.class);

    private final HeavyKeyHistory heavyHitters;
    private final HeavyKeyHistory heavyChangers;

    public HeavyKeyDetectionHistory(Long historyMaxLength) {
        LOGGER.info(MessageFormat.format("Initializing {0}", HeavyKeyDetectionHistory.class.getName()));
        LOGGER.info(MessageFormat.format("Will maintain a history of {0} object(s).", historyMaxLength));


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
