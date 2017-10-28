package com.lpineda.dsketch.core;

import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leandro on 25/10/17.
 */
public final class SketchHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchHistory.class);

    private final Queue<Sketch> sketch_queue = EvictingQueue.create(2);
    private final AtomicLong sketch_counter = new AtomicLong(0);

    public void addSketch(final Sketch sketch) {
        sketch_queue.add(sketch);
        this.sketch_counter.incrementAndGet();
    }

    public Sketch getSketch(Integer time) {
        return (Sketch)sketch_queue.toArray()[time];
    }

    public Long getCounter() {
        return sketch_counter.get();
    }
}
