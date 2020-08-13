package com.example.user.restaurantreviewapp.model;

import com.example.user.restaurantreviewapp.placesApi.ApiClient;
import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("height")
    private int height;
    @SerializedName("width")
    private int width;
    @SerializedName("photo_reference")
    private String photo_reference;


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public String getPhotourl() {
//        return ApiClient.base_url + "place/photo?maxwidth=100&photoreference=" + this.photo_reference +
//                "&key=" + ApiClient.GOOGLE_PLACE_API_KEY;
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=" + this.photo_reference + "&key=" + ApiClient.GOOGLE_PLACE_API_KEY;
    }


    Photo(int height, int width, String photo_reference)
    {
        this.height = height;
        this.width = width;
        this.photo_reference = photo_reference;
    }
}
