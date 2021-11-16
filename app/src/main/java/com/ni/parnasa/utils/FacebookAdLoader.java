package com.ni.parnasa.utils;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import com.ni.parnasa.BuildConfig;
import com.ni.parnasa.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

//import com.service.pickme.BuildConfig;
//import com.service.pickme.R;

import java.util.Random;

public class FacebookAdLoader {

    private Context mContext;
    private LinearLayout linlayContainer;
    private AdView adView;
    private AdListener adListener;

    public FacebookAdLoader(Context mContext, LinearLayout linlayContainer, AdListener adListener) {
        this.mContext = mContext;
        this.linlayContainer = linlayContainer;
        this.adListener = adListener;

        loadAdd();
    }

    private void loadAdd() {

        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);

        if (BuildConfig.DEBUG) {
//            AdSettings.setDebugBuild(true);
            AdSettings.setTestMode(true);
            AdSettings.addTestDevice("516b1403-c72f-41ec-ae90-d9b02b80b90b");
            adView = new AdView(mContext, "IMG_16_9_APP_INSTALL#" + getRandomKey(), AdSize.BANNER_HEIGHT_50);
        } else {
            adView = new AdView(mContext, getRandomKey(), AdSize.BANNER_HEIGHT_50);
        }

        linlayContainer.addView(adView);

        AdView.AdViewLoadConfig loadAdConfig = adView.buildLoadAdConfig()
                .withAdListener(adListener)
                .build();

        adView.loadAd(loadAdConfig);

    }

    private String getRandomKey() {

        // it will return 0-4
        int random = new Random().nextInt(4);
        Log.e("FacebookAdLoader", "Random:" + random);

        if (random == 1) {
            return mContext.getString(R.string.fb_ad_one);
        } else if (random == 2) {
            return mContext.getString(R.string.fb_ad_two);
        } else if (random == 3) {
            return mContext.getString(R.string.fb_ad_three);
        } else {
            return mContext.getString(R.string.fb_ad_four);
        }

//        return "1563374847152099_1563983293757921"; //this is for test key
    }

    public void destroyAdView() {
        if (adView != null) {
            adView.destroy();
        }
    }

    public static abstract class CustomAdListenerFacebook implements AdListener {
        @Override
        public void onAdLoaded(Ad ad) {

        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    }
}
