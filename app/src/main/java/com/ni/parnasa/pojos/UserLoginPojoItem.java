package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class UserLoginPojoItem {

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("lng")
    private String lng;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("company_email")
    private String companyEmail;

    @SerializedName("email")
    private String email;

    public String getEmail() {
        return email;
    }

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("mobile_number")
    private String mobileNumber;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("lat")
    private String lat;

    @SerializedName("rating")
    private float rating = 0.0f;

    @SerializedName("birthdate")
    private String birthdate;

    @SerializedName("location")
    private String location;

    @SerializedName("address")
    private String address;

    public String getAddress() {
        return address;
    }

    @SerializedName("company_name")
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    /*@SerializedName("location")
    private String location;*/

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
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
                "UserLoginPojoItem{" +
                        "role = '" + role + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",lng = '" + lng + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",company_email = '" + companyEmail + '\'' +
                        ",logo_image = '" + logoImage + '\'' +
                        ",mobile_number = '" + mobileNumber + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",lat = '" + lat + '\'' +
                        "}";
    }
}