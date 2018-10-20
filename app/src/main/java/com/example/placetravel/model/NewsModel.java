package com.example.placetravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsModel implements Parcelable {
    private String id;
    private String title;
    private String des;
    private String createTimestamp;
    private String updateTimestamp;

    public NewsModel() {

    }

    protected NewsModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        des = in.readString();
        createTimestamp = in.readString();
        updateTimestamp = in.readString();
    }

    public static final Creator<NewsModel> CREATOR = new Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel in) {
            return new NewsModel(in);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
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

    @Override
    public String toString() {
        return "NewsModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", des='" + des + '\'' +
                ", createTimestamp='" + createTimestamp + '\'' +
                ", updateTimestamp='" + updateTimestamp + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(des);
        dest.writeString(createTimestamp);
        dest.writeString(updateTimestamp);
    }
}
