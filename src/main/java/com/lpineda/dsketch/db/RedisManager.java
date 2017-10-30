package com.lpineda.dsketch.db;

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
    private final LoadingCache<String, Long> db_cache;

    public RedisManager(String db_address) {
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

        db_cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new CacheLoader<String, Long>() {
                    public Long load(String key) throws Exception {
                        return addEvent(key);
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

    private Long addEvent(String key) {
        LOGGER.debug("Retrieving key from redis " + key);
        try (Jedis jedis = getJedisConnection()) {
            // if the key is not already stored on the db
            if (!jedis.sismember("keys", key)) {
                // get the amount of keys stored on the db
                Long n_keys = jedis.scard("keys");
                // add the key to the key set 'keys'
                jedis.sadd("keys", key);
                // map the new key with a new id
                jedis.hset("mappings", key, String.valueOf(n_keys));
                // reverse map the id to the event
                jedis.hset("reverse_mappings", String.valueOf(n_keys), key);
                return n_keys;
            } else {
                // if the key exists, return its mapping
                return Long.valueOf(jedis.hget("mappings", key));
            }
        } catch (Exception ex) {
            LOGGER.error("Error with key " + key);
            LOGGER.error(ex.getMessage());
            throw ex;
        }

    }

    public Integer getValue(String key)  {
        Integer value = -1;
        try {
            value = Math.toIntExact(db_cache.get(key));
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
