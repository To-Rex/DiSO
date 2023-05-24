package com.app.ecommerce.fragments;

import static com.app.ecommerce.utilities.Constant.GET_HELP;

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
import com.app.ecommerce.activities.ActivityHelp;
import com.app.ecommerce.activities.MyApplication;
import com.app.ecommerce.adapters.AdapterHelp;
import com.app.ecommerce.models.Help;
import com.app.ecommerce.utilities.MyDividerItemDecoration;
import com.app.ecommerce.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentHelp extends Fragment implements AdapterHelp.ContactsAdapterListener {

    private List<Help> helpList;
    private AdapterHelp mAdapter;
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
        helpList = new ArrayList<>();
        mAdapter = new AdapterHelp(getActivity(), helpList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        fetchContacts();
        onRefresh();

        return view;
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            helpList.clear();
            new Handler().postDelayed(() -> {
                if (Utils.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
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
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(GET_HELP, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                return;
            }

            List<Help> items = new Gson().fromJson(response.toString(), new TypeToken<List<Help>>() {
            }.getType());

            // adding contacts to contacts list
            helpList.clear();
            helpList.addAll(items);

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

        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
    public void onContactSelected(Help help) {
        Intent intent = new Intent(getActivity(), ActivityHelp.class);
        intent.putExtra("title", help.getTitle());
        intent.putExtra("content", help.getContent());
        startActivity(intent);
    }

}