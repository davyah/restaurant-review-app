package com.example.user.restaurantreviewapp.placesApi;

import androidx.annotation.NonNull;


public class Place {

    public String address,phone_no,distance,name,photourl, placeid;
    public float rating;


    @Override
    public String toString() {
        return "Place{" +
                "address='" + address + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", distance='" + distance + '\'' +
                ", name='" + name + '\'' +
                ", photourl='" + photourl + '\'' +
                ", placeid='" + placeid + '\'' +
                ", rating=" + rating +
                '}';
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
