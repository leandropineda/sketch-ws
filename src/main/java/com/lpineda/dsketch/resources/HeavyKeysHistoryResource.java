package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.core.HeavyKeys;
import com.lpineda.dsketch.core.HeavyKeysHistoryQueue;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/heavykeys")
@Produces(MediaType.APPLICATION_JSON)

public class HeavyKeysHistoryResource {
    private final HeavyKeysHistoryQueue heavyKeysHistoryQueue;

    public HeavyKeysHistoryResource(HeavyKeysHistoryQueue heavyKeysHistoryQueue) {
        this.heavyKeysHistoryQueue = heavyKeysHistoryQueue;
    }

    @GET
    @Path("/heavyhitters")
    public Map<String, NavigableMap<Integer, HeavyKeys>> getHeavyHitters(@QueryParam("count") final String count) {
        Map<String, NavigableMap<Integer, HeavyKeys>> resource_map = new HashMap<>();
        if (count == null)
            resource_map.put("HeavyHitters", this.heavyKeysHistoryQueue.getHeavyHitters(0));
        else
            resource_map.put("HeavyHitters", this.heavyKeysHistoryQueue.getHeavyHitters(Integer.valueOf(count)));
        return resource_map;
    }

    @GET
    @Path("/heavychangers")
    public  Map<String, NavigableMap<Integer, HeavyKeys>> getHeavyChangers(@QueryParam("count") final String count) {
        Map<String, NavigableMap<Integer, HeavyKeys>> resource_map = new HashMap<>();
        if (count == null)
            resource_map.put("HeavyChangers", this.heavyKeysHistoryQueue.getHeavyChangers(0));
        else
            resource_map.put("HeavyChangers", this.heavyKeysHistoryQueue.getHeavyChangers(Integer.valueOf(count)));
        return resource_map;
    }
}
