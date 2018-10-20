package com.example.placetravel.place;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.database.PlaceDatabase;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.model.RateModel;
import com.example.placetravel.model.RatingModel;
import com.example.placetravel.utils.DialogUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class PlaceDetailActivity extends BaseActivity implements DialogUtil.DialogDeleteShopCallback,PlaceDatabase.DeletePlaceListener, PlaceDatabase.GetRatingListener, PlaceDatabase.AddRatingPlaceListener {

    private AppCompatTextView tvName, tvcate, tvDes, tvPhoneNumber, tvFacebook, tvRating, tvSumUserRating;
    private AppCompatImageView imgLocation;
    private List<AppCompatImageView> imgRating = new ArrayList<>();
    private List<AppCompatImageView> stars = new ArrayList<>();
    private PlaceModel placeModel;
    private GalleryAdapter galleryAdapter;
    private Toolbar toolbar;
    private CircleIndicator indicator;
    private ViewPager gallery;
    private View containerControl, btnActionSubmit;
    private ScrollView scrollView;
    private int rating = 0;
    private int oldRating = 0;
    private AppCompatTextView tvTotalRateOne, tvTotalRateTwo, tvTotalRateThree,
            tvTotalRateFour, tvTotalRateFive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        placeModel = getIntent().getParcelableExtra("data");
        toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, placeModel.getName());
        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFromMyPlace()) {
            getMenuInflater().inflate(R.menu.detail_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit) {
            startActivityForResult(new Intent(this, PlaceFormActivity.class)
                    .putExtra("data", placeModel), 1008);
        } else if (itemId == R.id.action_delete) {
            DialogUtil.showDialogDeletePlace(this, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1008) {
            if (data != null) {
                PlaceModel newPlaceModel = data.getParcelableExtra("new_data");
                if (newPlaceModel != null) {
                    placeModel.setGalleries(newPlaceModel.getGalleries());
                    placeModel.setName(newPlaceModel.getName());
                    placeModel.setPhoneNumber(newPlaceModel.getPhoneNumber());
                    placeModel.setLng(newPlaceModel.getLng());
                    placeModel.setLat(newPlaceModel.getLat());
                    placeModel.setDescription(newPlaceModel.getDescription());
                    setupData();
                }
            }
        }
    }

    @Override
    public void onDeleteShop() {
        DialogUtil.showDialog(this);
        PlaceDatabase.deletePlace(placeModel.getId(), this);
    }

    @Override
    public void onDeletePlaceSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDeletePlaceFailure() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_please_try_again, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetRatingPlaceSuccess(RatingModel ratingModel) {
        DialogUtil.dismissDialog();
        setupViewRating(ratingModel);
    }

    @Override
    public void onGetRatingPlaceFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
    }

    @Override
    public void onAddRatingSuccess() {
        DialogUtil.dismissDialog();
        loadRating();
    }

    @Override
    public void onAddRatingFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
    }

    private boolean isFromMyPlace() {
        return getIntent().getBooleanExtra("is_from_my_place", false);
    }

    private void setupView() {
        loadRating();
        View addRateView = findViewById(R.id.addRateView);
        View rateView = findViewById(R.id.ratingView);
        View viewTotalRate = findViewById(R.id.viewTotalRate);
        scrollView = findViewById(R.id.scrollView);
        containerControl = findViewById(R.id.containerControl);
        btnActionSubmit = findViewById(R.id.btnActionSubmit);
        findViewById(R.id.btnActionCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStarView(oldRating, false);
            }
        });
        tvRating = findViewById(R.id.tvRating);
        tvSumUserRating = findViewById(R.id.tvSumUserRating);
        imgRating.add((AppCompatImageView) findViewById(R.id.imgStarOne));
        imgRating.add((AppCompatImageView) findViewById(R.id.imgStarTwo));
        imgRating.add((AppCompatImageView) findViewById(R.id.imgStarThree));
        imgRating.add((AppCompatImageView) findViewById(R.id.imgStarFour));
        imgRating.add((AppCompatImageView) findViewById(R.id.imgStarFive));
        stars.add((AppCompatImageView) findViewById(R.id.starOne));
        stars.add((AppCompatImageView) findViewById(R.id.starTwo));
        stars.add((AppCompatImageView) findViewById(R.id.starThree));
        stars.add((AppCompatImageView) findViewById(R.id.starFour));
        stars.add((AppCompatImageView) findViewById(R.id.starFive));
        tvTotalRateOne = findViewById(R.id.tvTotalStarOne);
        tvTotalRateTwo = findViewById(R.id.tvTotalStarTwo);
        tvTotalRateThree = findViewById(R.id.tvTotalStarThree);
        tvTotalRateFour = findViewById(R.id.tvTotalStarFour);
        tvTotalRateFive = findViewById(R.id.tvTotalStarFive);
        for (AppCompatImageView star : stars) {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = Integer.parseInt(String.valueOf(v.getTag()));
                    setStarView(tag, true);
                }
            });
        }
        tvName = findViewById(R.id.tvName);
        tvcate = findViewById(R.id.tvcate);
        tvDes = findViewById(R.id.tvDes);
        tvFacebook = findViewById(R.id.tvFacebook);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        imgLocation = findViewById(R.id.imgLocation);
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geoUriString = "geo:" + placeModel.getLat() + "," +
                        +placeModel.getLng() + "?q=" + placeModel.getName() + "@" +
                        +placeModel.getLat() + "," + placeModel.getLng();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString)));
            }
        });
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", placeModel.getPhoneNumber(), null));
                startActivity(intent);
            }
        });
        galleryAdapter = new GalleryAdapter();
        gallery = findViewById(R.id.gallery);
        gallery.setAdapter(galleryAdapter);
        indicator = findViewById(R.id.indicator);

        if (isFromMyPlace()) {
            addRateView.setVisibility(View.GONE);
            viewTotalRate.setVisibility(View.GONE);
        } else {
            String userId = FirebaseAuth.getInstance().getUid();
            if (userId != null) {
                if (userId.equals(placeModel.getUserId())) addRateView.setVisibility(View.GONE);
            } else {
                addRateView.setVisibility(View.GONE);
            }
            rateView.setVisibility(View.GONE);
        }

        findViewById(R.id.btnSeeComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOwner = false;
                String userId = FirebaseAuth.getInstance().getUid();
                if (userId != null) isOwner = userId.equals(placeModel.getUserId());
                startActivity(new Intent(PlaceDetailActivity.this, CommentActivity.class)
                        .putExtra("place_id", placeModel.getId())
                        .putExtra("is_owner", isOwner));
            }
        });
        setupData();
    }

    @SuppressLint("SetTextI18n")
    private void setupData() {
        toolbar.setTitle(placeModel.getName());
        galleryAdapter.setItems(placeModel.getGalleries());
        tvName.setText(placeModel.getName());
        tvcate.setText("หมวดหมู่ : "+placeModel.getCategory());
        tvDes.setText(placeModel.getDescription());
        tvFacebook.setText(getString(R.string.txt_title_facebook) + " " + placeModel.getLinkFacebook());
        tvPhoneNumber.setText(getString(R.string.txt_title_tel) + " " + Html.fromHtml("<u>" + placeModel.getPhoneNumber() + "</u>"), TextView.BufferType.SPANNABLE);
        String url = "http://maps.google.com/maps/api/staticmap?center=" +
                placeModel.getLat() + "," + placeModel.getLng() + "&zoom=16&size=500x500&" +
                "sensor=false&" + "markers=color:red%7C" + placeModel.getLat() + "," + placeModel.getLng();
        Glide.with(this).load(url).into(imgLocation);
        indicator.setViewPager(gallery);
    }

    private void loadRating() {
        DialogUtil.showDialog(this);
        PlaceDatabase.getRating(placeModel.getId(), FirebaseAuth.getInstance().getUid(), this);
    }

    private void setStarView(int tag, boolean isShowControl) {
        rating = tag;
        for (int i = 0; i < stars.size(); i++) {
            if (tag == 0) stars.get(i).setSelected(false);
            else stars.get(i).setSelected(i <= (tag - 1));
        }
        if (isShowControl) {
            containerControl.setVisibility(View.VISIBLE);
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 300
            );
        } else {
            containerControl.setVisibility(View.GONE);
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void setupViewRating(RatingModel ratingModel) {
        oldRating = ratingModel.getRate();
        setStarView(ratingModel.getRate(), false);
        btnActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showDialog(PlaceDetailActivity.this);
                RateModel data = new RateModel();
                data.setShopId(placeModel.getId());
                data.setUserId(FirebaseAuth.getInstance().getUid());
                data.setRate(rating);
                PlaceDatabase.addRating(data, PlaceDetailActivity.this);
            }
        });
        tvSumUserRating.setText(String.valueOf(ratingModel.getTotalUserRating()));
        tvRating.setText(getString(R.string.txt_avg_rating, String.valueOf(ratingModel.getAvgRating())));
        tvTotalRateOne.setText(String.valueOf(ratingModel.getTotalOneRate()));
        tvTotalRateTwo.setText(String.valueOf(ratingModel.getTotalTwoRate()));
        tvTotalRateThree.setText(String.valueOf(ratingModel.getTotalThreeRate()));
        tvTotalRateFour.setText(String.valueOf(ratingModel.getTotalFourRate()));
        tvTotalRateFive.setText(String.valueOf(ratingModel.getTotalFiveRate()));
        for (int i = 0; i < imgRating.size(); i++) {
            if (ratingModel.getAvgRating() == 0) imgRating.get(i).setSelected(false);
            else imgRating.get(i).setSelected(i + 1 <= ratingModel.getAvgRating());
        }
    }
}