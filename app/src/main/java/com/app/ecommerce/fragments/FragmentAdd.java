package com.app.ecommerce.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ecommerce.R;
import com.app.ecommerce.models.Category;
import com.app.ecommerce.utilities.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentAdd extends Fragment {

    private EditText add_productAdd;
    private EditText add_priceAdd;
    private EditText add_descriptionAdd;
    private EditText add_quantityAdd;
    private ImageView add_imageAdd;
    private ImageView add_image;
    private Button add_productBtn;
    private TextView add_categoryAdd;
    List<Category> items;
    ArrayList<String> categoryList;
    String category_id = "";

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        setHasOptionsMenu(true);
        add_productAdd = view.findViewById(R.id.add_productAdd);
        add_priceAdd = view.findViewById(R.id.add_priceAdd);
        add_descriptionAdd = view.findViewById(R.id.add_descriptionAdd);
        add_quantityAdd = view.findViewById(R.id.add_quantityAdd);
        add_imageAdd = view.findViewById(R.id.add_imageAdd);
        add_image = view.findViewById(R.id.add_image);
        add_productBtn = view.findViewById(R.id.add_productBtn);
        add_categoryAdd = view.findViewById(R.id.add_productCategory);

        getCategory();
        categoryList = new ArrayList<>();

        add_imageAdd.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        });

        add_categoryAdd.setOnClickListener(v -> {
            if (items == null) {
                Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                return;
            }
            categoryList.clear();
            PopupMenu popupMenu = new PopupMenu(getActivity(), add_categoryAdd);
            for (int i = 0; i < items.size(); i++) {
                popupMenu.getMenu().add(0, i, i, items.get(i).getCategory_name());
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                add_categoryAdd.setText(item.getTitle());
                category_id = items.get(item.getItemId()).getCategory_id();
                return true;
            });
            popupMenu.show();
        });

        add_productBtn.setOnClickListener(v -> {
            String product = add_productAdd.getText().toString();
            String price = add_priceAdd.getText().toString();
            String description = add_descriptionAdd.getText().toString();
            String quantity = add_quantityAdd.getText().toString();
            if (product.isEmpty() || price.isEmpty() || description.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "https://ecommerce.wesmart.uz/api/api.php?post_product";
            StringRequest stringRequest = new StringRequest(1, url, response -> {
                Toast.makeText(getActivity(), "Product added successfully", Toast.LENGTH_SHORT).show();
                add_productAdd.setText("");
                add_priceAdd.setText("");
                add_descriptionAdd.setText("");
                add_quantityAdd.setText("");
            }, error -> Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("product_name", product);
                    params.put("product_price", price);
                    params.put("product_status", "Available");
                    params.put("product_description", description);
                    params.put("product_quantity", quantity);
                    params.put("category_id", category_id);
                    return params;
                }
            };
            Volley.newRequestQueue(requireActivity()).add(stringRequest);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            add_image.setImageURI(selectedImageUri);
        }
    }

    private void getCategory() {
        JsonArrayRequest request = new JsonArrayRequest(Constant.GET_CATEGORY, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), "Couldn't fetch the category! Pleas try again.", Toast.LENGTH_LONG).show();
            } else {
                items = new Gson().fromJson(response.toString(), new TypeToken<List<Category>>() {
                }.getType());
            }

        }, error -> Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(requireActivity()).add(request);

    }
}