package com.ni.parnasa.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class FingerPrintPrefUtil {

    private static final String DEFAULT_STRING = "";
    private static final boolean DEFAULT_BOOLEAN = false;

    private static SharedPreferences sharedPreferences;
    private static FingerPrintPrefUtil fingerPrintPrefUtil;

    private FingerPrintPrefUtil(@NonNull Context mContext) {
        if (sharedPreferences == null) {
            sharedPreferences = mContext.getApplicationContext().getSharedPreferences("fingerPrintAuth", Context.MODE_PRIVATE);
        }
    }

    public static FingerPrintPrefUtil with(@NonNull Context context) {
        if (fingerPrintPrefUtil == null) {
            fingerPrintPrefUtil = new FingerPrintPrefUtil(context);
        }
        return fingerPrintPrefUtil;
    }

    public void write(String name, String str) {
        sharedPreferences.edit().putString(name, str).apply();
    }

    public String readString(String name) {
        return sharedPreferences.getString(name, DEFAULT_STRING);
    }

    public void write(String name, boolean bool) {
        sharedPreferences.edit().putBoolean(name, bool).apply();
    }

    public boolean readBoolean(String name) {
        return sharedPreferences.getBoolean(name, DEFAULT_BOOLEAN);
    }

    public void writeCalendarEventId(int calEventId) {
        sharedPreferences.edit().putInt("calEventId", calEventId).apply();
    }

    public int readCalendarEventId() {
        return sharedPreferences.getInt("calEventId", -1);
    }

}
