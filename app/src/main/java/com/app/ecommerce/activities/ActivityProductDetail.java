package com.app.ecommerce.activities;

import static com.app.ecommerce.utilities.Constant.GET_TAX_CURRENCY;
import static com.app.ecommerce.utilities.Utils.PERMISSIONS_REQUEST;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.utilities.DBHelper;
import com.app.ecommerce.utilities.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class ActivityProductDetail extends AppCompatActivity {

    long product_id;
    TextView txt_product_name, txt_product_price, txt_product_quantity;
    private String product_name, product_image, category_name, product_status, currency_code, product_description;
    private double product_price;
    private int product_quantity;
    WebView txt_product_description;
    ImageView img_product_image;
    Button btn_cart;
    public static DBHelper dbhelper;
    final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    double resp_tax;
    String resp_currency_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Utils.lightNavigation(this);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        dbhelper = new DBHelper(this);
        getData();
        initComponent();
        displayData();
        setupToolbar();
        makeJsonObjectRequest();
    }

    public void setupToolbar() {

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(category_name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    public void getData() {
        Intent intent = getIntent();
        product_id = intent.getLongExtra("product_id", 0);
        product_name = intent.getStringExtra("title");
        product_image = intent.getStringExtra("image");
        product_price = intent.getDoubleExtra("product_price", 0);
        product_description = intent.getStringExtra("product_description");
        product_quantity = intent.getIntExtra("product_quantity", 0);
        product_status = intent.getStringExtra("product_status");
        currency_code = intent.getStringExtra("currency_code");
        category_name = intent.getStringExtra("category_name");
    }

    public void initComponent() {
        txt_product_name = findViewById(R.id.product_name);
        img_product_image = findViewById(R.id.product_image);
        txt_product_price = findViewById(R.id.product_price);
        txt_product_description = findViewById(R.id.product_description);
        txt_product_quantity = findViewById(R.id.product_quantity);
        btn_cart = findViewById(R.id.btn_add_cart);
    }

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    public void displayData() {
        txt_product_name.setText(product_name);

        Picasso.with(this)
                .load(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image.replace(" ", "%20"))
                .placeholder(R.drawable.ic_loading)
                .into(img_product_image);

        img_product_image.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ActivityImageDetail.class);
            intent.putExtra("image", product_image);
            startActivity(intent);
        });

        if (Config.ENABLE_DECIMAL_ROUNDING) {
            String price = String.format(Locale.GERMAN, "%1$,.0f", product_price);
            txt_product_price.setText(price + " " + currency_code);
        } else {
            txt_product_price.setText(product_price + " " + currency_code);
        }

        txt_product_quantity.setText(product_quantity + " " + getString(R.string.txt_items));

        if (product_status.equals("Available")) {
            btn_cart.setText(R.string.btn_add_to_cart);
            btn_cart.setBackgroundResource(R.color.available);
            btn_cart.setOnClickListener(v -> inputDialog());
        } else {
            btn_cart.setEnabled(false);
            btn_cart.setText(R.string.btn_out_of_stock);
            btn_cart.setBackgroundResource(R.color.sold);
        }

        txt_product_description.setBackgroundColor(Color.parseColor("#ffffff"));
        txt_product_description.setFocusableInTouchMode(false);
        txt_product_description.setFocusable(false);
        txt_product_description.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = txt_product_description.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = product_description;

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
            txt_product_description.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            txt_product_description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        }

    }

    public void inputDialog() {

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText edtQuantity = mView.findViewById(R.id.userInputDialog);
        alert.setCancelable(false);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton(R.string.dialog_option_add, (dialog, whichButton) -> {
            String temp = edtQuantity.getText().toString();
            int quantity = 0;

            if (!temp.equalsIgnoreCase("")) {

                quantity = Integer.parseInt(temp);

                if (quantity <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.msg_stock_below_0, Toast.LENGTH_SHORT).show();
                } else if (quantity > product_quantity) {
                    Toast.makeText(getApplicationContext(), R.string.msg_stock_not_enough, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.msg_success_add_cart, Toast.LENGTH_SHORT).show();

                    if (dbhelper.isDataExist(product_id)) {
                        dbhelper.updateData(product_id, quantity, (product_price * quantity));
                    } else {
                        dbhelper.addData(product_id, product_name, quantity, (product_price * quantity), currency_code, product_image);
                    }
                }

            } else {
                dialog.cancel();
            }
        });

        alert.setNegativeButton(R.string.dialog_option_cancel, (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    private void makeJsonObjectRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, GET_TAX_CURRENCY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("INFO", response.toString());
                try {
                    resp_tax = response.getDouble("tax");
                    resp_currency_code = response.getString("currency_code");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, error -> {
            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.cart:
                Intent intent = new Intent(getApplicationContext(), ActivityCart.class);
                intent.putExtra("tax", resp_tax);
                intent.putExtra("currency_code", resp_currency_code);
                startActivity(intent);
                break;
            case R.id.share:
                requestStoragePermission();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(ActivityProductDetail.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSIONS_REQUEST);
            } else {
                (new ShareTask(ActivityProductDetail.this)).execute(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image);
            }
        } else {
            (new ShareTask(ActivityProductDetail.this)).execute(Config.ADMIN_PANEL_URL + "/upload/product/" + product_image);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    public class ShareTask extends AsyncTask<String, String, String> {
        private final Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        public ShareTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getString(R.string.loading_msg));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.PNG, 99, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            if (Config.ENABLE_DECIMAL_ROUNDING) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_section_one) + " " + product_name + " " + getString(R.string.share_product_section_two) + " " + String.format(Locale.GERMAN, "%1$,.0f", product_price) + " " + currency_code + getString(R.string.share_product_section_three) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Image"));
                pDialog.dismiss();
            } else {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_product_section_one) + " " + product_name + " " + getString(R.string.share_product_section_two) + " " + product_price + " " + currency_code + getString(R.string.share_product_section_three) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(intent, "Share Image"));
                pDialog.dismiss();
            }
        }
    }

}
