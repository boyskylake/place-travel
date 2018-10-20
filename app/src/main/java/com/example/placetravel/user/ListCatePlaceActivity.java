package com.example.placetravel.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
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
import com.example.placetravel.place.PlaceDetailActivity;
import com.example.placetravel.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class ListCatePlaceActivity extends BaseActivity implements PlaceDatabase.MyCatePlaceListener {

    private PlaceAdapter placeAdapter;
    private View pb;
    private AppCompatTextView tvEmptyPlaceList;

    @Override
    protected void onStart() {
        super.onStart();
        PlaceDatabase.getListCatePlace(getCateNameFromIntent(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeAdapter = new PlaceAdapter();
        setContentView(R.layout.activity_list_cate_place);
        pb = findViewById(R.id.viewProgressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, getCateNameFromIntent());
        tvEmptyPlaceList = findViewById(R.id.tvEmptyPlaceList);
        RecyclerView rvPlace = findViewById(R.id.rvPlaceCateList);
        rvPlace.setLayoutManager(new LinearLayoutManager(this));
        rvPlace.setAdapter(placeAdapter);
    }

    private String getCateNameFromIntent() {
        return getIntent().getStringExtra("name");
    }

    @Override
    public void onLoadMyCatePlaceSuccess(List<PlaceModel> list) {
        pb.setVisibility(View.GONE);
        tvEmptyPlaceList.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        placeAdapter.setItems(list);
    }

    @Override
    public void onLoadMyCatePlaceFailure() {
        pb.setVisibility(View.GONE);
        tvEmptyPlaceList.setVisibility(View.VISIBLE);
        placeAdapter.setItems(new ArrayList<PlaceModel>());
    }


    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

        private List<PlaceModel> items = new ArrayList<>();

        void setItems(List<PlaceModel> newItems) {
            items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
            holder.onBindData(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class PlaceViewHolder extends RecyclerView.ViewHolder {

            private AppCompatImageView imgPlace;
            private AppCompatTextView tvName, tvDes;

            PlaceViewHolder(View itemView) {
                super(itemView);
                imgPlace = itemView.findViewById(R.id.imgPlace);
                tvName = itemView.findViewById(R.id.tvNamePlace);
                tvDes = itemView.findViewById(R.id.tvDesPlace);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ListCatePlaceActivity.this, PlaceDetailActivity.class)
                                .putExtra("data", items.get(getAdapterPosition()))
                                .putExtra("is_from_my_place", false));
                    }
                });
            }

            void onBindData(PlaceModel placeModel) {
                tvName.setText(placeModel.getName());
                tvDes.setText(placeModel.getDescription());
                if (placeModel.getGalleries() != null) {
                    ImageUtil.loadImageFirebase(placeModel.getGalleries().get(0), imgPlace);
                }
            }
        }
    }

}
