package com.lpineda.dsketch.core;

import com.google.common.cache.*;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.db.KeyValueTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by leandro on 25/10/17.
 */

public class SketchManager {

    public interface RotationListener {
        void onRotation(Sketch sketch);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchManager.class);

    private final SketchConfig sketchConfig;

    private final KeyValueTransformer keyValueTransformer;

    private final LoadingCache<Integer, Sketch> sketchCache;

    public SketchManager(SketchConfig sketchConfig,
                         RotationListener rotationListener,
                         KeyValueTransformer keyValueTransformer) {
        this.sketchConfig = sketchConfig;
        this.keyValueTransformer = keyValueTransformer;

        LOGGER.info(MessageFormat.format("Initializing {0}", SketchManager.class.getName()));

        RemovalListener<Integer, Sketch> listener = new RemovalListener<Integer, Sketch>() {
            public void onRemoval(RemovalNotification<Integer, Sketch> notification) {
                rotationListener.onRotation(notification.getValue());
            }
        };

        CacheLoader<Integer, Sketch> loader = new CacheLoader<Integer, Sketch>() {
            public Sketch load(Integer key) throws Exception {
                return buildSketch();
            }
        };

        sketchCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .removalListener(listener)
                .build(loader);
    }

    public Sketch buildSketch() {
        //TODO: Read sketch configuration from an external place instead of using a config file
        Map<Integer, Integer> hash_functions =
                Sketch.buildHashFunctions(sketchConfig.getRows(),10000);
        return new Sketch(sketchConfig.getRows(),
                sketchConfig.getCols(),
                sketchConfig.getPrime(),
                hash_functions);
    }

    protected void rotateSketch()  {
        try {
            this.sketchCache.invalidate(0);
            this.sketchCache.get(0);
        } catch (ExecutionException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public Mapping addEvent(String event) throws ExecutionException {
        Integer mapping = keyValueTransformer.getIntegerFromString(event);
        sketchCache.get(0).addElement(mapping);
        return new Mapping(event, String.valueOf(mapping));

    }

}
