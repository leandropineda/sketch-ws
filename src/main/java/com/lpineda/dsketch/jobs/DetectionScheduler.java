package com.lpineda.dsketch.jobs;

import com.lpineda.dsketch.api.DetectionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
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
        LOGGER.info(MessageFormat.format("Initializing {0}", DetectionScheduler.class.getName()));

        this.detectionParameters = detectionParameters;
        this.sketchManager = sketchManager;
        this.heavyKeyDetector = heavyKeyDetector;
        LOGGER.info("Detection scheduler parameters:");
        LOGGER.info(String.format("Heavy hitter threshold: %d", this.detectionParameters.getHeavyHitterThreshold()));
        LOGGER.info(String.format("Heavy changer threshold: %d", this.detectionParameters.getHeavyChangerThreshold()));
        LOGGER.info(String.format("Sketch rotation interval: %d", this.detectionParameters.getSketchRotationInterval()));
    }

    public void start() {
        final Runnable detectHeavyKeys = new Runnable() {
            public void run() {
                try {
                    sketchManager.rotateSketch();
                    heavyKeyDetector.detectHeavyKeys();
                } catch (ExecutionException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        };

        scheduler.scheduleAtFixedRate(detectHeavyKeys,
                detectionParameters.getSketchRotationInterval(),
                detectionParameters.getSketchRotationInterval(),
                SECONDS);

    }
}
