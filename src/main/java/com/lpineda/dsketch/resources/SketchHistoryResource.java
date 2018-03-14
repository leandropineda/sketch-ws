package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.core.Sketch;
import com.lpineda.dsketch.core.SketchHistoryQueue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

@Path("/sketchhistory")
@Produces(MediaType.APPLICATION_JSON)

public class SketchHistoryResource {
    private final SketchHistoryQueue sketchHistoryQueue;
    private final Integer defaultCount = 30;

    public SketchHistoryResource(SketchHistoryQueue sketchHistoryQueue) {
        this.sketchHistoryQueue = sketchHistoryQueue;
    }

    @GET
    public Map<String, NavigableMap<Integer, Sketch>> getAll(@QueryParam("count") String count) {
        Map<String, NavigableMap<Integer, Sketch>> resource_map = new HashMap<>();
        if (count == null)
            resource_map.put("SketchHistoryQueue", sketchHistoryQueue.getSketchHistoryQueue(0));
        else
            resource_map.put("SketchHistoryQueue", sketchHistoryQueue.getSketchHistoryQueue(Integer.valueOf(count)));
        return resource_map;
    }

}
