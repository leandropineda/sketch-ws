package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.api.HeavyKeysList;
import com.lpineda.dsketch.db.KeyValueTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by leandro on 26/10/17.
 */

public class HeavyKeyDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetector.class);

    private DetectionParameters detectionParameters;
    private KeyValueTransformer keyValueTransformer;
    private SketchHistory sketchHistory;
    private HeavyKeyDetectionHistory heavyKeyDetectionHistory = null;

    public HeavyKeyDetector(DetectionParameters detectionParameters,
                            KeyValueTransformer keyValueTransformer,
                            SketchHistory sketchHistory) {
        this.detectionParameters = detectionParameters;
        this.keyValueTransformer = keyValueTransformer;
        this.sketchHistory = sketchHistory;
    }

    public void setHeavyKeyDetectionHistory(HeavyKeyDetectionHistory heavyKeyDetectionHistory) {
        this.heavyKeyDetectionHistory = heavyKeyDetectionHistory;
    }

    public void detectHeavyKeys() {
        if (this.sketchHistory.getCounter() < 2) {
            LOGGER.info(MessageFormat.format("Not enough data to detect heavy keys: {0} sketch(es) available.",
                    sketchHistory.getCounter()));
            return;
        }
        Sketch sketch = this.sketchHistory.getSketch(0);
        Integer heavyHitterThreshold = detectionParameters.getHeavyHitterThreshold();
        Set<Integer> heavyHittersInt = sketch.getHeavyHitters(heavyHitterThreshold);

        Sketch old_sketch = this.sketchHistory.getSketch(1);
        Integer heavyChangerThreshold = detectionParameters.getHeavyChangerThreshold();
        Set<Integer> heavyChangersInt = sketch.getHeavyChangers(heavyChangerThreshold, old_sketch);

        Set<String> heavyHittersString = keyValueTransformer.getStringFromInteger(heavyHittersInt);
        Set<String> heavyChangersString = keyValueTransformer.getStringFromInteger(heavyChangersInt);

        LOGGER.info("["+ sketchHistory.getCounter() +"]" +
                " Heavy hitters: " + heavyHittersString +
                " Heavy changers: "  + heavyChangersString);

        if (this.heavyKeyDetectionHistory != null) {
            Date date = this.sketchHistory.getSketchDate(0);
            HeavyKeysList heavyHitterList = new HeavyKeysList(heavyHittersString, date);
            HeavyKeysList heavyChangerList = new HeavyKeysList(heavyChangersString, date);
            this.heavyKeyDetectionHistory.addHeavyKeys(heavyHitterList, heavyChangerList);
        }

    }
}
