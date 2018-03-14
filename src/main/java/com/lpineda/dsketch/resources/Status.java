package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.api.DetectionParameters;
import com.lpineda.dsketch.api.SketchConfig;
import com.lpineda.dsketch.core.HeavyKeys;
import com.lpineda.dsketch.core.HeavyKeysHistoryQueue;
import com.lpineda.dsketch.jobs.HeavyKeyDetector;
import com.lpineda.dsketch.jobs.SketchManager;

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
    private final SketchManager sketchManager;
    private final HeavyKeyDetector heavyKeyDetector;

    public Status(final SketchConfig sketchConfig,
                  final DetectionParameters detectionParameters,
                  final SketchManager sketchManager,
                  final HeavyKeyDetector heavyKeyDetector) {

        this.sketchConfig = sketchConfig;
        this.detectionParameters = detectionParameters;
        this.sketchManager = sketchManager;
        this.heavyKeyDetector = heavyKeyDetector;
    }

    @GET
    public Map<String,Object> getStatus(){
        Map<String,Object> status = new HashMap<>();
        status.put("SketchConfig", Response.ok(sketchConfig).build().getEntity());
        status.put("DetectionParameters", Response.ok(detectionParameters).build().getEntity());
        status.put("SketchManager", Response.ok(sketchManager).build().getEntity());
        status.put("HeavyKeyDetector", Response.ok(heavyKeyDetector).build().getEntity());
        return status;
    }

}
