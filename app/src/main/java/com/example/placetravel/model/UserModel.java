package com.example.placetravel.model;

public class UserModel {

    private String email;
    private String username;
    private String imageUrl;
    private String userId;
    private String timestamp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
