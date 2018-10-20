package com.example.placetravel.admin.news;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.model.NewsModel;

public class DetailNewsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        NewsModel newsModel = getIntent().getParcelableExtra("data");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.txt_topic_news);
        setSupportActionBar(toolbar);
        AppCompatTextView tvTitle = findViewById(R.id.tvTitle);
        AppCompatTextView tvDes = findViewById(R.id.tvDes);
        tvTitle.setText(newsModel.getTitle());
        tvDes.setText(newsModel.getDes());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
