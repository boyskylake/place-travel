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

import com.example.placetravel.R;
import com.example.placetravel.database.PlaceDatabase;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.place.PlaceDetailActivity;
import com.example.placetravel.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class PlaceFragment extends Fragment implements PlaceDatabase.GetListPlaceListener {

    private View pb, tvEmptyList;
    private PlaceAdapter placeAdapter;

    public static PlaceFragment newInstance() {
        return new PlaceFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        PlaceDatabase.getListPlace(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeAdapter = new PlaceAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_place, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view) {
        pb = view.findViewById(R.id.viewProgressBar);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        RecyclerView rvPlace = view.findViewById(R.id.rvPlace);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rvPlace.setLayoutManager(mLayoutManager);
        rvPlace.setAdapter(placeAdapter);
    }

    @Override
    public void onGetListPlaceSuccess(List<PlaceModel> list) {
        pb.setVisibility(View.GONE);
        tvEmptyList.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        placeAdapter.setItems(list);
    }

    @Override
    public void onGetListPlaceFailure() {
        pb.setVisibility(View.GONE);
        tvEmptyList.setVisibility(View.VISIBLE);
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
                        startActivity(new Intent(getActivity(), PlaceDetailActivity.class)
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
