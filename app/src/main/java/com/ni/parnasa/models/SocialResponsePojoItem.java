package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class SocialResponsePojoItem {

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

    @SerializedName("average_rating")
    private int averageRating;

    @SerializedName("device_auth_token")
    private String deviceAuthToken;

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("service")
    private String service;

    @SerializedName("location")
    private String location;

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("member_ship_type")
    private String memberShipType;

    @SerializedName("member_ship_cycle")
    private String memberShipCycle;

    @SerializedName("membership_start_date")
    private String memberShipStartDate;

    @SerializedName("membership_end_date")
    private String memberShipEndDate;

    public String getMemberShipEndDate() {
        return memberShipEndDate;
    }

    public String getMemberShipStartDate() {
        return memberShipStartDate;
    }

    public String getMemberShipCycle() {
        return memberShipCycle;
    }

    public String getMemberShipType() {
        return memberShipType;
    }

    @SerializedName("site_settings")
    private SiteSettingPojoItem siteSettingPojoItem;

    public SiteSettingPojoItem getSiteSettingPojoItem() {
        return siteSettingPojoItem;
    }

    @SerializedName("isRegistrationPurchased")
    private String isRegistrationPurchased;

    public String getIsRegistrationPurchased() {
        return isRegistrationPurchased;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

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

    public void setAverageRating(int averageRating) {
        this.averageRating = averageRating;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public void setDeviceAuthToken(String deviceAuthToken) {
        this.deviceAuthToken = deviceAuthToken;
    }

    public String getDeviceAuthToken() {
        return deviceAuthToken;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getLogoImage() {
        return logoImage;
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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
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
                "SocialResponsePojoItem{" +
                        "address = '" + address + '\'' +
                        ",role = '" + role + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",company_email = '" + companyEmail + '\'' +
                        ",average_rating = '" + averageRating + '\'' +
                        ",device_auth_token = '" + deviceAuthToken + '\'' +
                        ",logo_image = '" + logoImage + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",service = '" + service + '\'' +
                        ",location = '" + location + '\'' +
                        ",keyword = '" + keyword + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}