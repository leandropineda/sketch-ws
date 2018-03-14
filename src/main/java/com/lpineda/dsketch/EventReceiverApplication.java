package com.lpineda.dsketch;

import com.lpineda.dsketch.api.*;
import com.lpineda.dsketch.core.*;
import com.lpineda.dsketch.data.*;
import com.lpineda.dsketch.health.MosquittoHealthCheck;
import com.lpineda.dsketch.health.RedisHealthCheck;
import com.lpineda.dsketch.jobs.DetectionScheduler;
import com.lpineda.dsketch.jobs.HeavyKeyDetector;
import com.lpineda.dsketch.jobs.SketchManager;
import com.lpineda.dsketch.resources.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
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


        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        try {
            // Get config parameters
            SketchConfig sketchConfig = configuration.getSketchConfig();
            DetectionParameters detectionParameters = configuration.getDetectionParameters();

            MessageBrokerConfig messageBrokerConfig = configuration.getMessageBrokerConfig();

            // Database manager
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

            //Initialize Sketch History
            SketchHistoryQueue sketchHistoryQueue = new SketchHistoryQueue(
                    detectionParameters.getMaxHistoryQueueLength().intValue());

            SketchManager.RotationListener rotationListener = new SketchManager.RotationListener() {
                @Override
                public void onRotation(final Sketch sketch, final Integer epoch) {
                    sketchHistoryQueue.addSketch(sketch, epoch);
                }
            };

            //Sketch manager
            SketchManager sketchManager = new SketchManager(sketchConfig,
                    rotationListener,
                    keyValueTransformer);

            // Heavy key history
            HeavyKeysHistoryQueue heavyKeysHistoryQueue = new HeavyKeysHistoryQueue(
                    detectionParameters.getMaxHistoryQueueLength().intValue());
            //Heavy key detector
            HeavyKeyDetector heavyKeyDetector = new HeavyKeyDetector(detectionParameters,
                    keyValueTransformer,
                    sketchHistoryQueue,
                    heavyKeysHistoryQueue,
                    sketchManager);

            DetectionScheduler detectionScheduler = new DetectionScheduler(detectionParameters, sketchManager, heavyKeyDetector);
            detectionScheduler.start();


            BrokerClient brokerClient = new BrokerClient(messageBrokerConfig, sketchManager);

            environment.healthChecks().register("Redis", new RedisHealthCheck(redisManager));
            environment.healthChecks().register("Mosquitto", new MosquittoHealthCheck(brokerClient));
            environment.jersey().register(new HeavyKeysHistoryResource(heavyKeysHistoryQueue));
            environment.jersey().register(new SketchHistoryResource(sketchHistoryQueue));
            environment.jersey().register(new Health(environment.healthChecks()));
            environment.jersey().register(
                    new Status(sketchConfig, detectionParameters, sketchManager, heavyKeyDetector));

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }


    }

    public static void main(String[] args) throws Exception {
        new EventReceiverApplication().run(args);
    }

}
