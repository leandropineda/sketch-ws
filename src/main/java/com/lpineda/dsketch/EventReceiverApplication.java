package com.lpineda.dsketch;

import com.lpineda.dsketch.core.Sketch;
import com.lpineda.dsketch.db.RedisMappings;
import com.lpineda.dsketch.db.SketchCache;
import com.lpineda.dsketch.resources.EventResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by leandro on 02/09/17.
 */
public class EventReceiverApplication extends Application<EventReceiverConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventReceiverApplication.class);

    private Jedis jedis = new Jedis("0.0.0.0");
    private RedisMappings mappings = new RedisMappings(jedis);
    private SketchCache sketchCache = new SketchCache(mappings);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public String getName() {
        return "events-ws";
    }

    @Override
    public void initialize(Bootstrap<EventReceiverConfiguration> bootstrap) {

        final Runnable cleanUp = new Runnable() {
            public void run() {
                try {
                    Sketch sketch = sketchCache.getSketch();
                    HashSet<Integer> heavy_hitters = sketch.getHeavyHitters((int)Math.round(sketch.getBiggestBucketCounter() * .8));
                    LOGGER.info("Heavy hitters: " + mappings.getMappings(heavy_hitters));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                sketchCache.invalidateSketch();
            }
        };

        Long sketchKeepAlive = (long)2;
        scheduler.scheduleAtFixedRate   (cleanUp, sketchKeepAlive, sketchKeepAlive, SECONDS);

    }

    @Override
    public void run(EventReceiverConfiguration configuration, Environment environment) {
        final EventResource resource = new EventResource(this.sketchCache);
        environment.jersey().register(resource);
    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
