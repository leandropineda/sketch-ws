package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.SketchParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by leandro on 26/10/17.
 */
public class DetectionScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectionScheduler.class);

    private final SketchParameters sketchParameters;
    private final SketchManager sketchManager;
    private HeavyKeyDetection heavyKeyDetection;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DetectionScheduler(SketchParameters sketchParameters,
                              SketchManager sketchManager,
                              HeavyKeyDetection heavyKeyDetection) {
        this.sketchParameters = sketchParameters;
        this.sketchManager = sketchManager;
        this.heavyKeyDetection = heavyKeyDetection;
    }

    public void start() {
        final Runnable detectHeavyKeys = new Runnable() {
            public void run() {
                sketchManager.rotateSketch();
                heavyKeyDetection.detectHeavyKeys();
            }
        };

        scheduler.scheduleAtFixedRate(detectHeavyKeys,
                sketchParameters.getSketchCleanUpInterval(),
                sketchParameters.getSketchCleanUpInterval(),
                SECONDS);

    }
}
