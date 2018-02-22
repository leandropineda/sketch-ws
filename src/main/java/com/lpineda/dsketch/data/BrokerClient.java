package com.lpineda.dsketch.data;

import com.lpineda.dsketch.api.MessageBrokerConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BrokerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerClient.class);

    private final MqttClient client;

    public BrokerClient(MessageBrokerConfig messageBrokerConfig, MessageManager messageManager) throws Exception {

        LOGGER.info("Initializing MqttClient");

        client = new MqttClient(messageBrokerConfig.getAddress(), MqttClient.generateClientId());

        client.setCallback(messageManager);
        client.connect();
        client.subscribe("events");

    }

    public boolean healthy () {
        return client.isConnected();
    }

}
