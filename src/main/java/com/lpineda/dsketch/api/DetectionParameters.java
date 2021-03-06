package com.lpineda.dsketch.api;

/**
 * Created by leandro on 24/10/17.
 */
public class DetectionParameters {

    private Integer heavyHitterThreshold;
    private Integer heavyChangerThreshold;
    private Long sketchRotationInterval;
    private Long maxHistoryQueueLength;

    public void setHeavyHitterThreshold(Integer heavyHitterThreshold) { this.heavyHitterThreshold = heavyHitterThreshold; }
    public Integer getHeavyHitterThreshold() { return this.heavyHitterThreshold; }

    public void setHeavyChangerThreshold(Integer heavyChangerThreshold) { this.heavyChangerThreshold = heavyChangerThreshold; }
    public Integer getHeavyChangerThreshold() { return this.heavyChangerThreshold; }

    public void setSketchRotationInterval(Long sketchRotationInterval) { this.sketchRotationInterval = sketchRotationInterval; }
    public Long getSketchRotationInterval() { return sketchRotationInterval; }

    public void setMaxHistoryQueueLength(Long maxHistoryQueueLength) {
        this.maxHistoryQueueLength = maxHistoryQueueLength;
    }

    public Long getMaxHistoryQueueLength() {
        return maxHistoryQueueLength;
    }

}
