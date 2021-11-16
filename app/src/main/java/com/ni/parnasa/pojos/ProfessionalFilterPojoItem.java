package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ProfessionalFilterPojoItem {

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("rating")
    private int rating;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("service_icon")
    private String serviceIcon;

    @SerializedName("user_code")
    private String userCode;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("service")
    private String service;

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("location")
    private String location;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLng() {
        return lng;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
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

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
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
        return firstName + " " + lastName;
    }
}