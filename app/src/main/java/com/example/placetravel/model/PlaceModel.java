package com.example.placetravel.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PlaceModel implements Parcelable {

    private String id;
    private String name;
    private String description;
    private String phoneNumber;
    private String linkFacebook;
    private double lat;
    private double lng;
    private List<String> galleries;
    private String createTimestamp;
    private String updateTimestamp;
    private String userId;
    private String category;

    public PlaceModel() {

    }

    protected PlaceModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        category = in.readString();
        description = in.readString();
        phoneNumber = in.readString();
        linkFacebook = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        galleries = in.createStringArrayList();
        createTimestamp = in.readString();
        updateTimestamp = in.readString();
        userId = in.readString();
    }

    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLinkFacebook() {
        return linkFacebook;
    }

    public void setLinkFacebook(String linkFacebook) {
        this.linkFacebook = linkFacebook;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<String> getGalleries() {
        return galleries;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public String getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(String createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(String updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PlaceModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", linkFacebook='" + linkFacebook + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", galleries=" + galleries +
                ", createTimestamp='" + createTimestamp + '\'' +
                ", updateTimestamp='" + updateTimestamp + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(phoneNumber);
        dest.writeString(linkFacebook);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeStringList(galleries);
        dest.writeString(createTimestamp);
        dest.writeString(updateTimestamp);
        dest.writeString(userId);
    }
}
