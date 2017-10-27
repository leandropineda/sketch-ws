package com.lpineda.dsketch.core;

import com.google.common.cache.*;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.db.EventMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by leandro on 25/10/17.
 */

@ParametersAreNonnullByDefault
public class SketchManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchManager.class);

    private SketchParameters sketchParameters;

    private LoadingCache<Integer, Sketch> sketch_cache;
    private SketchHistory sketchHistory;
    private EventMapping eventMapping;

    public SketchManager(SketchParameters sketchParameters) {
        this.sketchParameters = sketchParameters;

        RemovalListener<Integer, Sketch> listener = new RemovalListener<Integer, Sketch>() {
            public void onRemoval(RemovalNotification<Integer, Sketch> notification) {
                LOGGER.info("Removing sketch");
                sketchHistory.addSketch(notification.getValue());
            }
        };

        CacheLoader<Integer, Sketch> loader = new CacheLoader<Integer, Sketch>() {
            public Sketch load(Integer key) throws Exception {
                return buildSketch();
            }
        };

        sketch_cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .removalListener(listener)
                .build(loader);
    }

    public void setSketchHistory(SketchHistory sketchHistory) {
        this.sketchHistory = sketchHistory;
    }

    public void setEventMapping(EventMapping eventMapping) {
        this.eventMapping = eventMapping;
    }

    private Sketch buildSketch() {
        //TODO: Read sketch configuration from an external place instead of using a config file
        Map<Integer, Integer> hash_functions =
                Sketch.buildHashFunctions(sketchParameters.getRows(),10000);
        return new Sketch(sketchParameters.getRows(),
                sketchParameters.getCols(),
                sketchParameters.getPrime(),
                hash_functions);
    }

    protected void invalidateSketch() throws ExecutionException {
        this.sketch_cache.invalidate(0);
    }

    public Mapping addEvent(String event) throws ExecutionException {
        Integer mapping = Integer.valueOf(this.eventMapping.get(event));
        sketch_cache.get(0).addElement(mapping);
        return new Mapping(event, mapping);

    }

}
