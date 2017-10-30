package com.lpineda.dsketch.resources;

import com.codahale.metrics.health.HealthCheck;
import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.core.SketchHistory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
public class Status {

    private final SketchConfig sketchConfig;
    private final DetectionParameters detectionParameters;
    private final SketchHistory sketchHistory;

    public Status(final SketchConfig sketchConfig,
                  final DetectionParameters detectionParameters,
                  final SketchHistory sketchHistory) {

        this.sketchConfig = sketchConfig;
        this.detectionParameters = detectionParameters;
        this.sketchHistory = sketchHistory;
    }
    @GET
    public Map<String,Object> getStatus(){
        Map<String,Object> status = new HashMap<>();
        status.put("SketchConfig", Response.ok(sketchConfig).build().getEntity());
        status.put("DetectionParameters", Response.ok(detectionParameters).build().getEntity());
        status.put("SketchHistory", Response.ok(sketchHistory).build().getEntity());
        return status;
    }

}
