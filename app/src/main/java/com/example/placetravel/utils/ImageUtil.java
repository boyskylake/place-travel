package com.example.placetravel.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {

    public static String getImagePath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                return path;
            }
        }
        return "";
    }

    public static void loadImageFirebase(String url, AppCompatImageView view) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = storageRef.child("place/" + url);
        Glide.with(view.getContext()).load(islandRef).into(view);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFileName() {
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + date;
    }
}
