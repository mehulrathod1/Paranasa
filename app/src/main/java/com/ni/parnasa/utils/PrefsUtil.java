package com.ni.parnasa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUtil {

    private Context context;
    private Activity activity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String packeageName = "com.ni.parnasa";
    private String USER_ID, UserType, ProfessionaType;
    private String UserName, UserPic, UserGender, JobRate, UserId, HourRate, TaxRate;
    private int MOD_PRIVATE = 0;
    private double latitute, longitute;
    private boolean isLoggedin;
    private String lang = "lang";

    private String fName = "fName", lName = "lName";
    private String lastSendId = "lastSendId";

    public PrefsUtil(Activity activity) {
        this.activity = activity;
        sharedPreferences = this.activity.getSharedPreferences(packeageName, MOD_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public PrefsUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(packeageName, MOD_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /*-------------------*/


    public void setUpdateInterval(int interval) {
        editor.putInt("updateInterval", interval);
        editor.commit();
    }

    public int getUpdateInterval() {
        return sharedPreferences.getInt("updateInterval", 10);
    }

    public void setLastSendId(String strId) {
        editor.putString(lastSendId, strId);
        editor.commit();
    }

    public String getLastSendId() {
        return sharedPreferences.getString(lastSendId, "");
    }

    public void setFname(String name) {
        editor.putString(fName, name);
        editor.commit();
    }

    public String getFname() {
        return sharedPreferences.getString(fName, "");
    }

    public void setUserLoginDetail(String string) {
        editor.putString("userLoginPojo", string);
        editor.commit();
    }

    public String getUserLoginDetail() {
        return sharedPreferences.getString("userLoginPojo", "");
    }

    public void setRequiredFingerPrint(boolean isRequiredFingerPrint) {
        editor.putBoolean("isRequiredFingerPrint", isRequiredFingerPrint);
        editor.commit();
    }

    public boolean isRequiredFingerPrint() {
        return sharedPreferences.getBoolean("isRequiredFingerPrint", false);
    }

    public String getLanguage() {
        return sharedPreferences.getString(lang, "EN");
    }

    public void setLanguage(String lang1) {
        editor.putString(lang, lang1);
        editor.commit();
    }

    public String getRole() {
        return sharedPreferences.getString("role", "");
    }

    public void setRole(String role) {
        editor.putString("role", role);
        editor.commit();
    }

    public void setDeviceAuthToken(String token) {
        editor.putString("deviceAuthToken", token);
        editor.commit();
    }

    public String getDeviceAuthToken() {
        return sharedPreferences.getString("deviceAuthToken", "");
    }

    /*-----------------*/


    public void Set_UserType(String userType) {
        editor.putString("UserType", userType);
        editor.commit();
    }

    public String GetUserType() {
        UserType = sharedPreferences.getString("UserType", "");
        return UserType;
    }

    public void Set_professionaType(String professionaType) {
        editor.putString("professionaType", professionaType);
        editor.commit();
    }

    public String GetprofessionaType() {
        ProfessionaType = sharedPreferences.getString("professionaType", "");
        return ProfessionaType;
    }

    public String getJobRate() {
        JobRate = sharedPreferences.getString("jobRate", "");
        return JobRate;
    }

    public void setJobRate(String jobRate) {
        editor.putString("jobRate", jobRate);
        editor.commit();
    }

    /*public String getUserId() {
        UserId = sharedPreferences.getString("userId", "");
        return UserId;
    }

    public void setUserId(String userId) {
        editor.putString("userId", userId);
        editor.commit();
    }*/

    public String getHourRate() {
        HourRate = sharedPreferences.getString("hourRate", "");
        return HourRate;
    }

    public void setHourRate(String hourRate) {
        editor.putString("hourRate", hourRate);
        editor.commit();
    }

    public String getTaxRate() {
        TaxRate = sharedPreferences.getString("taxRate", "");
        return TaxRate;
    }

    public void setTaxRate(String taxRate) {
        editor.putString("taxRate", taxRate);
        editor.commit();
    }


    public void SetIsloogedIn(boolean logged) {
        editor.putBoolean("isloggedin", true);
        editor.commit();
    }

    public boolean getLoggdInstaus() {
        isLoggedin = sharedPreferences.getBoolean("isloggedin", false);
        return isLoggedin;
    }


    public void Set_UserID(String UserID) {

        editor.putString("USERID", UserID);
        editor.commit();
    }

    public String GetUserID() {
        USER_ID = sharedPreferences.getString("USERID", "");
        return USER_ID;
    }

    public String getUserGender() {
        UserGender = sharedPreferences.getString("userGender", "");

        return UserGender;
    }

    public void setUserGender(String userGender) {

        editor.putString("userGender", userGender);
        editor.commit();
    }

    public void LogOut() {

        String tmp = getDefaultMap();
        String lang = getLanguage();

        editor.clear();
        editor.commit();

        setDefaultMap(tmp);
        setLanguage(lang);
    }

    public void setDefaultMap(String val) {
        editor.putString("defaultMap", val);
        editor.commit();
    }

    public String getDefaultMap() {
        return sharedPreferences.getString("defaultMap", "");
    }

    public String getUserName() {
        UserName = sharedPreferences.getString("userName", "");
        return UserName;
    }

    public void setUserName(String userName) {
        editor.putString("userName", userName);
        editor.commit();
    }

    public String getUserPic() {
        UserPic = sharedPreferences.getString("userPic", "");
        return UserPic;
    }

    public void setUserPic(String userPic) {
        editor.putString("userPic", userPic);
        editor.commit();
    }

    public void setlattitute(double lattitute) {
        editor.putString("lattitute", String.valueOf(lattitute));
        editor.commit();
    }

    public double getlattitute() {
        latitute = Double.parseDouble(sharedPreferences.getString("lattitute", ""));
        return latitute;
    }

    public void setLongitute(double longitute) {
        editor.putString("longitute", String.valueOf(longitute));
        editor.commit();
    }

    public double getLongitute() {
        longitute = Double.parseDouble(sharedPreferences.getString("longitute", ""));
        return longitute;
    }


    public void setLat(String lat) {
        editor.putString("lat", lat);
        editor.commit();
    }

    public String getLat() {
        return sharedPreferences.getString("lat", "0.0");
    }

    public void setLng(String lng) {
        editor.putString("lng", lng);
        editor.commit();
    }

    public String getLng() {
        return sharedPreferences.getString("lng", "0.0");
    }

    public void setRating(String rating) {
        editor.putString("rating", rating);
        editor.commit();
    }

    public String getRating() {
        return sharedPreferences.getString("rating", "0");
    }


}
