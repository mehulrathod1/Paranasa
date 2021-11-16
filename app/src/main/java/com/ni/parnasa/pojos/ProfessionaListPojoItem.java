package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ProfessionaListPojoItem {

    @SerializedName("birthdate")
    private Object birthdate;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("keywords")
    private String keywords;

    @SerializedName("rating")
    private float rating;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

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

    @SerializedName("distance_in_km")
    private String distanceInKm;

    @SerializedName("location")
    private String location;

    @SerializedName("commerce_number")
    private String commerceNumber;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("user_profile_picture")
    private String userProfilePicture;

    @SerializedName("is_my_favorite")
    private boolean isMyFavorite;

    @SerializedName("longitude")
    private String professionalLongitude;

    public String getProfessionalLongitude() {
        return professionalLongitude;
    }

    @SerializedName("latitude")
    private String professionalLatitude;

    public String getProfessionalLatitude() {
        return professionalLatitude;
    }

    public boolean isMyFavorite() {
        return isMyFavorite;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }


    public void setBirthdate(Object birthdate) {
        this.birthdate = birthdate;
    }

    public Object getBirthdate() {
        return birthdate;
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

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
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

    public void setDistanceInKm(String distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public String getDistanceInKm() {
        return distanceInKm;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setCommerceNumber(String commerceNumber) {
        this.commerceNumber = commerceNumber;
    }

    public String getCommerceNumber() {
        return commerceNumber;
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
                "ProfessionaListPojoItem{" +
                        "birthdate = '" + birthdate + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",keywords = '" + keywords + '\'' +
                        ",rating = '" + rating + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",company_email = '" + companyEmail + '\'' +
                        ",service_icon = '" + serviceIcon + '\'' +
                        ",user_code = '" + userCode + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",service = '" + service + '\'' +
                        ",company_name = '" + companyName + '\'' +
                        ",distance_in_km = '" + distanceInKm + '\'' +
                        ",location = '" + location + '\'' +
                        ",commerce_number = '" + commerceNumber + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}