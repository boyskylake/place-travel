package com.example.placetravel.place;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.placetravel.R;
import com.example.placetravel.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

class GalleryAdapter extends PagerAdapter {

    private List<String> items = new ArrayList<>();

    void setItems(List<String> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(view.getContext()).inflate(R.layout.item_gallery, view, false);
        final AppCompatImageView imageView = imageLayout.findViewById(R.id.image);
        ImageUtil.loadImageFirebase(items.get(position), imageView);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
