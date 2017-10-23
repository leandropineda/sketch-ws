package com.lpineda.dsketch;

import com.lpineda.dsketch.core.Sketch;
import com.lpineda.dsketch.db.EventMapping;
import com.lpineda.dsketch.db.SketchFactory;
import com.lpineda.dsketch.health.DetectionParametersHealthCheck;
import com.lpineda.dsketch.health.EventMappingHealthCheck;
import com.lpineda.dsketch.health.SketchFactoryHealthCheck;
import com.lpineda.dsketch.resources.EventResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by leandro on 02/09/17.
 */
public class EventReceiverApplication extends Application<EventReceiverConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverApplication.class);

    private EventMapping mappings;
    private SketchFactory sketchFactory;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Sketch old_sketch;

    private AtomicLong sketch_counter = new AtomicLong(0);

    @Override
    public String getName() {
        return "events-ws";
    }

    @Override
    public void initialize(Bootstrap<EventReceiverConfiguration> bootstrap) {
    }

    @Override
    public void run(EventReceiverConfiguration configuration, Environment environment) {

        LOGGER.info("Initializing DB connection");
        mappings = configuration.getEventMapping();
        mappings.connect();
        LOGGER.info("Initializing Sketch factory");
        sketchFactory = configuration.getSketchFactory();
        sketchFactory.setMappings(mappings);
        try {
            old_sketch = sketchFactory.getSketch();
        } catch (ExecutionException ex) {
            LOGGER.error(ex.getMessage());
        }

        final Runnable cleanUp = new Runnable() {
            public void run() {

                try {
                    Sketch sketch = sketchFactory.getSketch();
                    HashSet<Integer> heavy_hitters =
                            sketch.getHeavyHitters(configuration.getDetectionParameters().getHeavy_hitter_threshold());
                    HashSet<Integer> heavy_changers =
                            sketch.getHeavyChangers(configuration.getDetectionParameters().getHeavy_changer_threshold(), old_sketch);
                    LOGGER.info("["+ sketch_counter.getAndIncrement() +"]" +
                            " Heavy hitters: " + mappings.getMappings(heavy_hitters) +
                            " Heavy changers: " + mappings.getMappings(heavy_changers));
                    old_sketch = sketch;

                    sketchFactory.invalidateSketch();
                } catch (Exception ex ) {
                    LOGGER.error(ex.getMessage());
                }

            }
        };

        Long sketchKeepAlive = configuration.getDetectionParameters().getSketch_clean_up_interval();
        scheduler.scheduleAtFixedRate(cleanUp, sketchKeepAlive, sketchKeepAlive, SECONDS);

        final EventResource resource = new EventResource(sketchFactory);
        environment.healthChecks().register("SketchFactory", new SketchFactoryHealthCheck(sketchFactory));
        environment.healthChecks().register("EventMapping", new EventMappingHealthCheck(mappings));
        environment.healthChecks().register("DetectionParameters", new DetectionParametersHealthCheck(configuration.getDetectionParameters()));
        environment.jersey().register(resource);

    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
