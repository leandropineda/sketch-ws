package com.lpineda.dsketch.health;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.api.SketchParameters;
import com.lpineda.dsketch.db.SketchFactory;

/**
 * Created by leandro on 10/10/17.
 */

public class SketchParametersHealthCheck extends HealthCheck {

    private final SketchParameters sketchParameters;

    public SketchParametersHealthCheck(SketchParameters sketchParameters) {
        this.sketchParameters = sketchParameters;
    }

    @Override
    protected Result check() throws Exception
    {
        if(sketchParameters.getCols() != 0){
            return Result.healthy("Cols: " + sketchParameters.getCols()
                    + ". Rows: " + sketchParameters.getRows()
                    + ". Prime: " + sketchParameters.getPrime()
                    + ". HH Threshold: " + sketchParameters.getHeavyHitterThreshold()
                    + ". HC Threshold: " + sketchParameters.getHeavyChangerThreshold()
                    + ". Clean Up: " + sketchParameters.getSketchCleanUpInterval()
                    + ".");
        }
        return Result.unhealthy("Error message");
    }
}
