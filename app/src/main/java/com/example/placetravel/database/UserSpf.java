package com.example.placetravel.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSpf {

    private SharedPreferences spf;
    private static String KEY_IS_LOGGED_ID = "is_logged_id";
    private static String KEY_IMAGE_URL = "image_url";
    private static String KEY_IS_ADMIN = "is_admin";

    public UserSpf(Activity activity) {
        spf = activity.getApplication().getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    public void setLoggedIn(Boolean isLoggedIn) {
        spf.edit().putBoolean(KEY_IS_LOGGED_ID, isLoggedIn).apply();
    }

    public Boolean isLoggedIn() {
        return spf.getBoolean(KEY_IS_LOGGED_ID, false);
    }

    public void setImageUrl(String imageUrl) {
        spf.edit().putString(KEY_IMAGE_URL, imageUrl).apply();
    }

    public String getImageUrl() {
        return spf.getString(KEY_IMAGE_URL, "");
    }

    public void setAdminLogin(boolean isLogin) {
        spf.edit().putBoolean(KEY_IS_ADMIN, isLogin).apply();
    }

    public boolean isAdminLogin() {
        return spf.getBoolean(KEY_IS_ADMIN, false);
    }
}
