package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class FavoritePojoItem {

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("lng")
    private String lng;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

    @SerializedName("location")
    private String location;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("total_average_rating")
    private int total_average_rating;

    public int getTotal_average_rating() {
        return total_average_rating;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLng() {
        return lng;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return
                "FavoritePojoItem{" +
                        "profile_image = '" + profileImage + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",company_name = '" + companyName + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",company_email = '" + companyEmail + '\'' +
                        ",location = '" + location + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}