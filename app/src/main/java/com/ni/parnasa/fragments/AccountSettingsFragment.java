package com.ni.parnasa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.ChangePasswordActivity;
import com.ni.parnasa.activities.DefaultNavSystemActivity;
import com.ni.parnasa.activities.LanguageSelectActivity;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout relChangePwd, relDefNavSys, relChangeLanguage;
    private View hideView;

    public AccountSettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getActivity()).changeLanguage();
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        relChangePwd = view.findViewById(R.id.relChangePwd);
        relDefNavSys = view.findViewById(R.id.relDefNavSys);
        relChangeLanguage = view.findViewById(R.id.relChangeLanguage);
        hideView = view.findViewById(R.id.hideView);

        relChangePwd.setOnClickListener(this);
        relDefNavSys.setOnClickListener(this);
        relChangeLanguage.setOnClickListener(this);

        String role = "";
        if (getActivity() instanceof ProfessionalHomeActivity) {
            role = ((ProfessionalHomeActivity) getActivity()).prefsUtil.getRole();
        } else {
            role = ((CustomerHomeActivity) getActivity()).prefsUtil.getRole();
        }

        if (!role.equals("")) {
            if (role.equalsIgnoreCase("Customer")) {
                relDefNavSys.setVisibility(View.GONE);
                hideView.setVisibility(View.GONE);
            }
        }

        setupAdMob(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == relChangePwd) {
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        } else if (view == relDefNavSys) {
            startActivity(new Intent(getActivity(), DefaultNavSystemActivity.class));
        } else if (view == relChangeLanguage) {
            startActivity(new Intent(getActivity(), LanguageSelectActivity.class));
        }
    }

    private FacebookAdLoader facebookAdLoader;

    private void setupAdMob(View view) {

        /** Load Google Ad */
        LinearLayout linlayConteiner = view.findViewById(R.id.adMobView);

        new GoogleAdLoader(getActivity(), getString(R.string.admob_settings_ad_id),linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });

        /** Load facebook Ad */
        facebookAdLoader = new FacebookAdLoader(getActivity(), linlayConteiner, new FacebookAdLoader.CustomAdListenerFacebook() {
            @Override
            public void onError(Ad aad, AdError adError) {
                Log.e("facebookAd", "onError CODE :" + adError.getErrorCode() + " Message : " + adError.getErrorMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        if (facebookAdLoader != null) {
            facebookAdLoader.destroyAdView();
        }
        super.onDestroy();
    }
}
