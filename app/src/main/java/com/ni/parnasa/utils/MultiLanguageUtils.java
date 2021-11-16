package com.ni.parnasa.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.util.Locale;

public class MultiLanguageUtils {

    private static MultiLanguageUtils instance;
    private PrefsUtil prefsUtil;
    private Context mContext;
    private String lang = "";

    public MultiLanguageUtils(Context mContext) {
        this.mContext = mContext;
        if (prefsUtil == null) {
            prefsUtil = new PrefsUtil(mContext);
        }
        // fetch value from pref and set into 'lang' variable
    }

    public static MultiLanguageUtils getInstance(Context context) {
        if (instance == null) {
            instance = new MultiLanguageUtils(context);
        }
        return instance;
    }

    public void changeLanguage() {
        lang = prefsUtil.getLanguage();
        lang = lang.toLowerCase();
        Log.e("MultiLanguageUtil", "changeLanguage=" + lang);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());

        /*
        if (!lang.equalsIgnoreCase("ar")) {
            lang = "ar"; // set 'lang' variable value into preference
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
//        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        } else {
            MyLogs.w("TAG", "Does not required to change language");
        }*/

    }
}
