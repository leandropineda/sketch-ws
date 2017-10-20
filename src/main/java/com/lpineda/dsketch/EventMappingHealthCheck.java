package com.lpineda.dsketch;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.db.EventMapping;

/**
 * Created by leandro on 11/10/17.
 */
public class EventMappingHealthCheck extends HealthCheck {

    private final EventMapping mappings;

    public EventMappingHealthCheck(EventMapping mappings) {
        this.mappings = mappings;

    }
    @Override
    protected Result check() throws Exception {
        try {
            if (!mappings.ping().equals("PONG")) {
                throw new Exception("Couldn't connect to redis");
            };
        } catch (Exception ex) {
            return Result.unhealthy(ex.getMessage());
        }
        return Result.healthy("Keys: " + String.valueOf(mappings.getKeys()));
    }

}
