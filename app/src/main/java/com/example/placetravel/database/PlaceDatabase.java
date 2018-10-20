package com.example.placetravel.database;

import android.support.annotation.NonNull;

import com.example.placetravel.model.CommentModel;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.model.RateModel;
import com.example.placetravel.model.RatingModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlaceDatabase {

    public interface GetListPlaceListener {
        void onGetListPlaceSuccess(List<PlaceModel> list);

        void onGetListPlaceFailure();
    }
    public interface MyCatePlaceListener {
        void onLoadMyCatePlaceSuccess(List<PlaceModel> list);

        void onLoadMyCatePlaceFailure();
    }

    public interface CreatePlaceListener {
        void onCreatePlaceSuccess();

        void onCreatePlaceFailure();
    }

    public interface EditPlaceListener {
        void onEditPlaceSuccess();

        void onEditPlaceFailure();
    }

    public interface DeletePlaceListener {
        void onDeletePlaceSuccess();

        void onDeletePlaceFailure();
    }

    public interface CommentListener {
        void onCommentSuccess();

        void onCommentFailure();
    }

    public interface GetRatingListener {
        void onGetRatingPlaceSuccess(RatingModel ratingModel);

        void onGetRatingPlaceFailure();
    }

    public interface AddRatingPlaceListener {
        void onAddRatingSuccess();

        void onAddRatingFailure();
    }
    public static void getListCatePlace(final String cateplace,final MyCatePlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference shop = database.getReference("place");
        shop.orderByChild("category").equalTo(cateplace)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlaceModel> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceModel placeModel = postSnapshot.getValue(PlaceModel.class);
                    if (placeModel != null) {
                        placeModel.setId(postSnapshot.getKey());
                    }
                    list.add(placeModel);
                }
                listener.onLoadMyCatePlaceSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoadMyCatePlaceFailure();
            }
        });
    }

    public static void getListPlace(final GetListPlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference shop = database.getReference("place");
        shop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlaceModel> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceModel placeModel = postSnapshot.getValue(PlaceModel.class);
                    if (placeModel != null) {
                        placeModel.setId(postSnapshot.getKey());
                    }
                    list.add(placeModel);
                }
                listener.onGetListPlaceSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetListPlaceFailure();
            }
        });
    }

    public static void getListMyPlace(String userId, final GetListPlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference shop = database.getReference("place");
        shop.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlaceModel> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceModel placeModel = postSnapshot.getValue(PlaceModel.class);
                    if (placeModel != null) {
                        placeModel.setId(postSnapshot.getKey());
                    }
                    list.add(placeModel);
                }
                listener.onGetListPlaceSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetListPlaceFailure();
            }
        });
    }

    public static void createPlace(PlaceModel placeModel, final CreatePlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference place = database.getReference("place");
        place.push().setValue(placeModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onCreatePlaceSuccess();
                        } else {
                            listener.onCreatePlaceFailure();
                        }
                    }
                });
    }

    public static void updatePlace(String placeId, PlaceModel placeModel, final EditPlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference place = database.getReference("place");
        place.child(placeId).setValue(placeModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onEditPlaceSuccess();
                } else {
                    listener.onEditPlaceFailure();
                }
            }
        });
    }

    public static void deletePlace(String placeId, final DeletePlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference place = database.getReference("place");
        place.child(placeId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onDeletePlaceSuccess();
                } else {
                    listener.onDeletePlaceFailure();
                }
            }
        });
    }

    public static void comment(CommentModel model, final CommentListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference comment = database.getReference("comment");
        comment.child(model.getPlaceId()).push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onCommentSuccess();
                } else {
                    listener.onCommentFailure();
                }
            }
        });
    }

    public static void getRating(final String placeId, final String userId, final GetRatingListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rating = database.getReference("rating");
        rating.child(placeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double avgRating;
                int countFiveRate = 0;
                int countFourRate = 0;
                int countThreeRate = 0;
                int countTwoRate = 0;
                int countOneRate = 0;
                RatingModel ratingModel = new RatingModel();
                ratingModel.setPlaceId(placeId);
                ratingModel.setUserId(userId);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RatingModel data = snapshot.getValue(RatingModel.class);
                    if (data != null) {
                        if (data.getUserId().equals(userId)) {
                            ratingModel.setRate(data.getRate());
                        }
                        if (data.getRate() == 5) countFiveRate++;
                        if (data.getRate() == 4) countFourRate++;
                        if (data.getRate() == 3) countThreeRate++;
                        if (data.getRate() == 2) countTwoRate++;
                        if (data.getRate() == 1) countOneRate++;
                    }
                }
                ratingModel.setTotalFiveRate(countFiveRate);
                ratingModel.setTotalFourRate(countFourRate);
                ratingModel.setTotalThreeRate(countThreeRate);
                ratingModel.setTotalTwoRate(countTwoRate);
                ratingModel.setTotalOneRate(countOneRate);
                ratingModel.setRated(ratingModel.getRate() != 0);

                int totalRating = (ratingModel.getTotalFiveRate() * 5) + (ratingModel.getTotalFourRate() * 4) +
                        (ratingModel.getTotalThreeRate() * 3) + (ratingModel.getTotalTwoRate() * 2) +
                        (ratingModel.getTotalOneRate());
                int sumRating = ratingModel.getTotalFiveRate() + ratingModel.getTotalFourRate() +
                        ratingModel.getTotalThreeRate() + ratingModel.getTotalTwoRate() +
                        ratingModel.getTotalOneRate();

                ratingModel.setTotalUserRating(sumRating);
                DecimalFormat df = new DecimalFormat("#.#");
                if (sumRating != 0.0) {
                    avgRating = (double) (totalRating / sumRating);
                    ratingModel.setAvgRating(Double.parseDouble(df.format(avgRating)));
                } else {
                    ratingModel.setAvgRating(0.0);
                }
                listener.onGetRatingPlaceSuccess(ratingModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onGetRatingPlaceFailure();
            }
        });
    }

    public static void addRating(RateModel model, final AddRatingPlaceListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rating = database.getReference("rating");
        rating.child(model.getShopId()).child(model.getUserId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onAddRatingSuccess();
                } else {
                    listener.onAddRatingFailure();
                }
            }
        });
    }
}
