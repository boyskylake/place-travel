package com.example.placetravel.database;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.placetravel.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UserDatabase {

    public interface CreateUserListener {
        void onCreateUserSuccess();

        void onCreateUserFailure();
    }

    public interface UploadImageListener {
        void onUploadImageSuccess(String fileName);

        void onUploadImageFailure();
    }

    public interface UserInfoListener {
        void onGetUserInfoSuccess(UserModel userModel);

        void onGetUserInfoFailure();
    }


    public static void createUser(UserModel userModel, Activity activity, final CreateUserListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user");
        user.child(userModel.getUserId()).setValue(userModel)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onCreateUserSuccess();
                        } else {
                            listener.onCreateUserFailure();
                        }
                    }
                });
    }

    public static void uploadImage(String pathImage, final UploadImageListener listener) {
        Uri file = Uri.fromFile(new File(pathImage));
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = ref.child("user_profile/" + file.getLastPathSegment());
        final UploadTask uploadTask = riversRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.onUploadImageFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    listener.onUploadImageSuccess(taskSnapshot.getMetadata().getName());
                }
            }
        });
    }

    public static void getUserInfo(String userId, final UserInfoListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference user = database.getReference("user");
        user.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                listener.onGetUserInfoSuccess(userModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetUserInfoFailure();
            }
        });
    }
}
