package com.lpineda.dsketch;

import com.lpineda.dsketch.db.EventMapping;
import com.lpineda.dsketch.db.SketchFactory;
import com.lpineda.dsketch.health.EventMappingHealthCheck;
import com.lpineda.dsketch.resources.EventResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by leandro on 02/09/17.
 */
public class EventReceiverApplication extends Application<EventReceiverConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverApplication.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        EventMapping mappings = configuration.getEventMapping();
        mappings.connect();
        LOGGER.info("Initializing Sketch factory");
        SketchFactory sketchFactory = new SketchFactory();
        sketchFactory.setSketchParameters(configuration.getSketchParameters());
        sketchFactory.setMappings(mappings);

        Long sketchKeepAlive = configuration.getSketchParameters().getSketch_clean_up_interval();
        scheduler.scheduleAtFixedRate(sketchFactory, sketchKeepAlive, sketchKeepAlive, SECONDS);

        final EventResource resource = new EventResource(sketchFactory);
//        environment.healthChecks().register("SketchFactory", new SketchFactoryHealthCheck(sketchFactory));
        environment.healthChecks().register("EventMapping", new EventMappingHealthCheck(mappings));
        environment.jersey().register(resource);

    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
