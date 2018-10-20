package com.example.placetravel.admin.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.placetravel.R;
import com.example.placetravel.database.UserSpf;
import com.example.placetravel.model.NewsModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewsFragment extends Fragment {

    private RecyclerView rvNews;
    private View tvEmpty, pb;
    private FirebaseRecyclerAdapter<NewsModel, NewsViewHolder> adapter;
    private UserSpf userSpf;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) adapter.stopListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        setupInstance();
        setupView(view);
        return view;
    }

    private void setupInstance() {
        if (getActivity() != null) userSpf = new UserSpf(getActivity());
    }

    private void setupView(View view) {
        rvNews = view.findViewById(R.id.rvNews);
        tvEmpty = view.findViewById(R.id.tvEmptyList);
        pb = view.findViewById(R.id.viewProgressBar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        rvNews.setLayoutManager(mLayoutManager);
        View fabCreateNews = view.findViewById(R.id.fabAddNews);
        if (userSpf != null) {
            if (userSpf.isAdminLogin()) fabCreateNews.setVisibility(View.VISIBLE);
        }
        fabCreateNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateNewsActivity.class));
            }
        });
        getListNews();
    }

    private void getListNews() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseRecyclerOptions<NewsModel> options = new FirebaseRecyclerOptions.Builder<NewsModel>()
                .setQuery(database.getReference("news"), NewsModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<NewsModel, NewsViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                pb.setVisibility(View.GONE);
                if (adapter.getItemCount() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull NewsModel model) {
                final DatabaseReference newsRef = getRef(position);
                model.setId(newsRef.getKey());
                holder.onBindData(model);
            }

            @NonNull
            @Override
            public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false));
            }
        };
        adapter.startListening();
        rvNews.setAdapter(adapter);
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvName, tvDes;

        NewsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDes = itemView.findViewById(R.id.tvDes);
        }

        public void onBindData(final NewsModel model) {
            tvName.setText(model.getTitle());
            tvDes.setText(model.getDes());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userSpf.isAdminLogin()) {
                        startActivity(new Intent(getActivity(), CreateNewsActivity.class)
                                .putExtra("data", model)
                                .putExtra("is_edit", true));
                    } else {
                        startActivity(new Intent(getActivity(), DetailNewsActivity.class)
                                .putExtra("data", model));
                    }
                }
            });
        }
    }
}
