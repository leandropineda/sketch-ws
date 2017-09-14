package com.lpineda.dsketch.db;

import com.google.common.cache.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by leandro on 03/09/17.
 */

@ParametersAreNonnullByDefault
public class RedisMappings {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMappings.class);

    private final Jedis jedis;

    public RedisMappings(Jedis jedis) {
        this.jedis = jedis;
    }

    private final LoadingCache<String, String> eventCache =
            CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .build(new CacheLoader<String, String>() {
                        public String load(String key) throws Exception {
                            LOGGER.debug("Retrieving key from redis " + key);
                            Boolean is_member = jedis.sismember("keys", key);
                            if (!is_member) {
                                String n_keys = String.valueOf(jedis.scard("keys"));
                                jedis.sadd("keys", key);
                                jedis.hset("mappings", key, n_keys);
                                jedis.hset("reverse_mappings", n_keys, key);
                                return n_keys;
                            }
                            return String.valueOf(jedis.hget("mappings", key));
                        }
                    });

    public String get(String key) throws ExecutionException {
        return eventCache.get(key);

    }

    public Set<String> getMappings(Set<Integer> heavy_hitters) {

        if (heavy_hitters.size() == 0) {
            return new HashSet<>();
        }

        Set<String> heavy_hitters_string = new HashSet<>(heavy_hitters.size());
        heavy_hitters.forEach(i -> heavy_hitters_string.add(i.toString()));

        List<String> reverse_mappings = jedis.hmget("reverse_mappings",
                heavy_hitters_string.toArray(new String[heavy_hitters_string.size()]));

        return new HashSet<>(reverse_mappings);
    }
}
