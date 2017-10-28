package com.lpineda.dsketch;

import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.core.*;
import com.lpineda.dsketch.db.RedisManager;
import com.lpineda.dsketch.db.KeyValueTransformer;
import com.lpineda.dsketch.health.StringMappingHealthCheck;
import com.lpineda.dsketch.health.SketchParametersHealthCheck;
import com.lpineda.dsketch.resources.EventResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by leandro on 02/09/17.
 */
public class EventReceiverApplication extends Application<EventReceiverConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverApplication.class);

    @Override
    public String getName() {
        return "events-ws";
    }

    @Override
    public void initialize(Bootstrap<EventReceiverConfiguration> bootstrap) {
    }

    @Override
    public void run(EventReceiverConfiguration configuration, Environment environment) {

        SketchParameters sketchParameters = configuration.getSketchParameters();
        LOGGER.info("Initializing DB connection");
        RedisManager redisManager = new RedisManager(configuration.getDbConfig().getAddress());

        KeyValueTransformer keyValueTransformer = new KeyValueTransformer() {
            @Override
            public Integer getIntegerFromString(String value) {
                return redisManager.getValue(value);
            }

            @Override
            public Set<String> getStringFromInteger(Set<Integer> values) {
                return redisManager.getStringMappings(values);
            }
        };

        LOGGER.info("Initializing Sketch Manager");
        SketchHistory sketchHistory = new SketchHistory();

        SketchManager sketchManager = new SketchManager(sketchParameters,
                new SketchManager.RotationListener() {
                    @Override
                    public void onRotation(final Sketch sketch) {
                        sketchHistory.addSketch(sketch);
                    }
                }, keyValueTransformer);

        HeavyKeyDetection heavyKeyDetection = new HeavyKeyDetection(sketchParameters, keyValueTransformer, sketchHistory);

        DetectionScheduler detectionScheduler = new DetectionScheduler(sketchParameters, sketchManager, heavyKeyDetection);
        detectionScheduler.start();

        final EventResource resource = new EventResource(sketchManager);
        environment.healthChecks().register("SketchParameters", new SketchParametersHealthCheck(sketchParameters));
        environment.healthChecks().register("RedisManager", new StringMappingHealthCheck(redisManager));
        environment.jersey().register(resource);

    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
