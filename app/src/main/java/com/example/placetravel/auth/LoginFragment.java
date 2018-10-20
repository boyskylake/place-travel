package com.example.placetravel.auth;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.placetravel.R;
import com.example.placetravel.database.UserDatabase;
import com.example.placetravel.model.UserModel;
import com.example.placetravel.utils.DateTimeUtil;
import com.example.placetravel.utils.DialogUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginFragment extends Fragment implements UserDatabase.CreateUserListener {

    private TextInputEditText etEmail, etPassword;
    private LoginListener listener;
    private CallbackManager callbackManager;
    private FirebaseAuth auth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupInstance() {
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        DialogUtil.showDialogError(getActivity(), getString(R.string.txt_auth_fail), getString(R.string.txt_please_try_again));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        DialogUtil.showDialogError(getActivity(), getString(R.string.txt_auth_fail), exception.getMessage());
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        if (getActivity() != null) {
            DialogUtil.showDialog(getActivity());
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                createUser();
                                LoginManager.getInstance().logOut();
                            } else {
                                Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setupView(View view) {
        if (getParentFragment() != null) {
            listener = ((LoginListener) getParentFragment());
        } else if (getActivity() != null) {
            listener = ((LoginListener) getActivity());
        }
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        AppCompatTextView tvRegister = view.findViewById(R.id.tvRegister);
        tvRegister.setPaintFlags(tvRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });
        view.findViewById(R.id.btnLoginEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    DialogUtil.showDialog(getActivity());
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DialogUtil.dismissDialog();
                                        listener.onLoginSuccess();
                                    } else {
                                        DialogUtil.dismissDialog();
                                        if (task.getException() != null) {
                                            DialogUtil.showDialogError(getActivity(),
                                                    getString(R.string.txt_auth_fail),
                                                    task.getException().getMessage());
                                        }
                                    }
                                }
                            });
                }
            }
        });
        view.findViewById(R.id.btnLoginFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(
                        LoginFragment.this,
                        Arrays.asList("email", "public_profile")
                );
            }
        });
    }

    private void createUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String urlImage = "";
            if (!user.getProviderData().isEmpty() && user.getProviderData().size() > 1)
                urlImage = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?height=300";
            UserModel userModel = new UserModel();
            userModel.setTimestamp(DateTimeUtil.getTimeUTC());
            userModel.setUsername(user.getDisplayName());
            userModel.setUserId(auth.getUid());
            userModel.setEmail(user.getEmail());
            userModel.setImageUrl(urlImage);
            UserDatabase.createUser(userModel, getActivity(), this);
        }
    }

    @Override
    public void onCreateUserSuccess() {
        DialogUtil.dismissDialog();
        listener.onLoginSuccess();
    }

    @Override
    public void onCreateUserFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(getActivity(), getString(R.string.txt_auth_fail), getString(R.string.txt_please_try_again));
    }

    public interface LoginListener {
        void onLoginSuccess();
    }
}
