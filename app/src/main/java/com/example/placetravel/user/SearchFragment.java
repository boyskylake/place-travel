package com.example.placetravel.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.placetravel.R;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.place.PlaceDetailActivity;
import com.example.placetravel.utils.ImageUtil;
import com.example.placetravel.utils.TextSearchObservable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private AppCompatTextView tvEmptyList;
    private AppCompatEditText etSearch;
    private TextSearchObservable searchObservable;
    private RecyclerView rvSearch;
    private FirebaseRecyclerAdapter<PlaceModel, SearchViewHolder> adapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        setupInstance();
        setupView(view);
        return view;
    }

    private void setupInstance() {

    }

    @SuppressLint("CheckResult")
    public void setupView(View view) {
        tvEmptyList = view.findViewById(R.id.tvEmptyList);

        rvSearch = view.findViewById(R.id.rvSearch);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearch = view.findViewById(R.id.etSearch);
        searchObservable = new TextSearchObservable(etSearch);
        Observable.create(searchObservable)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    private Consumer<String> onNext = new Consumer<String>() {
        @Override
        public void accept(String result) {
            if (!result.trim().isEmpty()) {
                firebaseSearchShop(result);
            } else {
                if (adapter != null) {
                    adapter.stopListening();
                    tvEmptyList.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    private void firebaseSearchShop(String result) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference shop = database.getReference("place");
        Query query = shop.orderByChild("name").startAt(result).endAt(result + "\uf8ff");
        FirebaseRecyclerOptions<PlaceModel> options = new FirebaseRecyclerOptions.Builder<PlaceModel>()
                .setQuery(query, PlaceModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<PlaceModel, SearchViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (adapter.getItemCount() == 0) {
                    tvEmptyList.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyList.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull PlaceModel model) {
                final DatabaseReference shopRef = getRef(position);
                model.setId(shopRef.getKey());
                holder.onBindData(model);
            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false));
            }
        };
        adapter.startListening();
        rvSearch.setAdapter(adapter);
    }

    private Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) {
            throwable.printStackTrace();
            Toast.makeText(getActivity(), R.string.txt_please_try_again, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        etSearch.removeTextChangedListener(searchObservable.getListener());
        if (adapter != null) adapter.stopListening();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView imgPlace;
        private AppCompatTextView tvNamePlace;

        SearchViewHolder(View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace);
            tvNamePlace = itemView.findViewById(R.id.tvNamePlace);
        }

        void onBindData(final PlaceModel model) {
            tvNamePlace.setText(model.getName());
            if (model.getGalleries() != null) {
                ImageUtil.loadImageFirebase(model.getGalleries().get(0), imgPlace);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PlaceDetailActivity.class).putExtra("data", model));
                }
            });
        }
    }
}