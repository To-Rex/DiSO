package com.app.ecommerce.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.app.ecommerce.Config;
import com.app.ecommerce.R;
import com.app.ecommerce.activities.ActivityHistory;
import com.app.ecommerce.activities.ActivitySettings;
import com.app.ecommerce.utilities.SharedPref;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.Objects;

public class FragmentProfile extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedPref sharedPref;
    TextView txt_user_name;
    TextView txt_user_email;
    TextView txt_user_phone;
    TextView txt_user_address;
    TextView btn_edit_profile;
    MaterialRippleLayout btn_edit_user;
    MaterialRippleLayout btn_language;
    CardView btn_order_history, btn_rate, btn_share, btn_privacy;
    LinearLayout lyt_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        sharedPref = new SharedPref(getActivity());

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        txt_user_name = view.findViewById(R.id.txt_user_name);
        txt_user_email = view.findViewById(R.id.txt_user_email);
        txt_user_phone = view.findViewById(R.id.txt_user_phone);
        txt_user_address = view.findViewById(R.id.txt_user_address);
        btn_edit_profile = view.findViewById(R.id.btn_edit_profile);

        btn_edit_user = view.findViewById(R.id.btn_edit_user);
        btn_language = view.findViewById(R.id.btn_language);

        getLanguages();

        btn_language.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getActivity(), btn_language);
            popup.getMenuInflater().inflate(R.menu.menu_language, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_english) {
                    sharedPreferences.edit().putString("language", "eng").apply();
                    getData();
                } else if (item.getItemId() == R.id.menu_russian) {
                    sharedPreferences.edit().putString("language", "ru").apply();
                    getData();
                } else if (item.getItemId() == R.id.menu_uzbek) {
                    sharedPreferences.edit().putString("language", "uz").apply();
                    getData();
                } else if (item.getItemId() == R.id.menu_uzbek_cyrillic) {
                    sharedPreferences.edit().putString("language", "au").apply();
                    getData();
                }
                requireActivity().finish();
                startActivity(requireActivity().getIntent());
                getLanguages();
                return true;
            });
            popup.show();
        });

        btn_language.setOnLongClickListener(v -> {
            sharedPref.clearAllData();
            return true;
        });

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
            final String appName = requireActivity().getPackageName();
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
            intent.putExtra(Intent.EXTRA_TEXT, share_text + "\n\n" + "https://play.google.com/store/apps/details?id=" + requireActivity().getPackageName());
            intent.setType("text/plain");
            startActivity(intent);
        });
        btn_privacy = view.findViewById(R.id.btn_privacy);
        btn_privacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))));
        return view;
    }
    public void getLanguages() {
        String til = getLanguage();
        if (Objects.equals(til,"uz")) {
            btn_edit_profile.setText(getString(R.string.o_zbekcha_latin));
            getData();
        }
        if (Objects.equals(til,"ru")) {
            btn_edit_profile.setText(getString(R.string.russian));
            getData();
            return;
        }
        if (Objects.equals(til,"eng")) {
            btn_edit_profile.setText(getString(R.string.english));
            getData();
            return;
        }
        if (Objects.equals(til,"au")) {
            btn_edit_profile.setText(getString(R.string.uzbek_krill));
            getData();
        }
    }

    public String getLanguage() {
        return sharedPreferences.getString("language", "eng");
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        txt_user_name.setText(sharedPref.getYourName());
        txt_user_email.setText(sharedPref.getYourEmail());
        txt_user_phone.setText(sharedPref.getYourPhone());
        txt_user_address.setText(sharedPref.getYourAddress());
    }
}
