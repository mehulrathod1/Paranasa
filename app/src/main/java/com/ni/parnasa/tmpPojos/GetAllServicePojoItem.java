package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class GetAllServicePojoItem {

    @SerializedName("service_icon")
    private String serviceIcon;

    @SerializedName("service_name")
    private String serviceName;

    @SerializedName("service_id")
    private String serviceId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("status")
    private String status;

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return serviceName;
    }
}