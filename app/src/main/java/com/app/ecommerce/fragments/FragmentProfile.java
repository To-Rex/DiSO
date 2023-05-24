package com.app.ecommerce.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.activities.ActivityHistory;
import com.app.ecommerce.activities.ActivitySettings;
import com.app.ecommerce.utilities.SharedPref;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.Objects;

public class FragmentProfile extends Fragment {

    private SharedPref sharedPref;
    TextView txt_user_name;
    TextView txt_user_email;
    TextView txt_user_phone;
    TextView txt_user_address;
    MaterialRippleLayout btn_edit_user;
    MaterialRippleLayout btn_order_history, btn_rate, btn_share, btn_privacy;
    LinearLayout lyt_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPref = new SharedPref(getActivity());

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        txt_user_name = view.findViewById(R.id.txt_user_name);
        txt_user_email = view.findViewById(R.id.txt_user_email);
        txt_user_phone = view.findViewById(R.id.txt_user_phone);
        txt_user_address = view.findViewById(R.id.txt_user_address);

        btn_edit_user = view.findViewById(R.id.btn_edit_user);
        btn_edit_user.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivitySettings.class);
            startActivity(intent);
        });

        btn_order_history = view.findViewById(R.id.btn_order_history);
        btn_order_history.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityHistory.class);
            startActivity(intent);
        });

        btn_rate = view.findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(v -> {
            final String appName = Objects.requireNonNull(getActivity()).getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        });

        btn_share = view.findViewById(R.id.btn_share);
        btn_share.setOnClickListener(v -> {
            String share_text = Html.fromHtml(getResources().getString(R.string.share_app)).toString();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, share_text + "\n\n" + "https://play.google.com/store/apps/details?id=" + Objects.requireNonNull(getActivity()).getPackageName());
            intent.setType("text/plain");
            startActivity(intent);
        });

        btn_privacy = view.findViewById(R.id.btn_privacy);
        btn_privacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))));

        return view;
    }

    @Override
    public void onResume() {
        txt_user_name.setText(sharedPref.getYourName());
        txt_user_email.setText(sharedPref.getYourEmail());
        txt_user_phone.setText(sharedPref.getYourPhone());
        txt_user_address.setText(sharedPref.getYourAddress());
        super.onResume();
    }
}
