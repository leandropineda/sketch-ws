package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by leandro on 24/10/17.
 */
public class SketchParameters {
    private Integer rows;
    private Integer cols;
    private Integer prime;
    private Integer heavyHitterThreshold;
    private Integer heavyChangerThreshold;
    private Long sketchCleanUpInterval;

    public void setHeavyHitterThreshold(Integer heavyHitterThreshold) { this.heavyHitterThreshold = heavyHitterThreshold; }
    public Integer getHeavyHitterThreshold() { return this.heavyHitterThreshold; }

    public void setHeavyChangerThreshold(Integer heavyChangerThreshold) { this.heavyChangerThreshold = heavyChangerThreshold; }
    public Integer getHeavyChangerThreshold() { return this.heavyChangerThreshold; }

    public void setRows(Integer rows) { this.rows = rows; }
    public Integer getRows() { return rows; }

    public void setCols(Integer cols) { this.cols = cols; }
    public Integer getCols() { return cols; }

    public void setPrime(Integer prime) { this.prime = prime; }
    public Integer getPrime() { return prime; }

    public void setSketchCleanUpInterval(Long sketchCleanUpInterval) { this.sketchCleanUpInterval = sketchCleanUpInterval; }
    public Long getSketchCleanUpInterval() { return sketchCleanUpInterval; }

}
