package com.example.user.restaurantreviewapp.placesApi;

import androidx.annotation.NonNull;


public class Place {

    public String address,phone_no,distance,name,photourl, placeid;
    public float rating;


    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public Place(String address, String phone_no, float rating, String distance, String name, String photurl, String placeid)
    {
        this.address = address;
        this.phone_no = phone_no;
        this.rating = rating;
        this.distance = distance;
        this.name = name;
        this.photourl = photurl;
        this.placeid = placeid;
    }

}
