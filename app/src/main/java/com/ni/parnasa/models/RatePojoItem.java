package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class RatePojoItem {

    @SerializedName("basic_rate")
    private String basicRate;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("hour_rate")
    private String hourRate;

    @SerializedName("jobrate_id")
    private String jobrateId;

    @SerializedName("tax_rate")
    private String taxRate;

    public void setBasicRate(String basicRate) {
        this.basicRate = basicRate;
    }

    public String getBasicRate() {
        return basicRate;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setHourRate(String hourRate) {
        this.hourRate = hourRate;
    }

    public String getHourRate() {
        return hourRate;
    }

    public void setJobrateId(String jobrateId) {
        this.jobrateId = jobrateId;
    }

    public String getJobrateId() {
        return jobrateId;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxRate() {
        return taxRate;
    }

    @Override
    public String toString() {
        return
                "RatePojoItem{" +
                        "basic_rate = '" + basicRate + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",hour_rate = '" + hourRate + '\'' +
                        ",jobrate_id = '" + jobrateId + '\'' +
                        ",tax_rate = '" + taxRate + '\'' +
                        "}";
    }
}