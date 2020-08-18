package com.example.user.restaurantreviewapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Review {
    @SerializedName("description")
    private String description;

    @SerializedName("rating")
    private float rating;

    @SerializedName("photosUrl")
    private ArrayList<String> photosUrl;

    private String reviewID;

    private String authorID;

    private String dishID;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private String timeStamp;

    public Review(String description, float rating, ArrayList<String> photosUrl, String reviewID, String authorID, String dishID, String timeStamp) {
        this.description = description;
        this.rating = rating;
        this.photosUrl = photosUrl;
        this.reviewID = reviewID;
        this.authorID = authorID;
        this.dishID = dishID;
        this.timeStamp = timeStamp;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public ArrayList<String> getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(ArrayList<String> photosUrl) {
        this.photosUrl = photosUrl;
    }

    @Override
    public String toString() {
        return "Review{" +
                "description='" + description + '\'' +
                ", rating=" + rating +
                ", photosUrl=" + photosUrl +
                ", reviewID='" + reviewID + '\'' +
                ", authorID='" + authorID + '\'' +
                ", dishID='" + dishID + '\'' +
                '}';
    }
}
