package com.lpineda.dsketch.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class RedisManager {

    private final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);

    private final JedisPool jedisPool;
    private final LoadingCache<String, Long> cache;

    public RedisManager(String db_address, Integer cache_size) {
        LOGGER.info(MessageFormat.format("Initializing {0}", RedisManager.class.getName()));
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        LOGGER.info(String.format("Initializing Jedis pool: %s", db_address));
        this.jedisPool = new JedisPool(poolConfig, db_address);

        if (this.ping().equals("PONG")) {
            LOGGER.info("Connection to the database was successful");
        }

        LOGGER.info(String.format("Initializing LoadingCache with size: %d", cache_size));
        cache = CacheBuilder.newBuilder()
                .maximumSize(cache_size)
                .build(new CacheLoader<String, Long>() {
                    public Long load(String key) throws Exception {
                        return getOrSet(key);
                    }
                });
    }

    private Jedis getJedisConnection() {
        LOGGER.debug("Getting Jedis Connection");
        return jedisPool.getResource();
    }

    public String ping() {
        try (Jedis jedis = getJedisConnection()) {
            return jedis.ping();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw ex;
        }
    }

    private Long getOrSet(String event) {
        LOGGER.debug("Retrieving key from redis " + event);
        try (Jedis jedis = getJedisConnection()) {
            try {
                return Long.parseLong(jedis.hget("mappings", event));
            } catch (Exception ex) { // the key is not stored on the data
                Long newValue = jedis.hlen("mappings");
                // map the new key with a new id
                jedis.hset("mappings", event, String.valueOf(newValue));
                // reverse map the id to the event
                jedis.hset("reverse_mappings", String.valueOf(newValue), event);
                return newValue;
            }
        } catch (Exception ex) {
            LOGGER.error("Error with key " + event);
            LOGGER.error(ex.getMessage());
            throw ex;
        }

    }

    public Integer getValue(String event)  {
        Integer value = -1;
        try {
            value = Math.toIntExact(cache.get(event));
        } catch (ExecutionException ex) {
            LOGGER.error(ex.getMessage());
            return -1;
        }
        return value;
    }

    public Integer getKeySetCardinality() {
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.scard("keys"));
        }
    }

    public Set<String> getStringMappings(Set<Integer> heavy_hitters) {

        if (heavy_hitters.size() == 0) {
            return new HashSet<>();
        }

        // generate a set of string_id with the event int_id
        Set<String> heavy_hitters_string = new HashSet<>(heavy_hitters.size());
        heavy_hitters.forEach(i -> heavy_hitters_string.add(i.toString()));

        try (Jedis jedis = getJedisConnection()) {
            // get all events associated with their id
            List<String> reverse_mappings = jedis.hmget("reverse_mappings",
                    heavy_hitters_string.toArray(new String[heavy_hitters_string.size()]));

            return new HashSet<>(reverse_mappings);
        }
    }
}
