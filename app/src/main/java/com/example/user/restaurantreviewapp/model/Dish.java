package com.example.user.restaurantreviewapp.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Dish {
//    @SerializedName("rating")
//    private float rating;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("price")
    private float price;
    @SerializedName("photo_url")
    private ArrayList<String> photo_url;
    @SerializedName("placeID")
    private String placeID;

    private String authorID;

    private String currency;

    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }

    @SerializedName("dishID")
    private String dishID;

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

//    public float getRating() {
//        return rating;
//    }
//
//    public void setRating(float rating) {
//        this.rating = rating;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    public Dish(String title, String description, float price, ArrayList<String> photo_url, String placeID, String dishID, String authorID, String currency) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.photo_url = photo_url;
        this.placeID = placeID;
        this.dishID = dishID;
        this.authorID = authorID;
        this.currency = currency;
    }


    public ArrayList<String> getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(ArrayList<String> photo_url) {
        this.photo_url = photo_url;
    }

//    public void clacRating()
//    {
//        float sum = 0;
//        for (Review review:reviews){
//            sum+=review.getRating();
//        }
//        sum = sum/reviews.size();
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", photo_url=" + photo_url +
                ", placeID='" + placeID + '\'' +
                ", authorID='" + authorID + '\'' +
                ", dishID='" + dishID + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return FuzzySearch.ratio(this.getTitle(), ((Dish)obj).getTitle()) >= 85;
    }
}
