package com.example.placetravel.model;

public class RateModel {

    private String placeId;
    private String userId;
    private int rate;

    public String getShopId() {
        return placeId;
    }

    public void setShopId(String shopId) {
        this.placeId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "RateModel{" +
                "placeId='" + placeId + '\'' +
                ", userId='" + userId + '\'' +
                ", rate=" + rate +
                '}';
    }
}
