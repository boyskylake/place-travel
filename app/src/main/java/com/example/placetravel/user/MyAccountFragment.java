package com.example.placetravel.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.placetravel.R;
import com.example.placetravel.admin.AdminMainActivity;
import com.example.placetravel.database.UserDatabase;
import com.example.placetravel.database.UserSpf;
import com.example.placetravel.model.UserModel;
import com.example.placetravel.place.MyPlaceActivity;
import com.example.placetravel.utils.DialogUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountFragment extends Fragment implements DialogUtil.DialogLogoutCallback,
        UserDatabase.UserInfoListener {

    private AppCompatTextView tvUsername, tvEmail;
    private CircleImageView imgProfile;
    private View progressBar;
    private MyAccountListener listener;
    private UserSpf userSpf;

    public static MyAccountFragment newInstance() {
        return new MyAccountFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_my_account, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view) {
        if (getActivity() != null) userSpf = new UserSpf(getActivity());
        if (getParentFragment() != null) {
            listener = (MyAccountListener) getParentFragment();
        } else if (getActivity() != null) {
            listener = (MyAccountListener) getActivity();
        }
        progressBar = view.findViewById(R.id.viewProgressBar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        imgProfile = view.findViewById(R.id.imgProfile);
        view.findViewById(R.id.btnMyPlace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyPlaceActivity.class));
            }
        });
        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showDialogLogout(getContext(), MyAccountFragment.this);
            }
        });
        UserDatabase.getUserInfo(FirebaseAuth.getInstance().getUid(), this);
    }

    @Override
    public void onUserLogout() {
        listener.onUserLogout();
    }

    @Override
    public void onGetUserInfoSuccess(UserModel userModel) {
        progressBar.setVisibility(View.GONE);
        if (userModel == null) {
            userSpf.setAdminLogin(true);
            startActivity(new Intent(getContext(), AdminMainActivity.class));
            getActivity().finish();
        } else {
            userSpf.setAdminLogin(false);
            updateUI(userModel);
        }
    }

    @Override
    public void onGetUserInfoFailure() {
        progressBar.setVisibility(View.GONE);
        DialogUtil.showDialogError(getActivity(), getString(R.string.txt_error), getString(R.string.txt_get_user_info_error));
    }

    private void updateUI(UserModel userModel) {
        tvEmail.setText(userModel.getEmail());
        tvUsername.setText(userModel.getUsername());
        userSpf.setImageUrl(userModel.getImageUrl());
        if (getContext() != null) {
            if (userModel.getImageUrl().startsWith("https://graph.facebook.com/")) {
                Glide.with(getContext()).load(userModel.getImageUrl()).into(imgProfile);
            } else {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference islandRef = storageRef.child("user_profile/" + userModel.getImageUrl());
                Glide.with(getContext()).load(islandRef).into(imgProfile);
            }
        }
    }

    public interface MyAccountListener {
        void onUserLogout();
    }
}
