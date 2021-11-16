package com.ni.parnasa.utils;

import android.content.Context;

public class SaveDataUtility {

    private PrefsUtil prefsUtil;

    public SaveDataUtility(Context mContext) {
        prefsUtil = new PrefsUtil(mContext);
    }

    public void saveData(String userId, String firstName, String lastName, String gender, String logoImage, String userRole, boolean isRequiredFingerAuth, boolean isLogin) {
        prefsUtil.Set_UserID(userId);
        prefsUtil.setUserName(firstName + " " + lastName);
        prefsUtil.setUserGender(gender);
        prefsUtil.setUserPic(logoImage);
        prefsUtil.setRole(userRole);
//        prefsUtil.setRequiredFingerPrint(isRequiredFingerAuth);
        prefsUtil.SetIsloogedIn(isLogin);
    }

    public void saveDeviceAuthToken(String device_auth_token) {
        prefsUtil.setDeviceAuthToken(device_auth_token);
    }

}
