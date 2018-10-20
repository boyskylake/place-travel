package com.example.placetravel.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.placetravel.R;

public class DialogUtil {

    public interface DialogLogoutCallback {
        void onUserLogout();
    }

    public interface DialogConfirmShopOwnerCallback {
        void onUserConfirmShopOwner();
    }

    public interface DialogConfirmDeleteShopOwnerCallback {
        void onUserDeleteShopOwner();
    }

    public interface DialogChoiceSelectImageCallback {
        void onTakePhoto();

        void onPickGallery();
    }

    public interface DialogDeleteShopCallback {
        void onDeleteShop();
    }

    public interface DialogDeleteNewsCallback {
        void onDeleteNews();
    }

    public interface DialogDeleteCommentCallback{
        void onDeleteComment();
    }

    private static ProgressDialog progressDialog;

    public static void showDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(activity.getString(R.string.txt_loading));
        progressDialog.show();
    }

    public static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void showDialogError(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getText(R.string.txt_ok), null)
                .create();
        alertDialog.show();
    }

    public static void showDialogLogout(Context context, final DialogLogoutCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.txt_logout))
                .setMessage(context.getString(R.string.txt_des_logout))
                .setPositiveButton(context.getText(R.string.txt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onUserLogout();
                    }
                })
                .setNegativeButton(context.getText(R.string.txt_cancel), null)
                .create();
        alertDialog.show();
    }

    public static void showDialogChoiceImage(Context context, final DialogChoiceSelectImageCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.txt_select_image)
                .setItems(R.array.choice_select_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            callback.onTakePhoto();
                        } else {
                            callback.onPickGallery();
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    public static void showDialogDeletePlace(Context context, final DialogDeleteShopCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.txt_delete_place))
                .setMessage(context.getString(R.string.txt_des_delete_place))
                .setPositiveButton(context.getText(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onDeleteShop();
                    }
                })
                .setNegativeButton(context.getText(R.string.txt_no), null)
                .create();
        alertDialog.show();
    }

    public static void showDialogDeleteNews(Context context, final DialogDeleteNewsCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.txt_delete_news))
                .setMessage(context.getString(R.string.txt_des_delete_news))
                .setPositiveButton(context.getText(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onDeleteNews();
                    }
                })
                .setNegativeButton(context.getText(R.string.txt_no), null)
                .create();
        alertDialog.show();
    }

    public static void showDialogDeleteComment(Context context, final DialogDeleteCommentCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.txt_delete_comment))
                .setMessage(context.getString(R.string.txt_des_delete_comment))
                .setPositiveButton(context.getText(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onDeleteComment();
                    }
                })
                .setNegativeButton(context.getText(R.string.txt_no), null)
                .create();
        alertDialog.show();
    }
}