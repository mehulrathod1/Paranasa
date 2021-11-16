package com.ni.parnasa.utils;

import android.util.Log;

public class MyLogs {

    private static boolean isLogEnable = true;

    public static void setEnable(boolean state) {
        isLogEnable = state;
    }

    /*public static void d(String tag, String msg) {
        if (isLogEnable) {
            Log.d((tag != "" ? tag : "TAG"), msg);
        }
    }*/

    public static void e(String tag, String msg) {
        if (isLogEnable) {
            Log.e((tag != "" ? tag : "TAG"), msg);
        }
    }

    /*public static void v(String tag, String msg) {
        if (isLogEnable) {
            Log.v((tag != "" ? tag : "TAG"), msg);
        }
    }*/

    public static void i(String tag, String msg) {
        if (isLogEnable) {
            Log.i((tag != "" ? tag : "TAG"), msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isLogEnable) {
            Log.w((tag != "" ? tag : "TAG"), msg);
        }
    }
}


