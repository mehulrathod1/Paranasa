package com.ni.parnasa.utils;

import android.content.Context;
import android.widget.LinearLayout;

import com.ni.parnasa.BuildConfig;
import com.ni.parnasa.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class GoogleAdLoader {

    private Context mContext;
    private LinearLayout linlayContainer;
    private AdView mAdView;
    private AdListener adListener;

    public GoogleAdLoader() {
    }

    public GoogleAdLoader(Context mContext, LinearLayout linlayContainer, AdListener adListener) {
        this.mContext = mContext;
        this.linlayContainer = linlayContainer;
        this.adListener = adListener;

        loadAd("");
    }

    public GoogleAdLoader(Context mContext, String adUnitId, LinearLayout linlayContainer, AdListener adListener) {
        this.mContext = mContext;
        this.linlayContainer = linlayContainer;
        this.adListener = adListener;

        loadAd(adUnitId);
    }

    private void loadAd(String adUnitId) {
        mAdView = new AdView(mContext);
        mAdView.setAdSize(AdSize.BANNER);
        if (adUnitId.equals(""))
            mAdView.setAdUnitId(mContext.getString(R.string.admob_unit_id));
        else
            mAdView.setAdUnitId(adUnitId);

        linlayContainer.addView(mAdView);

        if (BuildConfig.DEBUG) {
            mAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
        } else {
            mAdView.loadAd(new AdRequest.Builder().build());
        }
        mAdView.setAdListener(adListener);
    }

    public static class CustomAdListenerGoogle extends AdListener {

        @Override
        public void onAdClosed() {
            super.onAdClosed();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
        }
    }
}
