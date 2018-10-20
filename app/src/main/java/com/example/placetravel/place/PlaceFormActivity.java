package com.example.placetravel.place;

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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.placetravel.BaseActivity;
import com.example.placetravel.BuildConfig;
import com.example.placetravel.R;
import com.example.placetravel.database.PlaceDatabase;
import com.example.placetravel.model.PlaceModel;
import com.example.placetravel.utils.DateTimeUtil;
import com.example.placetravel.utils.DialogUtil;
import com.example.placetravel.utils.ImageUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceFormActivity extends BaseActivity implements DialogUtil.DialogChoiceSelectImageCallback, PlaceDatabase.EditPlaceListener, PlaceDatabase.CreatePlaceListener {

    private TextInputEditText etName, etDes, etLinkFacebook, etPhoneNumber;
    private AppCompatImageView imgPlace1, imgPlace2, imgPlace3;
    private View icAdd1, icAdd2, icAdd3;
    private AppCompatImageView imgMap;
    private List<String> gallery = new ArrayList<>();
    private List<String> pathImages = new ArrayList<>();
    private Double lat, lng;
    private Spinner spinner;

    private String mCurrentPhotoPath;
    private File folder;

    private static int REQUEST_TAKE_PHOTO = 1234;
    private static int REQUEST_PICK_IMAGE = 1235;

    private int indexAddImage = 0;
    private PlaceModel placeModel;
    private List<String> pathImagesUpdate = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar, isCreate() ? R.string.txt_create_place : R.string.txt_edit_place);
        setupInstance();
        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_form) {
            String placeName = etName.getText().toString();
            String placeDes = etDes.getText().toString();
            String placeFacebook = etLinkFacebook.getText().toString();
            String placePhoneNumber = etPhoneNumber.getText().toString();

            if (isCreate()) {
                if (gallery.get(0).isEmpty()) {
                    DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_image_place));
                }
                else if (gallery.get(1).isEmpty()) {
                    DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_image_place));
                } else if (gallery.get(2).isEmpty()) {
                    DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_image_place));
                }
            }

            if (placeName.isEmpty()) {
                DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_place_name));
            } else if (placeDes.isEmpty()) {
                DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_place_des));
            } else if (placeFacebook.isEmpty()) {
                DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_place_link_facebook));
            } else if (placePhoneNumber.isEmpty()) {
                DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_place_phone_number));
            } else if (lat == null || lng == null) {
                DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_require_place_location));
            } else {
                placeModel = new PlaceModel();
                placeModel.setUserId(FirebaseAuth.getInstance().getUid());
                placeModel.setCategory(spinner.getSelectedItem().toString());
                placeModel.setDescription(placeDes);
                placeModel.setName(placeName);
                placeModel.setPhoneNumber(placePhoneNumber);
                placeModel.setLat(lat);
                placeModel.setLng(lng);
                placeModel.setLinkFacebook(placeFacebook);
                saveData();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        if (isCreate()) {
            DialogUtil.showDialog(this);
            uploadImage();
        } else {
            if (gallery.get(0).isEmpty() && gallery.get(1).isEmpty() && gallery.get(2).isEmpty()) {
                placeModel.setGalleries(pathImages);
                DialogUtil.showDialog(this);
                updatePlaceDetail();
            } else {
                DialogUtil.showDialog(this);
                uploadImageForUpdate();
            }
        }
    }

    private void updatePlaceDetail() {
        if (getPlaceModel() != null)
            placeModel.setCreateTimestamp(getPlaceModel().getCreateTimestamp());
        placeModel.setUpdateTimestamp(DateTimeUtil.getTimeUTC());
        String placeId = "0";
        if (getPlaceModel() != null) placeId = getPlaceModel().getId();
        PlaceDatabase.updatePlace(placeId, placeModel, this);
    }

    private void setupInstance() {
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES +
                "/" + getString(R.string.app_name));
        gallery.add("");
        gallery.add("");
        gallery.add("");
    }

    private void setupView() {
        imgMap = findViewById(R.id.imgMap);
        spinner = findViewById(R.id.spCategory);
        etDes = findViewById(R.id.etDes);
        etName = findViewById(R.id.etProductName);
        etLinkFacebook = findViewById(R.id.etFacebookLink);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        imgPlace1 = findViewById(R.id.imgPlaceOne);
        imgPlace2 = findViewById(R.id.imgProductTwo);
        imgPlace3 = findViewById(R.id.imgPlaceThree);
        icAdd1 = findViewById(R.id.icAdd1);
        icAdd2 = findViewById(R.id.icAdd2);
        icAdd3 = findViewById(R.id.icAdd3);
        findViewById(R.id.btnAddImgPlaceOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexAddImage = 0;
                checkPermissionImage();
            }
        });
        findViewById(R.id.btnAddImgPlaceTwo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexAddImage = 1;
                checkPermissionImage();
            }
        });
        findViewById(R.id.btnAddImgPlaceThree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexAddImage = 2;
                checkPermissionImage();
            }
        });
        findViewById(R.id.btnLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PlaceFormActivity.this, MapActivity.class), 1005);
            }
        });
        if (!isCreate()) prefillData();
    }

    private void prefillData() {
        if (getPlaceModel() != null) {
            etName.setText(getPlaceModel().getName());
            etLinkFacebook.setText(getPlaceModel().getLinkFacebook());
            etDes.setText(getPlaceModel().getDescription());
            etPhoneNumber.setText(getPlaceModel().getPhoneNumber());
            lat = getPlaceModel().getLat();
            lng = getPlaceModel().getLng();
            pathImages = getPlaceModel().getGalleries();
            String[] listCategory = getResources().getStringArray(R.array.category);
            int indexCategory = 0;
            for (int i = 0; i < listCategory.length; i++) {
                if (listCategory[i].equals(getPlaceModel().getCategory())) {
                    indexCategory = i;
                }
            }
            spinner.setSelection(indexCategory);
            initImage();
            loadStaticMap();
        }
    }

    private void initImage() {
        if (!pathImages.get(0).isEmpty()) {
            icAdd1.setVisibility(View.INVISIBLE);
            ImageUtil.loadImageFirebase(pathImages.get(0), imgPlace1);
        }
        if (!pathImages.get(1).isEmpty()) {
            icAdd2.setVisibility(View.INVISIBLE);
            ImageUtil.loadImageFirebase(pathImages.get(1), imgPlace2);
        }
        if (!pathImages.get(2).isEmpty()) {
            icAdd3.setVisibility(View.INVISIBLE);
            ImageUtil.loadImageFirebase(pathImages.get(2), imgPlace3);
        }
    }

    private void loadStaticMap() {
        String url = "http://maps.google.com/maps/api/staticmap?center=" +
                lat + "," + lng + "&zoom=16&size=500x500&" +
                "sensor=false&" +
                "markers=color:red%7C" + lat + "," + lng;
        imgMap.setVisibility(View.VISIBLE);
        Glide.with(this).load(url).into(imgMap);
    }

    private void checkPermissionImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (!hasDeniedPermission(report)) {
                            DialogUtil.showDialogChoiceImage(PlaceFormActivity.this, PlaceFormActivity.this);
                        } else {
                            Toast.makeText(PlaceFormActivity.this, R.string.txt_permission_denied, Toast.LENGTH_SHORT).show();
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

    private boolean isCreate() {
        return getIntent().getBooleanExtra("is_create", false);
    }

    private PlaceModel getPlaceModel() {
        return getIntent().getParcelableExtra("data");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1005) {
            if (data != null) {
                lat = data.getDoubleExtra("lat", 0.0);
                lng = data.getDoubleExtra("lng", 0.0);
                loadStaticMap();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO) {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                        String pathFile = file.getPath();
                        gallery.set(indexAddImage, pathFile);
                        loadImage();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        DialogUtil.dismissDialog();
                        DialogUtil.showDialogError(PlaceFormActivity.this, getString(R.string.txt_error), throwable.getMessage());
                    }
                });
    }

    private void uploadImageForUpdate() {
        int index = 0;
        if (!pathImagesUpdate.isEmpty()) index = pathImagesUpdate.size();
        if (pathImagesUpdate.size() == 3) {
            placeModel.setGalleries(pathImages);
            updatePlaceDetail();
        } else {
            if (!gallery.get(index).isEmpty()) {
                Uri file = Uri.fromFile(new File(gallery.get(index)));
                StorageReference ref = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef = ref.child("place/" + file.getLastPathSegment());
                final UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        DialogUtil.dismissDialog();
                        DialogUtil.showDialogError(PlaceFormActivity.this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            pathImagesUpdate.add(taskSnapshot.getMetadata().getName());
                            pathImages.set(pathImagesUpdate.size() - 1, taskSnapshot.getMetadata().getName());
                            uploadImageForUpdate();
                        }
                    }
                });
            } else {
                pathImagesUpdate.add("");
                uploadImageForUpdate();
            }
        }
    }

    private void uploadImage() {
        int index = 0;
        if (!pathImages.isEmpty()) index = pathImages.size();
        if (pathImages.size() == 3) {
            placeModel.setGalleries(pathImages);
            placeModel.setCreateTimestamp(DateTimeUtil.getTimeUTC());
            PlaceDatabase.createPlace(placeModel, this);
        } else {
            Uri file = Uri.fromFile(new File(gallery.get(index)));
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = ref.child("place/" + file.getLastPathSegment());
            final UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    DialogUtil.dismissDialog();
                    DialogUtil.showDialogError(PlaceFormActivity.this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        pathImages.add(taskSnapshot.getMetadata().getName());
                        uploadImage();
                    }
                }
            });
        }
    }

    private void loadImage() {
        if (!gallery.get(0).isEmpty()) {
            icAdd1.setVisibility(View.INVISIBLE);
            Glide.with(this).load(gallery.get(0)).into(imgPlace1);
        }
        if (!gallery.get(1).isEmpty()) {
            icAdd2.setVisibility(View.INVISIBLE);
            Glide.with(this).load(gallery.get(1)).into(imgPlace2);
        }
        if (!gallery.get(2).isEmpty()) {
            icAdd3.setVisibility(View.INVISIBLE);
            Glide.with(this).load(gallery.get(2)).into(imgPlace3);
        }
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
    public void onEditPlaceSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, new Intent().putExtra("new_data", placeModel));
        finish();
    }

    @Override
    public void onEditPlaceFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
    }

    @Override
    public void onCreatePlaceSuccess() {
        DialogUtil.dismissDialog();
        Toast.makeText(this, R.string.txt_successfully, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCreatePlaceFailure() {
        DialogUtil.dismissDialog();
        DialogUtil.showDialogError(this, getString(R.string.txt_error), getString(R.string.txt_please_try_again));
    }
}
