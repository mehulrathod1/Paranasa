package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class UserByEmailPojoItem {

    @SerializedName("country")
    private String country;

    @SerializedName("address")
    private String address;

    @SerializedName("birthdate")
    private String birthdate;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("city")
    private String city;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("user_role")
    private String userRole;

    @SerializedName("user_code")
    private String userCode;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("service")
    private String service;

    @SerializedName("location")
    private String location;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("email")
    private String email;

    @SerializedName("lat")
    private String lat;

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthdate() {
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserRole() {
        return userRole;
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

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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
                "UserByEmailPojoItem{" +
                        "country = '" + country + '\'' +
                        ",address = '" + address + '\'' +
                        ",birthdate = '" + birthdate + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",city = '" + city + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",logo_image = '" + logoImage + '\'' +
                        ",user_role = '" + userRole + '\'' +
                        ",user_code = '" + userCode + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",service = '" + service + '\'' +
                        ",location = '" + location + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",keyword = '" + keyword + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",email = '" + email + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}