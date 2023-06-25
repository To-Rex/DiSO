package com.app.ecommerce.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.app.ecommerce.R;

@SuppressWarnings("deprecation")
public class SharedPref {

    private final Context ctx;
    private final SharedPreferences default_prefence;

    public SharedPref(Context context) {
        this.ctx = context;
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    public void setYourName(String name) {
        default_prefence.edit().putString("Name", name).apply();
    }

    public String getYourName() {
        return default_prefence.getString("Name", str(R.string.default_your_name));
    }

    public void setYourEmail(String name) {
        default_prefence.edit().putString("Email", name).apply();
    }

    public String getYourEmail() {
        return default_prefence.getString("Email", str(R.string.default_your_email));
    }

    public void setYourPhone(String name) {
        default_prefence.edit().putString("Phone Number", name).apply();
    }

    public String getYourPhone() {
        return default_prefence.getString("Phone Number", str(R.string.default_your_phone));
    }

    public void setYourAddress(String name) {
        default_prefence.edit().putString("Address", name).apply();
    }

    public String getYourAddress() {
        return default_prefence.getString("Address", str(R.string.default_your_address));
    }

    public void setLanguage(String name) {
        default_prefence.edit().putString("Language", name).apply();
    }

    public String getLanguage() {
        return default_prefence.getString("Language", str(R.string.eng));
    }

    //clear all data
    public void clearAllData() {
        default_prefence.edit().clear().apply();
    }
}
