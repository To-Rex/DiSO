package com.app.ecommerce.activities;

import static com.app.ecommerce.utilities.Utils.PERMISSIONS_REQUEST;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.utilities.TouchImageView;
import com.app.ecommerce.utilities.Utils;
import com.squareup.picasso.Picasso;

public class ActivityImageDetail extends AppCompatActivity {

    TouchImageView product_image;
    String str_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        Utils.lightNavigation(this);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        str_image = getIntent().getStringExtra("image");

        product_image = findViewById(R.id.image);

        Picasso.with(this)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + str_image.replace(" ", "%20"))
                .placeholder(R.drawable.ic_loading)
                .into(product_image);

        initToolbar();

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.close_image:
                new Handler(Looper.getMainLooper()).postDelayed(this::finish, 300);
                return true;

            case R.id.save_image:
                new Handler().postDelayed(this::requestStoragePermission, 300);
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(ActivityImageDetail.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSIONS_REQUEST);
            } else {
                downloadImage();
            }
        } else {
            downloadImage();
        }
    }

    public void downloadImage() {
        String image_name = str_image;
        String image_url = Config.ADMIN_PANEL_URL + "/upload/product/" + str_image;
        Utils.downloadImage(this, image_name, image_url, "image/jpeg");
    }

}
