package com.lpineda.dsketch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leandro on 25/10/17.
 */
public final class SketchHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchHistory.class);

    private final Integer historyMaxLength;
    private final NavigableMap<Date, Sketch> sketchHistory;
    private final AtomicLong sketchCounter;

    public SketchHistory(Integer historyMaxLength) {
        LOGGER.info(MessageFormat.format("Initializing {0}", SketchHistory.class.getName()));

        if (historyMaxLength <= 0)
            throw new NegativeArraySizeException("History length must be greater than zero.");
        this.historyMaxLength = historyMaxLength;
        this.sketchCounter = new AtomicLong(0);
        LOGGER.info(MessageFormat.format("Will maintain a history of {0} object(s).", this.historyMaxLength));
        this.sketchHistory = new TreeMap<>();
    }

    public void addSketch(final Sketch sketch) {
        try {
            if (this.sketchHistory.size() > this.getHistoryMaxLength()) {
                throw new ArrayIndexOutOfBoundsException("Sketch history size is bigger than historyMaxLength.");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOGGER.error(ex.getMessage());
        }
        if (this.sketchHistory.size() == this.getHistoryMaxLength())
            this.sketchHistory.pollFirstEntry();
        Date date = new Date();
        this.sketchHistory.put(date, sketch);
        this.sketchCounter.incrementAndGet();
    }

    public Integer getHistoryMaxLength() {
        return historyMaxLength;
    }

    public Sketch getSketch(Integer idx) {
        List<Sketch> sketchList = new ArrayList<>(this.sketchHistory.values());
        return sketchList.get(idx);
    }

    public Date getSketchDate(Integer idx) {
        List<Date> dateList = new ArrayList<>(this.sketchHistory.keySet());
        return dateList.get(idx);
    }

    public Long getCounter() {
        return sketchCounter.get();
    }
}
