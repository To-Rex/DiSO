package com.app.ecommerce.fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.ecommerce.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentAdd extends Fragment{

    private EditText add_productAdd;
    private EditText add_priceAdd;
    private EditText add_descriptionAdd;
    private EditText add_quantityAdd;
    private ImageView add_imageAdd;
    private ImageView add_image;
    private Button add_productBtn;
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
            //post url, body from-data [{"key":"product_id","value":"3","type":"text","enabled":false},{"key":"product_name","value":"bitta O`zim","type":"text","enabled":true,"description":""},{"key":"product_price","value":"38700","type":"text","enabled":true},{"key":"product_status","value":"Available","type":"text","enabled":true},{"key":"product_image","type":"file","enabled":true,"value":["/Users/yorvoration/Documents/Documents - Dilshodjon’s MacBook Pro/2023-01-08 16.28.34.jpg"],"warning":"This file isn't in your working directory. Teammates you share this request with won't be able to use this file. To make collaboration easier you can setup your working directory in Settings."},{"key":"product_description","value":"ajoyib","type":"text","enabled":true},{"key":"product_quantity","value":"3","type":"text","enabled":true},{"key":"category_id","value":"4","type":"text","enabled":true}]
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
                    params.put("category_id", "4");
                    return params;
                }
            };
            Volley.newRequestQueue(requireActivity()).add(stringRequest);


        });

        return view;
    }
}