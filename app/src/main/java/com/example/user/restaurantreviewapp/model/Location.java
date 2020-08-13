package com.example.user.restaurantreviewapp.model;

import com.google.gson.annotations.SerializedName;

public class Location {
    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    Location(double lat, double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
