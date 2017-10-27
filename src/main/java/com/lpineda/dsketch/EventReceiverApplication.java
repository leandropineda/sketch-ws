package com.lpineda.dsketch;

import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.core.HeavyKeyDetection;
import com.lpineda.dsketch.core.SketchHistory;
import com.lpineda.dsketch.core.SketchManager;
import com.lpineda.dsketch.core.SketchScheduler;
import com.lpineda.dsketch.db.EventMapping;
import com.lpineda.dsketch.health.EventMappingHealthCheck;
import com.lpineda.dsketch.health.SketchParametersHealthCheck;
import com.lpineda.dsketch.resources.EventResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        EventMapping eventMapping = configuration.getEventMapping();
        eventMapping.connect();
        LOGGER.info("Initializing Sketch Manager");
        SketchHistory sketchHistory = new SketchHistory();

        SketchManager sketchManager = new SketchManager(sketchParameters);
        sketchManager.setSketchHistory(sketchHistory);
        sketchManager.setEventMapping(eventMapping);

        HeavyKeyDetection heavyKeyDetection = new HeavyKeyDetection(sketchParameters);
        heavyKeyDetection.setEventMapping(eventMapping);
        heavyKeyDetection.setSketchHistory(sketchHistory);

        SketchScheduler sketchScheduler = new SketchScheduler(sketchParameters);
        sketchScheduler.setSketchManager(sketchManager);
        sketchScheduler.setHeavyKeyDetection(heavyKeyDetection);
        sketchScheduler.start();

        final EventResource resource = new EventResource(sketchManager);
        environment.healthChecks().register("SketchParameters", new SketchParametersHealthCheck(sketchParameters));
        environment.healthChecks().register("EventMapping", new EventMappingHealthCheck(eventMapping));
        environment.jersey().register(resource);

    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
