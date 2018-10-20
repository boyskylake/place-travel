package com.example.placetravel.admin.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.placetravel.BaseActivity;
import com.example.placetravel.R;
import com.example.placetravel.database.NewsDatabase;
import com.example.placetravel.model.NewsModel;
import com.example.placetravel.utils.DialogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CreateNewsActivity extends BaseActivity implements DialogUtil.DialogDeleteNewsCallback,
        NewsDatabase.DeleteNewsListener, NewsDatabase.UpdateNewsListener, NewsDatabase.CreateNewsListener {

    private TextInputEditText etTitle, etDes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        int titleId = R.string.txt_create_news;
        if (isModeEdit()) titleId = R.string.txt_edit_news;
        toolbar.setTitle(titleId);
        setupToolbar(toolbar, titleId);
        etTitle = findViewById(R.id.etTitle);
        etDes = findViewById(R.id.etDes);
        if (isModeEdit()) prefillData();
    }

    private void prefillData() {
        etTitle.setText(getNewsModel().getTitle());
        etDes.setText(getNewsModel().getDes());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        menu.findItem(R.id.action_delete).setVisible(isModeEdit());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            DialogUtil.showDialogDeleteNews(this, this);
        } else if (item.getItemId() == R.id.action_save) {
            saveNews();
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveNews() {
        String title = etTitle.getText().toString();
        String des = etDes.getText().toString();
        if (title.isEmpty()) {
            DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_title_news));
        } else if (des.isEmpty()) {
            DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_des_news));
        } else {
            NewsModel newsModel = new NewsModel();
            newsModel.setTitle(title);
            newsModel.setDes(des);
            DialogUtil.showDialog(this);
            if (isModeEdit()) {
                newsModel.setCreateTimestamp(getNewsModel().getCreateTimestamp());
                newsModel.setUpdateTimestamp(getTimeUTC());
                NewsDatabase.updateNews(getNewsModel().getId(), newsModel, this);
            } else {
                newsModel.setCreateTimestamp(getTimeUTC());
                NewsDatabase.createNews(newsModel, this);
            }
        }
    }

    private boolean isModeEdit() {
        return getIntent().getBooleanExtra("is_edit", false);
    }

    private NewsModel getNewsModel() {
        return getIntent().getParcelableExtra("data");
    }

    @Override
    public void onDeleteNews() {
        DialogUtil.showDialog(this);
        NewsDatabase.deleteNews(getNewsModel().getId(), this);
    }

    @Override
    public void onDeleteNewsSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onDeleteNewsFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_try_again));
    }

    @Override
    public void onUpdateNewsSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUpdateNewsFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_try_again));
    }

    @Override
    public void onCreateNewsSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCreateNewsFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_try_again));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeUTC() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date());
    }
}