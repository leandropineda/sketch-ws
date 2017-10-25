package com.lpineda.dsketch.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by leandro on 03/09/17.
 */

@ParametersAreNonnullByDefault
public class EventMapping {

    private final Logger LOGGER = LoggerFactory.getLogger(EventMapping.class);

    @JsonProperty
    private String db_address;

    private final JedisPoolConfig poolConfig = new JedisPoolConfig();
    private JedisPool jedisPool;


    public void setDbAddress(String db_address) { this.db_address = db_address; }
    public String getDbAddress() {
        return db_address;
    }

    private final LoadingCache<String, String> jedis_cache =
            CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .build(new CacheLoader<String, String>() {
                        public String load(String key) throws Exception {
                            return addEvent(key);
                        }
                    });

    public void connect() {
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
        LOGGER.info(String.format("Initializing Jedis pool: %s", getDbAddress()));
        this.jedisPool = new JedisPool(poolConfig, getDbAddress());

    }

    private Jedis getJedisConnection() {
        LOGGER.debug("Getting Jedis Connection");
        return jedisPool.getResource();
    }

    private void closeJedisConnection(Jedis jedis) {
        LOGGER.debug("Closing Jedis Connection");
        jedis.close();
    }

    public String ping() {
        Jedis jedis = getJedisConnection();
        try {
            return jedis.ping();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw ex;
        } finally {
            closeJedisConnection(jedis);
        }
    }

    String get(String key) {
        try {
            return jedis_cache.get(key);
        } catch (ExecutionException ex) {
            LOGGER.error(ex.getMessage());
        }
        return "";
    }

    private String addEvent(String key) {
        LOGGER.debug("Retrieving key from redis " + key);
        Boolean is_member = Boolean.FALSE;

        Jedis jedis = getJedisConnection();
        try {
            // if the key is not already stored on the db
            if (!jedis.sismember("keys", key)) {
                // get the amount of keys stored on the db
                String n_keys = String.valueOf(jedis.scard("keys"));
                // add the key to the key set 'keys'
                jedis.sadd("keys", key);
                // map the new key with a new id
                jedis.hset("mappings", key, n_keys);
                // reverse map the id to the event
                jedis.hset("reverse_mappings", n_keys, key);
                closeJedisConnection(jedis);
                return n_keys;
            } else {
                // if the key exists, return its mapping
                String ret = String.valueOf(jedis.hget("mappings", key));
                closeJedisConnection(jedis);
                return ret;
            }
        } catch (Exception ex) {
            LOGGER.error("Error with key " + key);
            LOGGER.error(ex.getMessage());
            throw ex;
        }

    }

    public Set<String> getMappings(Set<Integer> heavy_hitters) {

        if (heavy_hitters.size() == 0) {
            return new HashSet<>();
        }

        // generate a set of string_id with the event int_id
        Set<String> heavy_hitters_string = new HashSet<>(heavy_hitters.size());
        heavy_hitters.forEach(i -> heavy_hitters_string.add(i.toString()));

        Jedis jedis = getJedisConnection();
        // get all events associated with their id
        List<String> reverse_mappings = jedis.hmget("reverse_mappings",
                heavy_hitters_string.toArray(new String[heavy_hitters_string.size()]));

        closeJedisConnection(jedis);
        return new HashSet<>(reverse_mappings);
    }

    public Long getKeys() {
        Jedis jedis = getJedisConnection();
        Long ret = jedis.scard("keys");
        closeJedisConnection(jedis);
        return ret;
    }
}
