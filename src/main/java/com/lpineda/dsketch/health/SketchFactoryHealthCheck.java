/*
package com.lpineda.dsketch.health;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.db.SketchFactory;

*/
/**
 * Created by leandro on 10/10/17.
 *//*

public class SketchFactoryHealthCheck extends HealthCheck {

    private final SketchFactory sketchFactory;

    public SketchFactoryHealthCheck(SketchFactory sketchFactory) {
        this.sketchFactory = sketchFactory;
    }
    @Override
    protected Result check() throws Exception
    {
        if(sketchFactory.getCols() != 0){
            return Result.healthy("Cols: " + sketchFactory.getCols()
                    + ". Rows: " + sketchFactory.getRows()
                    + ". Prime: " + sketchFactory.getPrime() + ".");
        }
        return Result.unhealthy("Error message");
    }
}
*/
