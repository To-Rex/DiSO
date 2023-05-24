package com.app.ecommerce.activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.ecommerce.Config;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    public static final String TAG = "MyApplication";
    private RequestQueue mRequestQueue;
    private static MyApplication mInstance;
    FirebaseAnalytics mFirebaseAnalytics;
    String message = "";
    String big_picture = "";
    String title = "";
    String link = "";
    String unique_id = "";
    long post_id = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        initNotification();
    }

    private void initNotification() {
        OneSignal.disablePush(false);
        Log.d(TAG, "OneSignal Notification is enabled");

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);

        FirebaseMessaging.getInstance().subscribeToTopic(Config.FCM_NOTIFICATION_TOPIC);
        OneSignal.setAppId(Config.ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(
                result -> {
                    title = result.getNotification().getTitle();
                    message = result.getNotification().getBody();
                    big_picture = result.getNotification().getBigPicture();
                    Log.d(TAG, title + ", " + message + ", " + big_picture);
                    try {
                        post_id = result.getNotification().getAdditionalData().getLong("cat_id");
                        unique_id = result.getNotification().getAdditionalData().getString("cat_name");
                        link = result.getNotification().getAdditionalData().getString("external_link");
                        Log.d(TAG, post_id + ", " + unique_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("cat_id", post_id);
                    intent.putExtra("cat_name", unique_id);
                    intent.putExtra("title", title);
                    intent.putExtra("external_link", link);
                    startActivity(intent);
                });

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}