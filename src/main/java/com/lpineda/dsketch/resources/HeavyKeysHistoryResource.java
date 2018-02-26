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
    public Map<String, NavigableMap<Integer, HeavyKeys>> getHeavyHitters(@QueryParam("count") final String count) {
        Map<String, NavigableMap<Integer, HeavyKeys>> resource_map = new HashMap<>();
        if (count == null)
            resource_map.put("HeavyHitters", this.heavyKeysHistoryQueue.getHeavyKeys(0));
        else
            resource_map.put("HeavyHitters", this.heavyKeysHistoryQueue.getHeavyKeys(Integer.valueOf(count)));
        return resource_map;
    }

}
