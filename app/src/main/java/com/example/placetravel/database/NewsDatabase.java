package com.example.placetravel.database;

import android.support.annotation.NonNull;

import com.example.placetravel.model.NewsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewsDatabase {

    public interface CreateNewsListener {
        void onCreateNewsSuccess();

        void onCreateNewsFailure();
    }

    public interface UpdateNewsListener {
        void onUpdateNewsSuccess();

        void onUpdateNewsFailure();
    }

    public interface DeleteNewsListener {
        void onDeleteNewsSuccess();

        void onDeleteNewsFailure();
    }

    public static void createNews(NewsModel model, final CreateNewsListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference news = database.getReference("news");
        news.push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onCreateNewsSuccess();
                } else {
                    listener.onCreateNewsFailure();
                }
            }
        });
    }

    public static void updateNews(String newsId, NewsModel model, final UpdateNewsListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference news = database.getReference("news");
        news.child(newsId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onUpdateNewsSuccess();
                } else {
                    listener.onUpdateNewsFailure();
                }
            }
        });
    }

    public static void deleteNews(String newsId, final DeleteNewsListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference news = database.getReference("news");
        news.child(newsId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onDeleteNewsSuccess();
                } else {
                    listener.onDeleteNewsFailure();
                }
            }
        });
    }

    public static DatabaseReference getNews() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference("news");
    }
}