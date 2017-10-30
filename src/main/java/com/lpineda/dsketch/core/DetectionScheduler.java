package com.lpineda.dsketch.core;

import com.lpineda.dsketch.api.DetectionParameters;
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

    private final DetectionParameters detectionParameters;
    private final SketchManager sketchManager;
    private HeavyKeyDetector heavyKeyDetector;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DetectionScheduler(DetectionParameters detectionParameters,
                              SketchManager sketchManager,
                              HeavyKeyDetector heavyKeyDetector) {
        this.detectionParameters = detectionParameters;
        this.sketchManager = sketchManager;
        this.heavyKeyDetector = heavyKeyDetector;
    }

    public void start() {
        final Runnable detectHeavyKeys = new Runnable() {
            public void run() {
                sketchManager.rotateSketch();
                heavyKeyDetector.detectHeavyKeys();
            }
        };

        scheduler.scheduleAtFixedRate(detectHeavyKeys,
                detectionParameters.getSketchRotationInterval(),
                detectionParameters.getSketchRotationInterval(),
                SECONDS);

    }
}
