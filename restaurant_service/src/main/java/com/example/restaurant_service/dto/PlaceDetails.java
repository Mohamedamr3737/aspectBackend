package com.example.restaurant_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlaceDetails {
    private String placeId;
    private String name;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private List<String> openingHours;
    private List<String> types;
    private double rating;
    private int priceLevel;

    @JsonCreator
    public PlaceDetails(
        @JsonProperty("placeId") String placeId,
        @JsonProperty("name") String name,
        @JsonProperty("address") String address,
        @JsonProperty("phone") String phone,
        @JsonProperty("latitude") double latitude,
        @JsonProperty("longitude") double longitude,
        @JsonProperty("imageUrl") String imageUrl,
        @JsonProperty("openingHours") List<String> openingHours,
        @JsonProperty("types") List<String> types,
        @JsonProperty("rating") double rating,
        @JsonProperty("priceLevel") int priceLevel
    ) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.openingHours = openingHours;
        this.types = types;
        this.rating = rating;
        this.priceLevel = priceLevel;
    }

    // Getters
    public String getPlaceId() { return placeId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getOpeningHours() { return openingHours; }
    public List<String> getTypes() { return types; }
    public double getRating() { return rating; }
    public int getPriceLevel() { return priceLevel; }
}
