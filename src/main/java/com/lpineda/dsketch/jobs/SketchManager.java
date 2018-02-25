package com.lpineda.dsketch.jobs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.*;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.core.Sketch;
import com.lpineda.dsketch.data.KeyValueTransformer;
import org.apache.commons.math3.primes.Primes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by leandro on 25/10/17.
 */

public class SketchManager {

    public interface RotationListener {
        void onRotation(Sketch sketch, Integer epoch);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchManager.class);

    private final SketchConfig sketchConfig;
    private final KeyValueTransformer keyValueTransformer;
    private final LoadingCache<Integer, Sketch> currentSketch;
    private final Map<Integer, Integer> hash_functions;

    private final AtomicLong processedEvents = new AtomicLong();
    private final AtomicLong epoch = new AtomicLong();

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
                rotationListener.onRotation(notification.getValue(), epoch.intValue());
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

    @JsonIgnore
    public Sketch getCurrentSketch() throws ExecutionException {
        return this.currentSketch.get(0);
    }

    public void rotateSketch() throws ExecutionException {
        this.currentSketch.invalidate(0);
        this.currentSketch.get(0);
        this.epoch.incrementAndGet();
    }

    public Mapping addEvent(String event) {
        Integer value = keyValueTransformer.getValue(event);
        try {
            this.getCurrentSketch().addElement(value);
        } catch (ExecutionException ex) {
            LOGGER.error("Execution exception thrown. Retrying.");
            ex.printStackTrace();
            this.addEvent(event);
        }
        this.processedEvents.incrementAndGet();
        return new Mapping(event, String.valueOf(value));
    }

    @JsonProperty
    public Long getProcessedEvents() {
        return this.processedEvents.longValue();
    }

    @JsonProperty
    public Long getCurrentEpoch() {
        return this.epoch.longValue();
    }

}
