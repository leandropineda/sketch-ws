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
    public Map<String, NavigableMap<Integer, HeavyKeys>> getHeavyKeys(@QueryParam("count") final String count) {
        Map<String, NavigableMap<Integer, HeavyKeys>> resource_map = new HashMap<>();
        if (count == null)
            resource_map.put("HeavyKeys", this.heavyKeysHistoryQueue.getHeavyKeys());
        else
            resource_map.put("HeavyKeys", this.heavyKeysHistoryQueue.getHeavyKeys(Integer.valueOf(count)));
        return resource_map;
    }

    @GET
    @Path("/heavyhitters")
    public List<String> getHeavyHitters(@QueryParam("epoch") final String epoch) {
        Map<String, List<String>> resource_map = new HashMap<>();
        if (epoch == null) {
            return new ArrayList<>();
        }
        HeavyKeys heavyKeys = this.heavyKeysHistoryQueue.getHeavyKeys().get(Integer.valueOf(epoch));
        return heavyKeys.getHeavyHitters();
    }

    @GET
    @Path("/heavychangers")
    public List<String> getHeavyChangers(@QueryParam("epoch") final String epoch) {
        if (epoch == null) {
            return new ArrayList<>();
        }
        HeavyKeys heavyKeys = this.heavyKeysHistoryQueue.getHeavyKeys().get(Integer.valueOf(epoch));
        return heavyKeys.getHeavyChangers();
    }



}
