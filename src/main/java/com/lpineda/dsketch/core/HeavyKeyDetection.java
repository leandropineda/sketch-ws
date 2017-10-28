package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.db.KeyValueTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Created by leandro on 26/10/17.
 */

public class HeavyKeyDetection {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetection.class);

    private SketchParameters sketchParameters;
    private KeyValueTransformer keyValueTransformer;
    private SketchHistory sketchHistory;

    public HeavyKeyDetection(SketchParameters sketchParameters,
                             KeyValueTransformer keyValueTransformer,
                             SketchHistory sketchHistory) {
        this.sketchParameters = sketchParameters;
        this.keyValueTransformer = keyValueTransformer;
        this.sketchHistory = sketchHistory;
    }


    public void detectHeavyKeys() {
        if (this.sketchHistory.getCounter() < 2) {
            LOGGER.info("Not enough data to detect heavy keys.");
            return;
        }
        Sketch sketch = this.sketchHistory.getSketch(0);
        Sketch old_sketch = this.sketchHistory.getSketch(1);
        HashSet<Integer> heavy_hitters =
                sketch.getHeavyHitters(sketchParameters.getHeavyHitterThreshold());
        HashSet<Integer> heavy_changers =
                sketch.getHeavyChangers(sketchParameters.getHeavyChangerThreshold(), old_sketch);
        LOGGER.info("["+ sketchHistory.getCounter() +"]" +
                " Heavy hitters: " + keyValueTransformer.getStringFromInteger(heavy_hitters) +
                " Heavy changers: "  + keyValueTransformer.getStringFromInteger(heavy_changers));

    }
}
