package com.example.user.restaurantreviewapp.model;

public class GoogleReview {
    String author_name, author_url, profile_photo_url, relative_time_description, text;
    int rating;
    long time;

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getRelative_time_description() {
        return relative_time_description;
    }

    public void setRelative_time_description(String relative_time_description) {
        this.relative_time_description = relative_time_description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public GoogleReview(String author_name, String author_url, String profile_photo_url, String relative_time_description, String text, int rating, long time) {
        this.author_name = author_name;
        this.author_url = author_url;
        this.profile_photo_url = profile_photo_url;
        this.relative_time_description = relative_time_description;
        this.text = text;
        this.rating = rating;
        this.time = time;
    }

    @Override
    public String toString() {
        return "GoogleReview{" +
                "author_name='" + author_name + '\'' +
                ", author_url='" + author_url + '\'' +
                ", profile_photo_url='" + profile_photo_url + '\'' +
                ", relative_time_description='" + relative_time_description + '\'' +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", time=" + time +
                '}';
    }
}
