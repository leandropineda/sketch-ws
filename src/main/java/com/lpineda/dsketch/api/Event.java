package com.lpineda.dsketch.api;

/**
 * Created by leandro on 02/09/17.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class Event {
    private Date date;
    private String event;

    public Event() {

    }

    public Event(Date date, String event) {
        this.date = date;
        this.event = event;
    }

    @JsonProperty
    public Date getDate() {
        return date;
    }

    @JsonProperty
    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("date", date)
                .add("event", event)
                .toString();
    }
}
