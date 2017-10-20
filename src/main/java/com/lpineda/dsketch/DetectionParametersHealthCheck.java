package com.lpineda.dsketch;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.db.SketchFactory;

/**
 * Created by leandro on 10/10/17.
 */
public class DetectionParametersHealthCheck extends HealthCheck {

    private final DetectionParameters detectionParameters;

    public DetectionParametersHealthCheck(DetectionParameters detectionParameters) {
        this.detectionParameters = detectionParameters;
    }
    @Override
    protected Result check() throws Exception
    {
        return Result.healthy("Heavy hitter threshold: " + detectionParameters.getHeavy_hitter_threshold()
                + ". Heavy changer threshold: " + detectionParameters.getHeavy_changer_threshold()
                + ". Clean up interval: " + detectionParameters.getSketch_clean_up_interval() + ".");
    }
}
