package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class UserByEmailPojo {

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserByEmailPojoItem userByEmailPojoItem;

    @SerializedName("status")
    private String status;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setUserByEmailPojoItem(UserByEmailPojoItem userByEmailPojoItem) {
        this.userByEmailPojoItem = userByEmailPojoItem;
    }

    public UserByEmailPojoItem getUserByEmailPojoItem() {
        return userByEmailPojoItem;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "UserByEmailPojo{" +
                        "message = '" + message + '\'' +
                        ",userByEmailPojoItem = '" + userByEmailPojoItem + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}