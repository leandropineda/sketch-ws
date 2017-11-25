package com.lpineda.dsketch.core;

import com.google.common.cache.*;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.db.KeyValueTransformer;
import org.apache.commons.math3.primes.Primes;
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

    private final LoadingCache<Integer, Sketch> currentSketch;

    private final Map<Integer, Integer> hash_functions;

    public SketchManager(SketchConfig sketchConfig,
                         RotationListener rotationListener,
                         KeyValueTransformer keyValueTransformer) throws Exception {
        this.sketchConfig = sketchConfig;
        this.keyValueTransformer = keyValueTransformer;

        LOGGER.info(MessageFormat.format("Initializing {0}", SketchManager.class.getName()));
        LOGGER.info(String.format("Using sketch with \'rows\': %d, \'cols\': %d and \'prime\': %d",
                sketchConfig.getRows(), sketchConfig.getCols(), sketchConfig.getPrime()));

        if (!Primes.isPrime(sketchConfig.getPrime())) {
            throw new Exception("Parameter \'prime\' is not a prime number. Change it by editing the configuration file.");
        }

        if (sketchConfig.getCols() > sketchConfig.getPrime()) {
            throw new Exception("Sketch \'cols\' must be minor than \'prime\'. Change it by editing the configuration file.");
        }

        RemovalListener<Integer, Sketch> listener = new RemovalListener<Integer, Sketch>() {
            public void onRemoval(RemovalNotification<Integer, Sketch> notification) {
                rotationListener.onRotation(notification.getValue());
            }
        };

        this.hash_functions =
                Sketch.buildHashFunctions(sketchConfig.getRows(),10000);
        LOGGER.info("Using hash functions: " + hash_functions.toString());


        CacheLoader<Integer, Sketch> loader = new CacheLoader<Integer, Sketch>() {
            public Sketch load(Integer key) throws Exception {
                return buildSketch();
            }
        };

        currentSketch = CacheBuilder.newBuilder()
                .maximumSize(1)
                .removalListener(listener)
                .build(loader);

    }

    public Sketch buildSketch() {
        //TODO: Read sketch configuration from an external place instead of using a config file
        return new Sketch(sketchConfig.getRows(),
                sketchConfig.getCols(),
                sketchConfig.getPrime(),
                this.hash_functions);
    }

    public Sketch getCurrentSketch() throws ExecutionException {
        return this.currentSketch.get(0);
    }

    protected void rotateSketch() throws ExecutionException {
        this.currentSketch.invalidate(0);
        this.currentSketch.get(0);
    }

    public Mapping addEvent(String event) throws ExecutionException {
        Integer value = keyValueTransformer.getValue(event);
        this.getCurrentSketch().addElement(value);
        return new Mapping(event, String.valueOf(value));
    }

}
