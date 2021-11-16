package com.ni.parnasa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.ni.parnasa.R;
import com.ni.parnasa.activities.customer.CustomerHomeActivity;
import com.ni.parnasa.activities.professional.ProfessionalHomeActivity;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.ni.parnasa.utils.PrefsUtil;

public class LanguageSelectActivity extends AppCompatActivity {

    private Context mContext;
    private PrefsUtil prefsUtil;

    private CheckBox[] chkArray = new CheckBox[8];
    private String[] langArray = new String[8];
    private String selectedLang = "";
    private Button btnSave;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        mContext = LanguageSelectActivity.this;
        prefsUtil = new PrefsUtil(mContext);

        img_back = findViewById(R.id.img_back);
        btnSave = findViewById(R.id.btnSave);

        chkArray[0] = findViewById(R.id.chkEnglish);
        chkArray[0].setOnClickListener(mListener);

        chkArray[1] = findViewById(R.id.chkFrench);
        chkArray[1].setOnClickListener(mListener);

        chkArray[2] = findViewById(R.id.chkGermen);
        chkArray[2].setOnClickListener(mListener);

        chkArray[3] = findViewById(R.id.chkHebrew);
        chkArray[3].setOnClickListener(mListener);

        chkArray[4] = findViewById(R.id.chkRussion);
        chkArray[4].setOnClickListener(mListener);

        chkArray[5] = findViewById(R.id.chkSpanish);
        chkArray[5].setOnClickListener(mListener);

        chkArray[6] = findViewById(R.id.chkArabic);
        chkArray[6].setOnClickListener(mListener);

        chkArray[7] = findViewById(R.id.chkChinese);
        chkArray[7].setOnClickListener(mListener);

        langArray[0] = "EN";
        langArray[1] = "FR";
        langArray[2] = "DE";
        langArray[3] = "IW";
        langArray[4] = "RU";
        langArray[5] = "ES";
        langArray[6] = "AR";
        langArray[7] = "ZH";

        selectedLang = prefsUtil.getLanguage();

        if (!selectedLang.equals("")) {
            if (selectedLang.equalsIgnoreCase("en")) { //English
                chkArray[0].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("fr")) { // French
                chkArray[1].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("de")) {  // German
                chkArray[2].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("iw")) {  // Hebrew
                chkArray[3].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("ru")) {  // Russion
                chkArray[4].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("es")) {  // Spanish
                chkArray[5].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("ar")) {  // Arabic
                chkArray[6].setChecked(true);
            } else if (selectedLang.equalsIgnoreCase("zh")) { // Chinese
                chkArray[7].setChecked(true);
            }
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedLang.equals("")) {

                    if (!selectedLang.equalsIgnoreCase(prefsUtil.getLanguage())) {

                        prefsUtil.setLanguage(selectedLang);

//                        finish();

                        if (prefsUtil.getRole().equalsIgnoreCase("Customer")) {
                            Intent intent = new Intent(mContext, CustomerHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, ProfessionalHomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(mContext, R.string.msg_validation_default_nav_sys, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupAdMob();
    }


    private View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final int checkedId = v.getId();
            for (int i = 0; i < chkArray.length; i++) {
                final CheckBox current = chkArray[i];
                if (current.getId() == checkedId) {
                    current.setChecked(true);
                    selectedLang = langArray[i];
                } else {
                    current.setChecked(false);
                }
            }
        }
    };

    private void setupAdMob() {
        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_change_lang_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });
    }
}
