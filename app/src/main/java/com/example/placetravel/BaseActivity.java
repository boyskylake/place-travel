package com.example.placetravel;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    protected void setupToolbar(Toolbar toolbar, int title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
