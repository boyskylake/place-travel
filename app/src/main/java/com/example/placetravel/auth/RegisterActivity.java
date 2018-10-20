package com.example.placetravel.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.placetravel.BaseActivity;
import com.example.placetravel.BuildConfig;
import com.example.placetravel.R;
import com.example.placetravel.database.UserDatabase;
import com.example.placetravel.model.UserModel;
import com.example.placetravel.utils.DateTimeUtil;
import com.example.placetravel.utils.DialogUtil;
import com.example.placetravel.utils.ImageUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity implements DialogUtil.DialogChoiceSelectImageCallback,
        UserDatabase.CreateUserListener, UserDatabase.UploadImageListener {

    private String mCurrentPhotoPath;
    private File folder;

    private static int REQUEST_TAKE_PHOTO = 1234;
    private static int REQUEST_PICK_IMAGE = 1235;

    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private CircleImageView imageProfile;
    private String pathFile;
    private UserModel userModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, R.string.txt_register);
        setupInstance();
        setupView();
    }

    private void setupInstance() {
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES +
                "/" + getString(R.string.app_name));

    }

    private void setupView() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imageProfile = findViewById(R.id.imgProfile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionImage();
            }
        });
        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                if (username.isEmpty()) {
                    DialogUtil.showDialogError(RegisterActivity.this,
                            getString(R.string.txt_error),
                            getString(R.string.txt_username_incorrect));
                } else if (email.isEmpty()) {
                    DialogUtil.showDialogError(RegisterActivity.this,
                            getString(R.string.txt_error),
                            getString(R.string.txt_email_incorrect));
                }else if (password.length() < 8) {
                    DialogUtil.showDialogError(RegisterActivity.this,
                            "ผิดพลาด",
                            "รหัสผ่านต้อง 8 ตัวขึ้นไป");
                } else if (!password.equals(confirmPassword)) {
                    DialogUtil.showDialogError(RegisterActivity.this,
                            getString(R.string.txt_error),
                            getString(R.string.txt_password_not_match));
                } else if (pathFile == null) {
                    DialogUtil.showDialogError(RegisterActivity.this,
                            getString(R.string.txt_error),
                            getString(R.string.txt_image_profile_require));
                } else {
                    DialogUtil.showDialog(RegisterActivity.this);
                    UserDatabase.uploadImage(pathFile, RegisterActivity.this);
                }
            }
        });
    }

    private void checkPermissionImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (!hasDeniedPermission(report)) {
                            DialogUtil.showDialogChoiceImage(RegisterActivity.this, RegisterActivity.this);
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.txt_permission_denied, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                    private boolean hasDeniedPermission(MultiplePermissionsReport report) {
                        List<PermissionDeniedResponse> denyPermission = report.getDeniedPermissionResponses();
                        return denyPermission != null && denyPermission.size() > 0;
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
            DialogUtil.showDialog(this);
            addImageToGallery();
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {
            if (data != null) {
                DialogUtil.showDialog(this);
                try {
                    compressorImage(new File(ImageUtil.getImagePath(this, data.getData())));
                } catch (Exception e) {
                    DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
                }
            }
        }
    }

    private void dispatchChooseImageGallery() {
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*"),
                getString(R.string.choose_from_gallery)), REQUEST_PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        File file = createImageFile();
        if (file != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.resolveActivity(getPackageManager());
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() {
        if (!folder.isDirectory()) folder.mkdir();
        try {
            File imageFile = File.createTempFile(ImageUtil.getFileName(), ".jpg", folder);
            mCurrentPhotoPath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addImageToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        compressorImage(file);
    }

    @SuppressLint("CheckResult")
    private void compressorImage(File file) {
        new Compressor(this)
                .setMaxHeight(816)
                .setMaxWidth(612)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        DialogUtil.dismissDialog();
                        pathFile = file.getPath();
                        Glide.with(RegisterActivity.this).load(pathFile).into(imageProfile);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        DialogUtil.dismissDialog();
                        DialogUtil.showDialogError(RegisterActivity.this, getString(R.string.txt_error), throwable.getMessage());
                    }
                });
    }


    @Override
    public void onTakePhoto() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onPickGallery() {
        dispatchChooseImageGallery();
    }

    @Override
    public void onCreateUserSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(RegisterActivity.this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCreateUserFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(RegisterActivity.this,
                getString(R.string.txt_error),
                getString(R.string.txt_please_try_again));
    }

    @Override
    public void onUploadImageSuccess(final String fileName) {
        final String username = etUsername.getText().toString();
        final String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                userModel = new UserModel();
                                userModel.setEmail(email);
                                userModel.setUsername(username);
                                userModel.setUserId(user.getUid());
                                userModel.setTimestamp(DateTimeUtil.getTimeUTC());
                                userModel.setImageUrl(fileName);
                                UserDatabase.createUser(userModel, RegisterActivity.this, RegisterActivity.this);
                            } else {
                                DialogUtil.dismissDialog();
                                DialogUtil.showDialogError(RegisterActivity.this,
                                        getString(R.string.txt_error),
                                        getString(R.string.txt_please_try_again));
                            }
                        } else {
                            DialogUtil.dismissDialog();
                            if (task.getException() != null) {
                                DialogUtil.showDialogError(RegisterActivity.this,
                                        getString(R.string.txt_error),
                                        task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    @Override
    public void onUploadImageFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(RegisterActivity.this,
                getString(R.string.txt_error),
                getString(R.string.txt_please_try_again));
    }
}
