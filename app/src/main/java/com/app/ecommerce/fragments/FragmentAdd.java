package com.app.ecommerce.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.app.ecommerce.utilities.SharedPref;
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
    private ImageView add_image;
    private TextView add_categoryAdd,add_used,add_new;
    List<Category> items;
    ArrayList<String> categoryList;

    String category_id = "",imageUrl = "",phoneNumber = "",is_used = "";

    Boolean isUsed = false;

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
        ImageView add_imageAdd = view.findViewById(R.id.add_imageAdd);
        add_image = view.findViewById(R.id.add_image);
        Button add_productBtn = view.findViewById(R.id.add_productBtn);
        add_categoryAdd = view.findViewById(R.id.add_productCategory);
        add_used = view.findViewById(R.id.add_used);
        add_new = view.findViewById(R.id.add_new);

        getCategory();
        categoryList = new ArrayList<>();

        //get shared preferences for phone number
        SharedPref sharedPref = new SharedPref(getActivity());
        phoneNumber = sharedPref.getYourPhone();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_SHORT).show();
            phoneNumber = "nomalum raqam";
        }

        isSetUsed();

        add_used.setOnClickListener(v -> {
            add_used.setBackgroundResource(R.drawable.all_radius3);
            add_used.setTextColor(getResources().getColor(R.color.white));
            add_new.setBackgroundResource(R.drawable.all_radius4);
            add_new.setTextColor(getResources().getColor(R.color.color_3));
            isUsed = false;
            isSetUsed();
        });

        add_new.setOnClickListener(v -> {
            add_new.setBackgroundResource(R.drawable.all_radius6);
            add_new.setTextColor(getResources().getColor(R.color.white));
            add_used.setBackgroundResource(R.drawable.all_radius5);
            add_used.setTextColor(getResources().getColor(R.color.color_3));
            isUsed = true;
            isSetUsed();
        });

        add_imageAdd.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
        });

        add_categoryAdd.setOnClickListener(v -> {
            if (items == null) {
                Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                getCategory();
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
            if (product.isEmpty()) {
                add_productAdd.setError("Enter product name");
                return;
            } else if (price.isEmpty()) {
                add_priceAdd.setError("Enter product price");
                return;
            } else if (quantity.isEmpty()) {
                add_quantityAdd.setError("Enter product quantity");
                return;
            } else if (category_id.isEmpty()) {
                add_categoryAdd.setError("Enter product Category");
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
                    //params.put("product_image", file);
                    params.put("product_description", description);
                    params.put("product_quantity", quantity);
                    params.put("category_id", category_id);
                    params.put("state", is_used);
                    params.put("user_phone", phoneNumber);
                    return params;
                }
            };
            Volley.newRequestQueue(requireActivity()).add(stringRequest);
        });
        return view;
    }

    private void isSetUsed() {
        if (isUsed){
            is_used = "used or new";
        }else {
            is_used = "used";
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageUrl = selectedImageUri.toString();
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