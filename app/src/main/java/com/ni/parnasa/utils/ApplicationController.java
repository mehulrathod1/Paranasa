package com.ni.parnasa.utils;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import com.facebook.ads.AudienceNetworkAds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationController extends Application {

    public Bitmap cropped = null;
    public Bitmap img = null;

    public double latitude,longitude;
    public Date date = null;
    public List<String> keywordList = new ArrayList<>();

    private String lang = "EN";
    private Locale locale = null;
    private static ApplicationController mInstance;
    private PrefsUtil preferences;
//    public static final String FONT_PATH = "Muli_Regular.ttf";

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

//        SystemClock.sleep(TimeUnit.SECONDS.toMillis(2));
//        preferences = new PrefsUtil(getApplicationContext());
//        changeLang();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (locale != null) {
            Locale.setDefault(locale);
            Configuration config = new Configuration(newConfig);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }*/
    }

    public void changeLang() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        /*try {
            if (flag == 1) {
                lang = "HI";
            } else if (flag == 2) {
                lang = "GU";
            } else {
                lang = "EN";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            if (preferences.getLanguage().equalsIgnoreCase("en")) {
                lang = "en";
            } else if (preferences.getLanguage().equalsIgnoreCase("hi")) {
                lang = "hi";
            } else {
                lang = "en";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!"".equals(lang)){// && !config.locale.getLanguage().equals(lang)) {

            locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration conf = new Configuration(config);
            conf.locale = locale;
            getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
