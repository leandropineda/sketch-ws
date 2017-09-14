package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.api.Event;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.db.SketchCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

/**
 * Created by leandro on 02/09/17.
 */

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);

    private final SketchCache sketchCache;

    public EventResource(SketchCache sketchCache) {
        this.sketchCache = sketchCache;
    }

    @POST
    public Mapping addEvent(@Valid Event event) throws ExecutionException {
        return sketchCache.addElement(event.getEvent());
    }
}

