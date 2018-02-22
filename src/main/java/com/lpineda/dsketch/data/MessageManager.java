package com.lpineda.dsketch.data;

import com.lpineda.dsketch.api.Mapping;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageManager implements MqttCallback {
    private final Logger LOGGER = LoggerFactory.getLogger(MessageManager.class);

    private final MessageReceiver messageReceiver;

    public MessageManager(MessageReceiver messageReceiver) {
        LOGGER.info("Initializing Mosquitto MQTT callback.");
        this.messageReceiver = messageReceiver;

    }

    public void connectionLost(Throwable throwable) {
        LOGGER.error("Connection to MQTT broker lost!");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        String evt = new String(mqttMessage.getPayload());
        Mapping return_mapping;
        //LOGGER.info("Message received: "+ evt);
        return_mapping = messageReceiver.onMessage(evt);
        //LOGGER.debug("Event " + return_mapping.getEvent() + " is mapped with " + return_mapping.getMapping());
    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // not used in this example
    }

}
