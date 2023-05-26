package com.app.ecommerce.fragments;

import static com.app.ecommerce.utilities.Constant.GET_CATEGORY;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.JsonArrayRequest;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.activities.ActivityProduct;
import com.app.ecommerce.activities.MyApplication;
import com.app.ecommerce.adapters.AdapterCategory;
import com.app.ecommerce.models.Category;
import com.app.ecommerce.utilities.MyDividerItemDecoration;
import com.app.ecommerce.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FragmentCategory extends Fragment implements AdapterCategory.ContactsAdapterListener {

    private List<Category> categoryList;
    private AdapterCategory mAdapter;
    SwipeRefreshLayout swipeRefreshLayout = null;
    LinearLayout lyt_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        setHasOptionsMenu(true);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        categoryList = new ArrayList<>();
        mAdapter = new AdapterCategory(getActivity(), categoryList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //divider false leave room for footer view
        recyclerView.addItemDecoration(new MyDividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL, 0));

        recyclerView.setAdapter(mAdapter);



        fetchContacts();
        onRefresh();

        return view;
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            categoryList.clear();
            new Handler().postDelayed(() -> {
                if (Utils.isNetworkAvailable(requireActivity())) {
                    swipeRefreshLayout.setRefreshing(false);
                    fetchContacts();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }, 1500);
        });
    }

    private void fetchContacts() {
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(GET_CATEGORY, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                return;
            }

            List<Category> items = new Gson().fromJson(response.toString(), new TypeToken<List<Category>>() {
            }.getType());

            // adding contacts to contacts list
            categoryList.clear();
            categoryList.addAll(items);

            // refreshing recycler view
            mAdapter.notifyDataSetChanged();
        }, error -> {
            // error in getting json
            Log.e("INFO", "Error: " + error.getMessage());
            Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(requireActivity().getComponentName()));
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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onContactSelected(Category category) {
        //Toast.makeText(getActivity(), "Selected: " + category.getCategory_name(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), ActivityProduct.class);
        intent.putExtra("category_id", category.getCategory_id());
        intent.putExtra("category_name", category.getCategory_name());
        startActivity(intent);
    }

}