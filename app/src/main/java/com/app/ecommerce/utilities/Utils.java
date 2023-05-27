package com.app.ecommerce.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.app.ecommerce.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@SuppressWarnings("deprecation")
public class Utils {

    public static final int PERMISSIONS_REQUEST = 102;
    Context context;

    // constructor
    public Utils(Context context) {
        this.context = context;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void lightNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.white));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    public static String getFormatedDateSimple(String date_str) {
        if (date_str != null && !date_str.trim().equals("")) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                String newStr = newFormat.format(oldFormat.parse(date_str));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFormatedDate(String date_str) {
        if (date_str != null && !date_str.trim().equals("")) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss");
            try {
                String newStr = newFormat.format(oldFormat.parse(date_str));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void downloadImage(Activity activity, String filename, String downloadUrlOfImage, String mimeType) {
        try {
            DownloadManager dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType(mimeType) // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            //Toast.makeText(activity, "Image download started.", Toast.LENGTH_SHORT).show();
            Snackbar.make(activity.findViewById(android.R.id.content), "Image download started.", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            //Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
            Snackbar.make(activity.findViewById(android.R.id.content), "Image download failed.", Snackbar.LENGTH_SHORT).show();
        }
    }

}
