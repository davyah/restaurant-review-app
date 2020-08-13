package com.example.user.restaurantreviewapp.model;
/**java file for user & all its details*/
public class User {
    String username;
    String email;
    String name;
    String image_url;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString()
    {
        return "name: " + name + " username: " + username + " email: " + email + " imageurl: " + image_url;
    }
}
