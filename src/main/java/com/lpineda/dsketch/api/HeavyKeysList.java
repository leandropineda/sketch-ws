package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class HeavyKeysList {
    @JsonProperty("keys")
    private final List<String> heavyKeys;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy:MM:dd HH:mm:ss")
    private final Date date;

    public HeavyKeysList(Set<String> heavyKeys, Date date) {
        this.heavyKeys = new ArrayList<>(heavyKeys);
        this.date = date;
    }

    public Integer count() {
        return this.heavyKeys.size();
    }

    public Date getDate() {
        return this.date;
    }

}
