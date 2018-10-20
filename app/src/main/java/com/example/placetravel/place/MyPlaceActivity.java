package com.example.placetravel.place;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.database.PlaceDatabase;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.utils.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyPlaceActivity extends BaseActivity implements PlaceDatabase.GetListPlaceListener {

    private View pb, tvEmpty;
    private MyPlaceAdapter myPlaceAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        PlaceDatabase.getListMyPlace(FirebaseAuth.getInstance().getUid(), this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.txt_my_place);
        pb = findViewById(R.id.viewProgressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        myPlaceAdapter = new MyPlaceAdapter();
        RecyclerView rvMyPlace = findViewById(R.id.rvMyPlace);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rvMyPlace.setLayoutManager(mLayoutManager);
        rvMyPlace.setAdapter(myPlaceAdapter);
        findViewById(R.id.fabAddPlace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPlaceActivity.this, PlaceFormActivity.class)
                        .putExtra("is_create", true));
            }
        });
    }

    @Override
    public void onGetListPlaceSuccess(List<PlaceModel> list) {
        pb.setVisibility(View.GONE);
        myPlaceAdapter.setItems(list);
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onGetListPlaceFailure() {
        pb.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        myPlaceAdapter.setItems(new ArrayList<PlaceModel>());
    }

    class MyPlaceAdapter extends RecyclerView.Adapter<MyPlaceAdapter.MyPlaceViewHolder> {

        private List<PlaceModel> items = new ArrayList<>();

        void setItems(List<PlaceModel> newItems) {
            items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyPlaceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_place, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyPlaceViewHolder holder, int position) {
            holder.onBindData(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class MyPlaceViewHolder extends RecyclerView.ViewHolder {

            private AppCompatTextView tvName;
            private AppCompatImageView imgPlace;

            MyPlaceViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvNamePlace);
                imgPlace = itemView.findViewById(R.id.imgPlace);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyPlaceActivity.this, PlaceDetailActivity.class)
                                .putExtra("data", items.get(getAdapterPosition()))
                                .putExtra("is_from_my_place", true));
                    }
                });
            }

            void onBindData(PlaceModel placeModel) {
                tvName.setText(placeModel.getName());
                if (placeModel.getGalleries() != null) {
                    ImageUtil.loadImageFirebase(placeModel.getGalleries().get(0), imgPlace);
                }
            }
        }
    }
}
