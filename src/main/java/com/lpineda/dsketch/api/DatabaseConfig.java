package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfig {
    @JsonProperty
    private String address;

    @JsonProperty
    private Integer cache_size;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCacheSize() {
        return cache_size;
    }

    public void setCacheSize(Integer cache_size) {
        this.cache_size = cache_size;
    }

}
