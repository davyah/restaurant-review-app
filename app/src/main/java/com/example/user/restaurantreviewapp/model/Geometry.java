package com.example.user.restaurantreviewapp.model;

import com.google.gson.annotations.SerializedName;

public class Geometry {

    public Location getLocation() {
        return location;
    }

    @SerializedName("location")
    private Location location;

    Geometry(Location location)
    {
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
