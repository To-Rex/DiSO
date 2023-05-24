package com.app.ecommerce.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.utilities.Utils;

public class ActivityHelp extends AppCompatActivity {

    String str_title, str_content;
    WebView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Utils.lightNavigation(this);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Intent intent = getIntent();
        str_title = intent.getStringExtra("title");
        str_content = intent.getStringExtra("content");

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(str_title);
        }

        content = findViewById(R.id.content);
        content.setBackgroundColor(Color.parseColor("#ffffff"));
        content.setFocusableInTouchMode(false);
        content.setFocusable(false);
        content.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = content.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = str_content;

        String fontStyleDefault = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom_font.ttf\")}body {overflow-wrap: break-word; word-wrap: break-word; -ms-word-break: break-all; word-break: break-all; word-break: break-word; -ms-hyphens: auto; -moz-hyphens: auto; -webkit-hyphens: auto; hyphens: auto; font-family: MyFont;font-size: medium;}</style>";

        String text = "<html><head>"
                + fontStyleDefault
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + fontStyleDefault
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        if (Config.ENABLE_RTL_MODE) {
            content.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            content.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
