package com.example.placetravel.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.placetravel.R;
import com.example.placetravel.auth.LoginFragment;
import com.example.placetravel.database.UserSpf;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment implements LoginFragment.LoginListener,
        MyAccountFragment.MyAccountListener {

    private UserSpf userSpf;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) userSpf = new UserSpf(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_account, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view) {
        checkStateView();
    }

    @Override
    public void onLoginSuccess() {
        userSpf.setLoggedIn(true);
        checkStateView();
    }

    @Override
    public void onUserLogout() {
        userSpf.setLoggedIn(false);
        userSpf.setAdminLogin(false);
        checkStateView();
        FirebaseAuth.getInstance().signOut();
    }

    private void checkStateView() {
        Fragment fragment;
        if (userSpf.isLoggedIn()) {
            fragment = MyAccountFragment.newInstance();
        } else {
            fragment = LoginFragment.newInstance();
        }
        getChildFragmentManager().beginTransaction().replace(R.id.containerView, fragment).commit();
    }
}
