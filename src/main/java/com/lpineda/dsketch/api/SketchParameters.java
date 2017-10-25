package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

/**
 * Created by leandro on 24/10/17.
 */
public class SketchParameters {
    @NotNull
    @JsonProperty
    private Integer rows;

    @NotNull
    @JsonProperty
    private Integer cols;

    @NotNull
    @JsonProperty
    private Integer prime;

    @NotNull
    @JsonProperty
    private Integer heavy_hitter_threshold;

    @NotNull
    @JsonProperty
    private Integer heavy_changer_threshold;

    @NotNull
    @JsonProperty
    private Long sketch_clean_up_interval;

    public void setHeavyHitterThreshold(Integer heavy_hitter_threshold) { this.heavy_hitter_threshold = heavy_hitter_threshold; }
    public Integer getHeavy_hitter_threshold() { return this.heavy_hitter_threshold; }

    public void setHeavy_changer_threshold(Integer heavy_changer_threshold) { this.heavy_changer_threshold = heavy_changer_threshold; }
    public Integer getHeavy_changer_threshold() { return this.heavy_changer_threshold; }

    public void setRows(Integer rows) { this.rows = rows; }
    public Integer getRows() { return rows; }

    public void setCols(Integer cols) { this.cols = cols; }
    public Integer getCols() { return cols; }

    public void setPrime(Integer prime) { this.prime = prime; }
    public Integer getPrime() { return prime; }

    public Long getSketch_clean_up_interval() {
        return sketch_clean_up_interval;
    }

    public void setSketch_clean_up_interval(Long sketch_clean_up_interval) {
        this.sketch_clean_up_interval = sketch_clean_up_interval;
    }

}
