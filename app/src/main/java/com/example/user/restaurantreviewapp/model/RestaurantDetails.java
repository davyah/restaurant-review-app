package com.example.user.restaurantreviewapp.model;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//TODO check why if there is menu, the RCV is not rolling independently
public class RestaurantDetails {
    @SerializedName("reviews")
    ArrayList<GoogleReview> googleReviews;

    @SerializedName("formatted_phone_number")
    private String formatted_phone_number;

    @SerializedName("formatted_address")
    private String formatted_address;

    @SerializedName("name")
    private String name;

    @SerializedName("place_id")
    private String place_id;

    @SerializedName("rating")
    private float rating;

    @SerializedName("user_ratings_total")
    private int user_ratings_total;

    @SerializedName("opening_hours")
    private Opening_hours opening_hours;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("photos")
    private ArrayList<Photo> photos = new ArrayList<Photo>();



    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public String getFormatted_address() {
        return formatted_address;
    }


    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public void setOpening_hours(Opening_hours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public String getName() {
        return name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public float getRating() {
        return rating;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public Opening_hours getOpening_hours() {
        return opening_hours;
    }

    public Geometry getGeometry() {
        return geometry;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDayHours(String day)
    {
        if(opening_hours != null)
            return opening_hours.getDayHours(day);
        else
            return "NA";
    }

    public String[] PhotosUrl()
    {
        String[] photosUrl = new String[photos.size()];
        for(int i = 0; i<photos.size(); i++)
        {
            Log.i("photo", photos.get(i).getPhotourl());
            photosUrl[i] = photos.get(i).getPhotourl();
        }
        return photosUrl;
    }

    public ArrayList<GoogleReview> getGoogleReviews() {
        return googleReviews;
    }

    public void setGoogleReviews(ArrayList<GoogleReview> googleReviews) {
        this.googleReviews = googleReviews;
    }

    RestaurantDetails(String place_id, String formatted_phone_number, String name, String formatted_address, float rating, int user_ratings_total, Opening_hours opening_hours, ArrayList<Photo> photos, Geometry geometry, ArrayList<GoogleReview> googleReviews)
    {
        this.place_id = place_id;
        this.name = name;
        this.formatted_address = formatted_address;
        this.rating = rating;
        this.user_ratings_total = user_ratings_total;
        this.formatted_phone_number = formatted_phone_number;
        this.opening_hours = opening_hours;
        this.photos = photos;
        this.geometry = geometry;
        this.googleReviews = googleReviews;
    }
}
