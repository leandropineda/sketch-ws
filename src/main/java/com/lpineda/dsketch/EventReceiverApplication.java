package com.lpineda.dsketch;

import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.core.*;
import com.lpineda.dsketch.db.RedisManager;
import com.lpineda.dsketch.db.KeyValueTransformer;
import com.lpineda.dsketch.health.RedisHealthCheck;
import com.lpineda.dsketch.resources.EventResource;
import com.lpineda.dsketch.resources.Health;
import com.lpineda.dsketch.resources.HeavyKeysResource;
import com.lpineda.dsketch.resources.Status;
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

    private void doLogo() {
        LOGGER.info("\n" +
                "                                       ╓▓▌                                      \n" +
                "                                      ╒▓▓▓▌                                     \n" +
                "                                      ▓▓▓▓▓╕                                    \n" +
                "                                     ║▓▓▓▓▓▓                                    \n" +
                "                                     ▓▓▓▓▓▓▓▌                                   \n" +
                "                                    ║▓▓▓▓▓▓▓▒╕                                  \n" +
                "                                    ▓▓▓▓▓▓▓▓▓▓                                  \n" +
                "                                   ╫▓▓▓▓▓▓▓▓▓▓ε                                 \n" +
                "                                   ▓▓▓▓▓▓▓▓▓▓▓▓                                 \n" +
                "                                  ╣▓▓▓▓▓▓▓▓▓▓▓▓▌                                \n" +
                "                                 ]▓▓▓▓▓▓▓▓▓▓▓▓▓▓⌐                               \n" +
                "                                 ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▌                               \n" +
                "                                ]▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓╕                              \n" +
                "                                ▓▓▓▓▀░▓▓▓▓▓▓▓▓▓▓▓▓                              \n" +
                "                               ╟▓▓▓▀░░░▓▓▓▓▓▓▓▓▓▓▓▌                             \n" +
                "                              ]▓▓▓▓▓░░║▓▓▓▓▓▓▓▓▓▓▓▓⌐                            \n" +
                "                              ▓▓▓▓▓▓▌║▓▓▓▓▓▓▓▓▓▓▓▓▓▌                            \n" +
                "                             ▐▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓╕                           \n" +
                "                             ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                           \n" +
                "                            ╫▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▀▓▓▒▌                          \n" +
                "                           ╓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▀▓▓▓                          \n" +
                "                           ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░▓▓▓▌                         \n" +
                "                          ╟▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░╣▓▓▓▒╕                        \n" +
                "                         ╒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▌╣▓▓▓▓▓▓                        \n" +
                "                         ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▌                       \n" +
                "                        ▐▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                       \n" +
                "                        ▓▓▓▓▓▓▓░╟▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▌                      \n" +
                "                       ╣▓▓▓▓▓▓░░░╟▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒╕                     \n" +
                "                      ]▓▓▓▓▓▓▓▌░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                     \n" +
                "                      ▓▓▓▓▓▓▓▓▓░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▄                    \n" +
                "                     ╫▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                    \n" +
                "                    ╒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓╬▓▓▒▌                   \n" +
                "                   ,▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓∩                  \n" +
                "                  '╟▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▀░░░                  \n" +
                "                    ░░░▀▀█▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▀▀░░░░'                   \n" +
                "                         '\"░░░▀▀▀▀▀▀████████▀▀▀▀▀▀░░░░'");

    }
    @Override
    public String getName() {
        return "events-ws";
    }

    @Override
    public void initialize(Bootstrap<EventReceiverConfiguration> bootstrap) {
    }

    @Override
    public void run(EventReceiverConfiguration configuration, Environment environment) {

        this.doLogo();
        try {
            SketchConfig sketchConfig = configuration.getSketchConfig();
            DetectionParameters detectionParameters = configuration.getDetectionParameters();
            RedisManager redisManager = new RedisManager(configuration.getDatabaseConfig().getAddress(),
                    configuration.getDatabaseConfig().getCacheSize());

            KeyValueTransformer keyValueTransformer = new KeyValueTransformer() {
                @Override
                public Integer getValue(String event) {
                    return redisManager.getValue(event);
                }

                @Override
                public Set<String> getEvent(Set<Integer> value) {
                    return redisManager.getStringMappings(value);
                }
            };

            SketchHistory sketchHistory = new SketchHistory(2);

            SketchManager sketchManager = new SketchManager(sketchConfig,
                    new SketchManager.RotationListener() {
                        @Override
                        public void onRotation(final Sketch sketch) {
                            sketchHistory.addSketch(sketch);
                        }
                    }, keyValueTransformer);

            HeavyKeyDetectionHistory heavyKeyDetectionHistory = new HeavyKeyDetectionHistory(detectionParameters.getHeavyKeyHistoryMaxLength());
            HeavyKeyDetector heavyKeyDetector = new HeavyKeyDetector(detectionParameters, keyValueTransformer, sketchHistory);
            heavyKeyDetector.setHeavyKeyDetectionHistory(heavyKeyDetectionHistory);

            DetectionScheduler detectionScheduler = new DetectionScheduler(detectionParameters, sketchManager, heavyKeyDetector);
            detectionScheduler.start();
            environment.healthChecks().register("Redis", new RedisHealthCheck(redisManager));
            environment.jersey().register(new EventResource(sketchManager));
            environment.jersey().register(new HeavyKeysResource(heavyKeyDetectionHistory));
            environment.jersey().register(new Health(environment.healthChecks()));
            environment.jersey().register(new Status(sketchConfig, detectionParameters, sketchHistory));

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            System.exit(1);
        }


    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
