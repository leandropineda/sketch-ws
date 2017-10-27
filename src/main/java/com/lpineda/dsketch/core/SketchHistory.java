package com.lpineda.dsketch.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leandro on 25/10/17.
 */
public class SketchHistory {
    private LinkedBlockingDeque<Sketch> sketch_queue = new LinkedBlockingDeque<>(2);
    private static final Logger LOGGER = LoggerFactory.getLogger(SketchHistory.class);
    private AtomicLong sketch_counter = new AtomicLong(0);

    public void addSketch(Sketch sketch) {
        LOGGER.info("Adding cache to history");
        try {
            sketch_queue.removeLast();
            sketch_queue.putFirst(sketch);
            this.sketch_counter.incrementAndGet();
            LOGGER.debug("Sketch added");
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public Sketch getSketch(Integer time) {
        return (Sketch)sketch_queue.toArray()[time];
    }

    public Long getCounter() {
        return sketch_counter.get();
    }
}
