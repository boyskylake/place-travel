package com.example.placetravel.model;

public class RatingModel {

    private String userId;
    private String placeId;
    private int rate;
    private double avgRating;
    private int totalFiveRate;
    private int totalFourRate;
    private int totalThreeRate;
    private int totalTwoRate;
    private int totalOneRate;
    private boolean rated;
    private int totalUserRating;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getTotalFiveRate() {
        return totalFiveRate;
    }

    public void setTotalFiveRate(int totalFiveRate) {
        this.totalFiveRate = totalFiveRate;
    }

    public int getTotalFourRate() {
        return totalFourRate;
    }

    public void setTotalFourRate(int totalFourRate) {
        this.totalFourRate = totalFourRate;
    }

    public int getTotalThreeRate() {
        return totalThreeRate;
    }

    public void setTotalThreeRate(int totalThreeRate) {
        this.totalThreeRate = totalThreeRate;
    }

    public int getTotalTwoRate() {
        return totalTwoRate;
    }

    public void setTotalTwoRate(int totalTwoRate) {
        this.totalTwoRate = totalTwoRate;
    }

    public int getTotalOneRate() {
        return totalOneRate;
    }

    public void setTotalOneRate(int totalOneRate) {
        this.totalOneRate = totalOneRate;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public int getTotalUserRating() {
        return totalUserRating;
    }

    public void setTotalUserRating(int totalUserRating) {
        this.totalUserRating = totalUserRating;
    }

    @Override
    public String toString() {
        return "RatingModel{" +
                "userId='" + userId + '\'' +
                ", placeId='" + placeId + '\'' +
                ", rate=" + rate +
                ", avgRating=" + avgRating +
                ", totalFiveRate=" + totalFiveRate +
                ", totalFourRate=" + totalFourRate +
                ", totalThreeRate=" + totalThreeRate +
                ", totalTwoRate=" + totalTwoRate +
                ", totalOneRate=" + totalOneRate +
                ", rated=" + rated +
                ", totalUserRating=" + totalUserRating +
                '}';
    }
}
