package com.app.ecommerce;

public class Config {

    //your admin panel url
    public static final String ADMIN_PANEL_URL = "https://ecommerce.wesmart.uz/";
    //OneSignal App Id
    public static final String ONESIGNAL_APP_ID = "1b1fb549-381d-4cc7-9494-77b72c2c85a5";
    //fcm notification topic should be written in lowercase without space, use underscore
    public static final String FCM_NOTIFICATION_TOPIC = "ecommerce_android_app_topic";

    //set false if you want price to be displayed in decimal
    public static final boolean ENABLE_DECIMAL_ROUNDING = true;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //splash screen duration in milliseconds
    public static final int SPLASH_TIME = 2000;

}