package com.app.ecommerce.activities;

import static com.app.ecommerce.utilities.Constant.GET_TAX_CURRENCY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.fragments.FragmentAdd;
import com.app.ecommerce.fragments.FragmentCategory;
import com.app.ecommerce.fragments.FragmentHelp;
import com.app.ecommerce.fragments.FragmentProfile;
import com.app.ecommerce.fragments.FragmentRecent;
import com.app.ecommerce.utilities.AppBarLayoutBehavior;
import com.app.ecommerce.utilities.DBHelper;
import com.app.ecommerce.utilities.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private BottomNavigationView navigation;
    public ViewPager viewPager;
    private Toolbar toolbar;
    MenuItem prevMenuItem;
    int pager_number = 5;
    DBHelper dbhelper;
    private long exitTime = 0;
    View view;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = new Locale("eng");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        view = findViewById(android.R.id.content);
        Utils.lightNavigation(this);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        AppBarLayout appBarLayout = findViewById(R.id.tab_appbar_layout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(pager_number);

        //update locale language for fragment


        navigation = findViewById(R.id.navigation);
        navigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_recent:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.nav_category:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_add:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.nav_info:
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.nav_profile:
                    viewPager.setCurrentItem(4);
                    return true;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 1) {
                    appBarLayout.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.title_nav_category);
                } else if (viewPager.getCurrentItem() == 2) {
                    appBarLayout.setVisibility(View.GONE);
                    toolbar.setTitle(R.string.add_product);
                } else if (viewPager.getCurrentItem() == 3) {
                    appBarLayout.setVisibility(View.VISIBLE);
                } else if (viewPager.getCurrentItem() == 4) {
                    appBarLayout.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.title_nav_profile);
                } else {
                    appBarLayout.setVisibility(View.VISIBLE);
                    toolbar.setTitle(R.string.app_name);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dbhelper = new DBHelper(this);
        try {
            dbhelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        makeJsonObjectRequest();

        if (Config.ENABLE_RTL_MODE) {
            viewPager.setRotationY(180);
        }

        if (getIntent() != null) {
            notificationOpenHandler(getIntent());
        }

    }

    public void notificationOpenHandler(Intent getIntent) {
        long post_id = getIntent.getLongExtra("cat_id", 0);
        String link = getIntent.getStringExtra("external_link");
        if (post_id == 0) {
            if (link != null && !link.equals("")) {
                if (!link.contains("no_url")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                }
            }
        } else if (post_id > 0) {
            Intent intent;
            if (post_id == 1010101010) {
                intent = new Intent(getApplicationContext(), ActivityHistory.class);
            } else {
                intent = new Intent(getApplicationContext(), ActivityNotificationDetail.class);
                intent.putExtra("product_id", post_id);
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentRecent();
                case 1:
                    return new FragmentCategory();
                case 2:
                    return new FragmentAdd();
                case 3:
                    return new FragmentHelp();
                case 4:
                    return new FragmentProfile();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager_number;
        }

    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(getString(R.string.db_exist_alert));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.dialog_option_yes), (dialog, which) -> {
            dbhelper.deleteAllData();
            dbhelper.close();
        });

        builder.setNegativeButton(getString(R.string.dialog_option_no), (dialog, which) -> {
            dbhelper.close();
            dialog.cancel();
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem((0), true);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void makeJsonObjectRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, GET_TAX_CURRENCY, null, response -> {
            Log.d("INFO", response.toString());
            try {
                final Double tax = response.getDouble("tax");
                final String currency_code = response.getString("currency_code");

                ImageButton btn_cart = findViewById(R.id.btn_add_cart);
                btn_cart.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
                    intent.putExtra("tax", tax);
                    intent.putExtra("currency_code", currency_code);
                    startActivity(intent);
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

}
