package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.SketchParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by leandro on 26/10/17.
 */
public class SketchScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SketchScheduler.class);

    private SketchParameters sketchParameters;
    private SketchManager sketchManager;
    private HeavyKeyDetection heavyKeyDetection;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SketchScheduler(SketchParameters sketchParameters) {
        this.sketchParameters = sketchParameters;
    }

    public void setSketchManager(SketchManager sketchManager) {
        this.sketchManager = sketchManager;
    }

    public void setHeavyKeyDetection(HeavyKeyDetection heavyKeyDetection) {
        this.heavyKeyDetection = heavyKeyDetection;
    }

    public void start() {
        final Runnable invalidate_sketch = new Runnable() {
            public void run() {
                LOGGER.info("Invalidating sketch");
                try {
                    sketchManager.invalidateSketch();
                } catch (ExecutionException ex) {
                    LOGGER.error(ex.getMessage());
                }

                heavyKeyDetection.detectHeavyKeys();
            }
        };

        scheduler.scheduleAtFixedRate(invalidate_sketch,
                sketchParameters.getSketchCleanUpInterval(),
                sketchParameters.getSketchCleanUpInterval(),
                SECONDS);

    }
}
