package com.example.restaurant_service.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaceSummary implements Serializable {

    private final String placeId;
    private final String name;
    private final String address;

    @JsonCreator
    public PlaceSummary(
        @JsonProperty("placeId") String placeId,
        @JsonProperty("name") String name,
        @JsonProperty("address") String address
    ) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
    }

    public String getPlaceId() { return placeId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
}
