package com.lpineda.dsketch.health;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.db.RedisManager;

/**
 * Created by leandro on 11/10/17.
 */
public class StringMappingHealthCheck extends HealthCheck {

    private final RedisManager redisManager;

    public StringMappingHealthCheck(RedisManager redisManager) {
        this.redisManager = redisManager;

    }
    @Override
    protected Result check() throws Exception {
        try {
            if (!redisManager.ping().equals("PONG")) {
                throw new Exception("Couldn't connect to redis");
            }
        } catch (Exception ex) {
            return Result.unhealthy(ex.getMessage());
        }
        return Result.healthy("Keys: " + String.valueOf(redisManager.getKeySetCardinality()));
    }

}
