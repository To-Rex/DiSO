package com.app.ecommerce.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.adapters.AdapterCart;
import com.app.ecommerce.models.Cart;
import com.app.ecommerce.utilities.DBHelper;
import com.app.ecommerce.utilities.MyDividerItemDecoration;
import com.app.ecommerce.utilities.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class ActivityCart extends AppCompatActivity {

    RecyclerView recyclerView;
    View lyt_empty_cart;
    RelativeLayout lyt_order;
    DBHelper dbhelper;
    AdapterCart adapterCart;
    double total_price;
    final int CLEAR_ALL_ORDER = 0;
    final int CLEAR_ONE_ORDER = 1;
    int FLAG;
    int ID;
    double str_tax;
    String str_currency_code;
    Button btn_checkout, btn_continue;
    ArrayList<ArrayList<Object>> data;
    public static ArrayList<Integer> product_id = new ArrayList<>();
    public static ArrayList<String> product_name = new ArrayList<>();
    public static ArrayList<Integer> product_quantity = new ArrayList<>();
    public static ArrayList<String> currency_code = new ArrayList<>();
    public static ArrayList<Double> sub_total_price = new ArrayList<>();
    public static ArrayList<String> product_image = new ArrayList<>();
    List<Cart> arrayCart;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Utils.lightNavigation(this);
        view = findViewById(android.R.id.content);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_cart);
        }

        Intent intent = getIntent();
        str_tax = intent.getDoubleExtra("tax", 0);
        str_currency_code = intent.getStringExtra("currency_code");

        recyclerView = findViewById(R.id.recycler_view);
        lyt_empty_cart = findViewById(R.id.lyt_empty_history);
        btn_checkout = findViewById(R.id.btn_checkout);
        btn_checkout.setOnClickListener(view -> {
            dbhelper.close();
            Intent intent1 = new Intent(ActivityCart.this, ActivityCheckout.class);
            intent1.putExtra("tax", str_tax);
            intent1.putExtra("currency_code", str_currency_code);
            startActivity(intent1);
        });
        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(v -> finish());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL, 86));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        lyt_order = findViewById(R.id.lyt_history);

        adapterCart = new AdapterCart(this, arrayCart);
        dbhelper = new DBHelper(this);

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(() -> showClearDialog(CLEAR_ONE_ORDER, product_id.get(position)), 400);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new getDataTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.clear:
                if (product_id.size() > 0) {
                    showClearDialog(CLEAR_ALL_ORDER, 1111);
                } else {
                    Snackbar.make(view, R.string.msg_empty_cart, Snackbar.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showClearDialog(int flag, int id) {
        FLAG = flag;
        ID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        switch (FLAG) {
            case 0:
                builder.setMessage(getString(R.string.clear_all_order));
                break;
            case 1:
                builder.setMessage(getString(R.string.clear_one_order));
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), (dialog, which) -> {
            switch (FLAG) {
                case 0:
                    dbhelper.deleteAllData();
                    clearData();
                    new getDataTask().execute();
                    break;
                case 1:
                    dbhelper.deleteData(ID);
                    clearData();
                    new getDataTask().execute();
                    break;
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void clearData() {
        product_id.clear();
        product_name.clear();
        product_quantity.clear();
        sub_total_price.clear();
        currency_code.clear();
        product_image.clear();
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class getDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            getDataFromDatabase();
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void result) {

            String _price = String.format(Locale.GERMAN, "%1$,.0f", total_price);
            String _tax = String.format(Locale.GERMAN, "%1$,.0f", str_tax);

            TextView txt_total_price = findViewById(R.id.txt_total_price);
            TextView txt_tax = findViewById(R.id.txt_tax);

            if (Config.ENABLE_DECIMAL_ROUNDING) {
                txt_total_price.setText(getResources().getString(R.string.txt_total) + " " + _price + " " + str_currency_code);
                txt_tax.setText(getResources().getString(R.string.txt_tax) + " " + _tax + " %");
            } else {
                txt_total_price.setText(getResources().getString(R.string.txt_total) + " " + total_price + " " + str_currency_code);
                txt_tax.setText(getResources().getString(R.string.txt_tax) + " " + str_tax + " %");
            }

            if (product_id.size() > 0) {
                lyt_order.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapterCart);
            } else {
                lyt_empty_cart.setVisibility(View.VISIBLE);
            }

        }
    }

    public void getDataFromDatabase() {

        total_price = 0;
        clearData();
        data = dbhelper.getAllData();

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            product_id.add(Integer.parseInt(row.get(0).toString()));
            product_name.add(row.get(1).toString());
            product_quantity.add(Integer.parseInt(row.get(2).toString()));
            sub_total_price.add(Double.parseDouble(row.get(3).toString()));

            total_price += sub_total_price.get(i);

            currency_code.add(row.get(4).toString());
            product_image.add(row.get(5).toString());
        }

        total_price += (total_price * (str_tax / 100));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private final GestureDetector gestureDetector;
        private final ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

}