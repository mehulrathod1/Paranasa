package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobCustomerDetail {

    @SerializedName("customer_profile")
    private String customerProfile;

    @SerializedName("customer_average_rating")
    private int customerAverageRating;

    @SerializedName("customer_email")
    private String customerEmail;

    @SerializedName("customer_phone")
    private String customerPhone;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("professional_favorited_customer")
    private boolean professional_favorited_customer;

    public boolean getProfessional_favorited_customer() {
        return professional_favorited_customer;
    }

    public void setCustomerProfile(String customerProfile) {
        this.customerProfile = customerProfile;
    }

    public String getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerAverageRating(int customerAverageRating) {
        this.customerAverageRating = customerAverageRating;
    }

    public int getCustomerAverageRating() {
        return customerAverageRating;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return
                "JobCustomerDetail{" +
                        "customer_profile = '" + customerProfile + '\'' +
                        ",customer_average_rating = '" + customerAverageRating + '\'' +
                        ",customer_email = '" + customerEmail + '\'' +
                        ",customer_phone = '" + customerPhone + '\'' +
                        ",customer_name = '" + customerName + '\'' +
                        ",customer_id = '" + customerId + '\'' +
                        "}";
    }
}