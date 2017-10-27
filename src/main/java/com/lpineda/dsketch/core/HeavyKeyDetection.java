package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.db.EventMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Created by leandro on 26/10/17.
 */

public class HeavyKeyDetection {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeavyKeyDetection.class);

    private SketchParameters sketchParameters;
    private EventMapping eventMapping;
    private SketchHistory sketchHistory;

    public HeavyKeyDetection(SketchParameters sketchParameters) {
        this.sketchParameters = sketchParameters;
    }

    public void setEventMapping(EventMapping eventMapping) {
        this.eventMapping = eventMapping;
    }

    public void setSketchHistory(SketchHistory sketchHistory) {
        this.sketchHistory = sketchHistory;
    }

    public void detectHeavyKeys() {
        LOGGER.info("Sketch History " + sketchHistory.getCounter());
        if (this.sketchHistory.getCounter() < 2) {
            return;
        }
        Sketch sketch = this.sketchHistory.getSketch(0);
        Sketch old_sketch = this.sketchHistory.getSketch(1);
        HashSet<Integer> heavy_hitters =
                sketch.getHeavyHitters(sketchParameters.getHeavyHitterThreshold());
        HashSet<Integer> heavy_changers =
                sketch.getHeavyChangers(sketchParameters.getHeavyChangerThreshold(), old_sketch);
        LOGGER.info("["+ sketchHistory.getCounter() +"]" +
                " Heavy hitters: " + eventMapping.getMappings(heavy_hitters) +
                " Heavy changers: " + eventMapping.getMappings(heavy_changers));

    }
}
