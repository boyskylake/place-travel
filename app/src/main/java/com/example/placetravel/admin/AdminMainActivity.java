package com.example.placetravel.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.admin.news.NewsFragment;
import com.example.placetravel.admin.place.PlaceFragment;
import com.example.placetravel.database.UserSpf;
import com.example.placetravel.user.MainActivity;
import com.example.placetravel.utils.DialogUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends BaseActivity implements DialogUtil.DialogLogoutCallback {

    private UserSpf userSpf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        userSpf = new UserSpf(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        setupToolbar(toolbar, R.string.txt_admin);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            DialogUtil.showDialogLogout(this, this);
        }
        return true;
    }

    @Override
    public void onUserLogout() {
        userSpf.setLoggedIn(false);
        userSpf.setAdminLogin(false);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> menus = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            menus.add(getString(R.string.txt_menu_place));
            menus.add(getString(R.string.txt_menu_news));
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return PlaceFragment.newInstance();
            } else {
                return NewsFragment.newInstance();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return menus.get(position);
        }

        @Override
        public int getCount() {
            return menus.size();
        }
    }
}
