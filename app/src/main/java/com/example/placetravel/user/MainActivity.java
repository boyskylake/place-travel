package com.example.placetravel.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.admin.AdminMainActivity;
import com.example.placetravel.admin.news.NewsFragment;
import com.example.placetravel.database.UserSpf;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserSpf userSpf = new UserSpf(this);

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (userSpf.isAdminLogin()) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> menus = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            menus.add(getString(R.string.txt_menu_place));
            menus.add(getString(R.string.txt_menu_placecate));
            menus.add(getString(R.string.txt_menu_search));
            menus.add(getString(R.string.txt_menu_news));
            menus.add(getString(R.string.txt_menu_account));
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return PlaceFragment.newInstance();
            } else if (position == 1) {
                return PlaceCateFragment.newInstance();
            }else if (position == 2) {
                return SearchFragment.newInstance();
            } else if (position == 3) {
                return NewsFragment.newInstance();
            } else {
                return AccountFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return menus.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return menus.get(position);
        }
    }
}
