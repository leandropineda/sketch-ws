package com.lpineda.dsketch.api;

/**
 * Created by leandro on 02/09/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DetectionParameters {
    private Long sketch_clean_up_interval;
    private Integer heavy_hitter_threshold;
    private Integer heavy_changer_threshold;

    @JsonProperty
    public Long getSketch_clean_up_interval() {
        return this.sketch_clean_up_interval;
    }

    @JsonProperty
    public void setSketch_clean_up_interval(Long clean_up_time) {
        this.sketch_clean_up_interval = clean_up_time;
    }

    @JsonProperty
    public Integer getHeavy_hitter_threshold() {
        return this.heavy_hitter_threshold;
    }

    @JsonProperty
    public void setHeavy_hitter_threshold(Integer threshold_) {
        this.heavy_hitter_threshold = threshold_;
    }
    @JsonProperty
    public Integer getHeavy_changer_threshold() {
        return this.heavy_changer_threshold;
    }

    @JsonProperty
    public void setHeavy_changer_threshold(Integer threshold_) {
        this.heavy_changer_threshold = threshold_;
    }

}
