package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.api.HeavyKeysList;
import com.lpineda.dsketch.core.HeavyKeyDetectionHistory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/heavykeys")
@Produces(MediaType.APPLICATION_JSON)

public class HeavyKeysResource {
    private final HeavyKeyDetectionHistory heavyKeyDetectionHistory;
    private final Integer defaultCount = 10;

    public HeavyKeysResource(HeavyKeyDetectionHistory heavyKeyDetectionHistory) {
        this.heavyKeyDetectionHistory = heavyKeyDetectionHistory;
    }

    @GET
    public Map<String,List<HeavyKeysList>> getAll(@QueryParam("count") String count) {
        Map<String,List<HeavyKeysList>> resource_map = new HashMap<>();
        if (count == null) {
            resource_map.put("HeavyHitters", heavyKeyDetectionHistory.getHeavyHitters(defaultCount));
            resource_map.put("HeavyChangers", heavyKeyDetectionHistory.getHeavyChangers(defaultCount));
            return resource_map;
        }
        resource_map.put("HeavyHitters", heavyKeyDetectionHistory.getHeavyHitters(Integer.valueOf(count)));
        resource_map.put("HeavyChangers", heavyKeyDetectionHistory.getHeavyChangers(Integer.valueOf(count)));
        return resource_map;
    }

    @GET
    @Path("/heavyhitters")
    public Map<String,List<HeavyKeysList>> getHeavyHitters(@QueryParam("count") final String count) {
        Map<String,List<HeavyKeysList>> resource_map = new HashMap<>();
        if (count == null) {
            resource_map.put("HeavyHitters", heavyKeyDetectionHistory.getHeavyHitters(defaultCount));
            return resource_map;
        }
        resource_map.put("HeavyHitters", heavyKeyDetectionHistory.getHeavyHitters(Integer.valueOf(count)));
        return resource_map;
    }

    @GET
    @Path("/heavychangers")
    public Map<String,List<HeavyKeysList>> getHeavyChangers(@QueryParam("count") final String count) {
        Map<String,List<HeavyKeysList>> resource_map = new HashMap<>();
        if (count == null) {
            resource_map.put("HeavyChangers", heavyKeyDetectionHistory.getHeavyChangers(defaultCount));
            return resource_map;
        }
        resource_map.put("HeavyChangers", heavyKeyDetectionHistory.getHeavyChangers(Integer.valueOf(count)));
        return resource_map;
    }
}
