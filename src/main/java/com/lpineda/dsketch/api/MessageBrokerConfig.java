package com.lpineda.dsketch.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageBrokerConfig {
    @JsonProperty
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
