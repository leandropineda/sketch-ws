package com.lpineda.dsketch.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.*;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.core.Sketch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by leandro on 02/09/17.
 */

@ParametersAreNonnullByDefault
public class SketchFactory implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchFactory.class);

    private EventMapping mappings_db;

    private SketchParameters sketchParameters;

    private AtomicLong sketch_counter = new AtomicLong(0);
    private Sketch old_sketch;

    private LoadingCache<Integer, Sketch> sketch_cache =
            CacheBuilder.newBuilder()
                    .maximumSize(1)
                    .removalListener(new RemovalListener<Integer, Sketch>() {
                        public void onRemoval(RemovalNotification notification) {
                            sketch_cache.refresh(0);
                        }
                    })
                    .build(new CacheLoader<Integer, Sketch>() {
                        public Sketch load(Integer key) throws Exception {
                            return build();
                        }
                    });

    public SketchFactory(SketchParameters sketchParameters) {
        this.sketchParameters = sketchParameters;
        this.old_sketch = build();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this,
                sketchParameters.getSketchCleanUpInterval(),
                sketchParameters.getSketchCleanUpInterval(),
                SECONDS);
    }

    public void setMappings(EventMapping mappings_db) {
        this.mappings_db = mappings_db;
    }

    private Sketch build() {
        //TODO: Read sketch configuration from an external place instead of using a config file
        Map<Integer, Integer> hash_functions = Sketch.buildHashFunctions(sketchParameters.getRows(),10000);
        return new Sketch(sketchParameters.getRows(),
                sketchParameters.getCols(),
                sketchParameters.getPrime(),
                hash_functions);
    }

    public Sketch getSketch() throws ExecutionException {
        return this.sketch_cache.get(0);
    }

    public void run() {
        try {
            Sketch sketch = getSketch();
            HashSet<Integer> heavy_hitters =
                    sketch.getHeavyHitters(sketchParameters.getHeavyHitterThreshold());
            HashSet<Integer> heavy_changers =
                    sketch.getHeavyChangers(sketchParameters.getHeavyChangerThreshold(), old_sketch);
            LOGGER.info("["+ sketch_counter.getAndIncrement() +"]" +
                    " Heavy hitters: " + mappings_db.getMappings(heavy_hitters) +
                    " Heavy changers: " + mappings_db.getMappings(heavy_changers));
            old_sketch = sketch;

            this.sketch_cache.invalidate(0);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Mapping addEvent(String event) throws ExecutionException {
        Integer mapping = Integer.valueOf(this.mappings_db.get(event));
        getSketch().addElement(mapping);
        return new Mapping(event, mapping);
    }
}
