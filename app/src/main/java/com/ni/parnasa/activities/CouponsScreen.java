package com.ni.parnasa.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ni.parnasa.R;
import com.ni.parnasa.utils.FacebookAdLoader;
import com.ni.parnasa.utils.GoogleAdLoader;
import com.ni.parnasa.utils.MultiLanguageUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import androidx.appcompat.app.AppCompatActivity;

public class CouponsScreen extends AppCompatActivity {

    private EditText et_coupon;
    private ImageView imgBack;
    private Button btnSaveCoupon;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiLanguageUtils.getInstance(getBaseContext()).changeLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_screen);

        et_coupon = findViewById(R.id.addcoupon);
        imgBack = findViewById(R.id.img_back);
        btnSaveCoupon = findViewById(R.id.btnSaveCoupon);
        mContext = CouponsScreen.this;

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSaveCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st_coupon = et_coupon.getText().toString().trim();

                if (st_coupon.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.enter_coupon, Toast.LENGTH_SHORT).show();
                } else {

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CouponsScreen.this);
                    builder.setTitle(R.string.app_name);
                    builder.setMessage(R.string.invalid_coupon);
                    builder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }
            }
        });

        setupAdMob();
    }

    private FacebookAdLoader facebookAdLoader;

    private void setupAdMob() {

        /** Load Google Ad */
        LinearLayout linlayConteiner = findViewById(R.id.adMobView);

        new GoogleAdLoader(mContext, getString(R.string.admob_coupon_ad_id), linlayConteiner, new GoogleAdLoader.CustomAdListenerGoogle() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.e("GoogleAd", "onAdFailedToLoad Code :" + errorCode);
            }
        });

        /** Load facebook Ad */
        facebookAdLoader = new FacebookAdLoader(mContext, linlayConteiner, new FacebookAdLoader.CustomAdListenerFacebook() {
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
