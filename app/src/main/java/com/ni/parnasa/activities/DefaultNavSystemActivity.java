package com.ni.parnasa.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

public class DefaultNavSystemActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private CheckBox chkGoogleMap, chkWaze;
    private ImageView img_back;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_nav_system);

        mContext = DefaultNavSystemActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        chkGoogleMap = findViewById(R.id.chkGoogleMap);
        chkWaze = findViewById(R.id.chkWaze);
        img_back = findViewById(R.id.img_back);
        btnSave = findViewById(R.id.btnSave);

        img_back.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        chkGoogleMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkWaze.setChecked(false);
                }
            }
        });

        chkWaze.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chkGoogleMap.setChecked(false);
                }
            }
        });

        String defaultMap = prefsUtil.getDefaultMap();

        if (defaultMap.equalsIgnoreCase("G")) {
            chkGoogleMap.setChecked(true);
        } else if (defaultMap.equalsIgnoreCase("W")) {
            boolean isWazeInstall = isAppExist("com.waze");
            if (isWazeInstall) {
                chkWaze.setChecked(true);
            } else {
//                rdbGoogle.setChecked(true);
                prefsUtil.setDefaultMap("");
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view == img_back) {
            onBackPressed();
        } else if (view == btnSave) {
            if (chkGoogleMap.isChecked()) {
                prefsUtil.setDefaultMap("G");
                finish();
            } else if (chkWaze.isChecked()) {
                boolean isWazeInstall = isAppExist("com.waze");
                if (isWazeInstall) {
                    prefsUtil.setDefaultMap("W");
                    finish();
                } else {
                    chkWaze.setChecked(false);
//                        rdbGoogle.setChecked(true);
                    Toast.makeText(mContext, R.string.no_waze_app_found, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.msg_validation_default_nav_sys, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAppExist(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_nav_setting_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
