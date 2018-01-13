package com.lpineda.dsketch.health;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.data.BrokerClient;

/**
 * Created by leandro on 11/10/17.
 */
public class MosquittoHealthCheck extends HealthCheck {

    private final BrokerClient brokerClient;

    public MosquittoHealthCheck(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;

    }
    @Override
    protected Result check() throws Exception {
        try {
            if (!brokerClient.healthy()) {
                throw new Exception("Couldn't connect to mosquitto broker");
            }
        } catch (Exception ex) {
            return Result.unhealthy(ex.getMessage());
        }
        return Result.healthy();
    }

}
