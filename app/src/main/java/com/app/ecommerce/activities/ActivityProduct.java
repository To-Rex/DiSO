package com.app.ecommerce.activities;

import static com.app.ecommerce.utilities.Constant.GET_CATEGORY_DETAIL;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.adapters.AdapterProduct;
import com.app.ecommerce.models.Product;
import com.app.ecommerce.utilities.ItemOffsetDecoration;
import com.app.ecommerce.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityProduct extends AppCompatActivity implements AdapterProduct.ContactsAdapterListener {

    private RecyclerView recyclerView;
    private List<Product> productList;
    private AdapterProduct mAdapter;
    private SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private String category_id, category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Utils.lightNavigation(this);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        category_id = intent.getStringExtra("category_id");
        category_name = intent.getStringExtra("category_name");

        // toolbar fancy stuff
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(category_name);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterProduct(this, productList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchData();
        onRefresh();

    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityProduct.this)) {
                            swipeRefreshLayout.setRefreshing(false);
                            fetchData();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_CATEGORY_DETAIL + category_id, new Response.Listener<JSONArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Product> items = new Gson().fromJson(response.toString(), new TypeToken<List<Product>>() {
                        }.getType());

                        // adding contacts to contacts list
                        productList.clear();
                        productList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, error -> {
                    // error in getting json
                    Log.e("INFO", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onContactSelected(Product product) {
        Intent intent = new Intent(getApplicationContext(), ActivityProductDetail.class);
        intent.putExtra("product_id", product.getProduct_id());
        intent.putExtra("title", product.getProduct_name());
        intent.putExtra("image", product.getProduct_image());
        intent.putExtra("product_price", product.getProduct_price());
        intent.putExtra("product_description", product.getProduct_description());
        intent.putExtra("product_quantity", product.getProduct_quantity());
        intent.putExtra("product_status", product.getProduct_status());
        intent.putExtra("currency_code", product.getCurrency_code());
        intent.putExtra("category_name", product.getCategory_name());
        startActivity(intent);
    }

}
