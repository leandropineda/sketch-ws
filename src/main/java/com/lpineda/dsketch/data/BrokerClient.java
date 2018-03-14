package com.lpineda.dsketch.data;

import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.api.MessageBrokerConfig;
import com.lpineda.dsketch.jobs.SketchManager;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BrokerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerClient.class);

    private final MessageBrokerConfig messageBrokerConfig;
    private MqttClient client;
    private MqttCallback messageCallback;

    public BrokerClient(MessageBrokerConfig messageBrokerConfig, SketchManager sketchManager) {

        LOGGER.info("Initializing MqttClient");
        this.messageBrokerConfig = messageBrokerConfig;
        this.messageCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                LOGGER.info("Mqtt connection lost.");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                String evt = new String(mqttMessage.getPayload());
                sketchManager.addEvent(evt);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

        this.connect();

        Runnable checkHealth = new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        };

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(checkHealth, 1, 1, TimeUnit.SECONDS);
    }

    private void connect()  {
        LOGGER.info(String.format("Connecting to: %s", messageBrokerConfig.getAddress()));
        try {
            client = new MqttClient(messageBrokerConfig.getAddress(), MqttClient.generateClientId());
            client.setCallback(this.messageCallback);
            client.connect();
            String topic = "events";
            LOGGER.info(String.format("Subscribing to topic %s", topic));
            client.subscribe(topic);
        } catch (MqttException ex) {
            LOGGER.error("Mqtt exception: " + ex.getMessage());
        }
    }

    public boolean healthy () {
        return client.isConnected();
    }


}
