package com.lpineda.dsketch.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.core.Sketch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.validation.constraints.NotNull;

/**
 * Created by leandro on 02/09/17.
 */

@ParametersAreNonnullByDefault
public class SketchFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SketchFactory.class);

    private EventMapping mappings_db;

    @NotNull
    @JsonProperty
    private Integer rows;

    @NotNull
    @JsonProperty
    private Integer cols;

    @NotNull
    @JsonProperty
    private Integer prime;

    private LoadingCache<Integer, Sketch> sketch_cache =
            CacheBuilder.newBuilder()
                    .maximumSize(1)
                    .removalListener(new RemovalListener<Integer, Sketch>() {
                        public void onRemoval(RemovalNotification notification) {
                            sketch_cache.refresh(0);
                        }
                    })
                    .build(new CacheLoader<Integer, Sketch>() {
                        public Sketch load(Integer key) throws Exception {
                            return build();
                        }
                    });

    public void setMappings(EventMapping mappings_db) {
        this.mappings_db = mappings_db;
    }

    public void setRows(Integer rows) { this.rows = rows; }
    public Integer getRows() { return rows; }

    public void setCols(Integer cols) { this.cols = cols; }
    public Integer getCols() { return cols; }

    public void setPrime(Integer prime) { this.prime = prime; }
    public Integer getPrime() { return prime; }

    private Sketch build() {
        //TODO: Read sketch configuration from an external place instead of using a config file
        Map<Integer, Integer> hash_functions = Sketch.buildHashFunctions(getRows(),10000);
        return new Sketch(getRows(), getCols(), getPrime(), hash_functions);
    }

    public Sketch getSketch() throws ExecutionException {
        return this.sketch_cache.get(0);

    }

    public void invalidateSketch() {
        this.sketch_cache.invalidate(0);
    }

    public Mapping addEvent(String event) throws ExecutionException {
        Integer mapping = Integer.valueOf(this.mappings_db.get(event));
        getSketch().addElement(mapping);
        return new Mapping(event, mapping);
    }
}
