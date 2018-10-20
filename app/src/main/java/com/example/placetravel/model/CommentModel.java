package com.example.placetravel.model;

public class CommentModel {

    private String placeId;
    private String userId;
    private String message;
    private String imageUrl;
    private String createTimestamp;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(String createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "placeId='" + placeId + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createTimestamp='" + createTimestamp + '\'' +
                '}';
    }
}
