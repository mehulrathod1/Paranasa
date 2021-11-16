package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class ProfilePojoItem {

    @SerializedName("country")
    private String country;

    @SerializedName("birthdate")
    private String birthdate;

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("city")
    private String city;

    @SerializedName("rating")
    private int rating;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

    @SerializedName("logo_image")
    private String logoImage;

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

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("user_code")
    private String user_code;

    public String getUser_code() {
        return user_code;
    }

    @SerializedName("favorite")
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthdate() {
        return birthdate;
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
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

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return
                "ProfilePojoItem{" +
                        "country = '" + country + '\'' +
                        ",birthdate = '" + birthdate + '\'' +
                        ",address = '" + address + '\'' +
                        ",role = '" + role + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",city = '" + city + '\'' +
                        ",rating = '" + rating + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",company_email = '" + companyEmail + '\'' +
                        ",logo_image = '" + logoImage + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",service = '" + service + '\'' +
                        ",company_name = '" + companyName + '\'' +
                        ",location = '" + location + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",keyword = '" + keyword + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}