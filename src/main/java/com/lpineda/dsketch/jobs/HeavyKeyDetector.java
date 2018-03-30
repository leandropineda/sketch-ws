package com.lpineda.dsketch.jobs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.core.*;
import com.lpineda.dsketch.data.KeyValueTransformer;
import com.sun.org.apache.xerces.internal.xs.StringList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leandro on 26/10/17.
 */

public class HeavyKeyDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetector.class);

    private DetectionParameters detectionParameters;
    private KeyValueTransformer keyValueTransformer;
    private SketchHistoryQueue sketchHistoryQueue;
    private HeavyKeysHistoryQueue heavyKeysHistoryQueue;
    private SketchManager sketchManager;
    @JsonProperty
    private Integer detectedHeavyHitters = 0;
    @JsonProperty
    private Integer detectedHeavyChangers = 0;

    public HeavyKeyDetector(DetectionParameters detectionParameters,
                            KeyValueTransformer keyValueTransformer,
                            SketchHistoryQueue sketchHistoryQueue,
                            HeavyKeysHistoryQueue heavyKeysHistoryQueue,
                            SketchManager sketchManager) {
        this.detectionParameters = detectionParameters;
        this.keyValueTransformer = keyValueTransformer;
        this.sketchHistoryQueue = sketchHistoryQueue;
        this.heavyKeysHistoryQueue = heavyKeysHistoryQueue;
        this.sketchManager = sketchManager;
    }

//    public void setHeavyKeyDetectionHistory(HeavyKeys heavyKeys) {
//        LOGGER.info(MessageFormat.format("Initializing {0}", HeavyKeyDetector.class.getName()));
//        this.heavyKeys = heavyKeys;
//    }

    public void detectHeavyKeys() {
        if (this.sketchHistoryQueue.size() < 2) {
            LOGGER.info(MessageFormat.format("Not enough data to detect heavy keys: {0} sketch(es) available.",
                    sketchHistoryQueue.size()));
            return;
        }
        Sketch sketch = this.sketchHistoryQueue.getSketch(0);
        Integer heavyHitterThreshold = detectionParameters.getHeavyHitterThreshold();
        Set<Integer> heavyHittersInt = sketch.getHeavyHitters(heavyHitterThreshold);
        Sketch old_sketch = this.sketchHistoryQueue.getSketch(1);
        Integer heavyChangerThreshold = detectionParameters.getHeavyChangerThreshold();
        Set<Integer> heavyChangersInt = sketch.getHeavyChangers(heavyChangerThreshold, old_sketch);

        this.detectedHeavyHitters += heavyHittersInt.size();
        this.detectedHeavyChangers += heavyChangersInt.size();

        Set<String> heavyHittersString = keyValueTransformer.getEvent(heavyHittersInt);
        Set<String> heavyChangersString = keyValueTransformer.getEvent(heavyChangersInt);

        Integer epoch = sketchManager.getCurrentEpoch().intValue() - 1;
        LOGGER.info("["+ epoch +"]" +
                " Heavy hitters: " + heavyHittersString +
                " Heavy changers: "  + heavyChangersString);

        if (this.heavyKeysHistoryQueue != null) {
            Date date = new Date();
            HeavyKeys heavyKeys = new HeavyKeys(date, new ArrayList<>(heavyHittersString), new ArrayList<>(heavyChangersString));
            this.heavyKeysHistoryQueue.addHeavyKeys(heavyKeys, epoch);
        }

    }

    @JsonProperty
    public Integer getEventsProcessingSpeed() {
        return Math.round(this.sketchManager.getEventsProcessingSpeed() / this.detectionParameters.getSketchRotationInterval());
    }
}
