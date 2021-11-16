package com.ni.parnasa.utils;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;

public class CommonUtils {

    public static String loadJSONFromAsset(Activity mActivity,String fileName) {
        String json = null;
        try {
            InputStream is = mActivity.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
