package com.example.placetravel.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.placetravel.R;
import com.example.placetravel.model.PlaceCateModel;
import com.example.placetravel.place.PlaceDetailActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PlaceCateFragment extends Fragment {

    private RecyclerView rvPlaceCate;

    public static PlaceCateFragment newInstance() {
        return new PlaceCateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_place_cate, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] names = getResources().getStringArray(R.array.category);
        List<PlaceCateModel> placeCateModels = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            PlaceCateModel placeCateModel = new PlaceCateModel();
            placeCateModel.setName(names[i]);
                placeCateModel.setUrlImage("placecate-" + (i + 1) + ".jpg");
            placeCateModels.add(placeCateModel);
        }
        PlaceCateAdapter placeCateAdapter = new PlaceCateAdapter();
        rvPlaceCate.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaceCate.setAdapter(placeCateAdapter);
        placeCateAdapter.setItems(placeCateModels);
    }

    private void setupView(View view) {
        rvPlaceCate = view.findViewById(R.id.rvPlaceCate);
    }

    class PlaceCateAdapter extends RecyclerView.Adapter<PlaceCateAdapter.PlaceCateViewHolder> {

        private List<PlaceCateModel> items = new ArrayList<>();

        void setItems(List<PlaceCateModel> newItems) {
            items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlaceCateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaceCateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cate_place, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceCateViewHolder holder, int position) {
            holder.onBindData(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class PlaceCateViewHolder extends RecyclerView.ViewHolder {

            private AppCompatImageView imgPlaceCate;
            private AppCompatTextView tvNameCate;

            PlaceCateViewHolder(View itemView) {
                super(itemView);
                imgPlaceCate = itemView.findViewById(R.id.imgPlaceCate);
                tvNameCate = itemView.findViewById(R.id.tvNameCate);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ListCatePlaceActivity.class)
                         .putExtra("name", items.get(getAdapterPosition()).getName())
                         .putExtra("is_from_my_place", false));
                    }
                });
            }
            void onBindData(PlaceCateModel placeCateModel) {
                tvNameCate.setText(placeCateModel.getName());
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference islandRef = storageRef.child("cate_place/" + placeCateModel.getUrlImage());
                Glide.with(itemView.getContext()).load(islandRef).into(imgPlaceCate);
            }
        }
    }
}
