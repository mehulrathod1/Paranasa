package com.ni.parnasa.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class PrefUtils {

    private static final int DEFAULT_INT = 0;
    public static final String PREF_KEY_APP_AUTO_START = "";
    private static final String DEFAULT_STRING = "";
    private static final float DEFAULT_FLOAT = -1f;
    private static final boolean DEFAULT_BOOLEAN = false;

    private static SharedPreferences sharedPreferences;
    private static PrefUtils prefsUtil;

    private PrefUtils(@NonNull Context mContext) {
        if (sharedPreferences == null) {
            sharedPreferences = mContext.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }
    }

    public static PrefUtils with(@NonNull Context context) {
        if (prefsUtil == null) {
            prefsUtil = new PrefUtils(context);
        }
        return prefsUtil;
    }

    public void write(String name, int number) {
        sharedPreferences.edit().putInt(name, number).apply();
    }

    public void write(String name, String str) {
        sharedPreferences.edit().putString(name, str).apply();
    }

    public void write(String name, float number) {
        sharedPreferences.edit().putFloat(name, number).apply();
    }

    public void write(String name, boolean bool) {
        sharedPreferences.edit().putBoolean(name, bool).apply();
    }

    public int readInt(String name) {
        return sharedPreferences.getInt(name, DEFAULT_INT);
    }

    public String readString(String name) {
        return sharedPreferences.getString(name, DEFAULT_STRING);
    }

    public float readFloat(String name) {
        return sharedPreferences.getFloat(name, DEFAULT_FLOAT);
    }

    public boolean readBoolean(String name) {
        return sharedPreferences.getBoolean(name, DEFAULT_BOOLEAN);
    }

    public void clearPrefs() {
        sharedPreferences.edit().clear().apply();
    }
}
