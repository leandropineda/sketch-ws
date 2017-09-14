package com.lpineda.dsketch.db;

import com.google.common.cache.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.core.Sketch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Created by leandro on 02/09/17.
 */

@ParametersAreNonnullByDefault
public class SketchCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchCache.class);

    private final RedisMappings mappings;

    public SketchCache(RedisMappings mappings) {
        this.mappings = mappings;
    }

    private LoadingCache<Integer, Sketch> sketchCache =
            CacheBuilder.newBuilder()
                    .maximumSize(1)
                    .removalListener(new RemovalListener<Integer, Sketch>() {
                        public void onRemoval(RemovalNotification notification) {
                            sketchCache.refresh(0);
                        }
                    })
                    .build(new CacheLoader<Integer, Sketch>() {
                        public Sketch load(Integer key) throws Exception {
                            return buildSketch();
                        }
                    });

    private Sketch buildSketch() {
        Integer rows = 5;
        Integer cols = 40;
        Integer prime = 9337;

        Map<Integer, Integer> hash_functions = Sketch.buildHashFunctions(rows,10000);
        return new Sketch(rows, cols, prime, hash_functions);
    }

    public Sketch getSketch() throws ExecutionException {
        return sketchCache.get(0);
    }

    public void invalidateSketch() {
        sketchCache.invalidate(0);
    }

    public Mapping addElement(String key) throws ExecutionException {
        String val = mappings.get(key);
        sketchCache.get(0).addElement(Integer.valueOf(val));
        return new Mapping(key, val);
    }
}
