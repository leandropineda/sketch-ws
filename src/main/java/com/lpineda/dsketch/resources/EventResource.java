package com.lpineda.dsketch.resources;

import com.lpineda.dsketch.api.Event;
import com.lpineda.dsketch.api.Mapping;
import com.lpineda.dsketch.db.SketchFactory;
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

    private final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);

    private final SketchFactory sketchFactory;

    public EventResource(SketchFactory sketchFactory) {
        this.sketchFactory = sketchFactory;
    }

    @POST
    public Mapping addEvent(@Valid Event event) {
        Mapping return_mapping;
        try {
            return_mapping = sketchFactory.addEvent(event.getEvent());
            LOGGER.debug("Event " + return_mapping.getEvent() + " is mapped with " + return_mapping.getMapping());
        } catch (ExecutionException ex) {
            LOGGER.error("Something went wrong when adding element " + event.getEvent());
            LOGGER.error(ex.getMessage());
        } finally {
            return_mapping = new Mapping(event.getEvent(), "null");
        }
        return return_mapping;
    }
}

