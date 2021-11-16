package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class PaypalPojo {

    @SerializedName("data")
    private PaypalPojoItem paypalPojoItem;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public void setPaypalPojoItem(PaypalPojoItem paypalPojoItem) {
        this.paypalPojoItem = paypalPojoItem;
    }

    public PaypalPojoItem getPaypalPojoItem() {
        return paypalPojoItem;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
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
                "PaypalPojo{" +
                        "paypalPojoItem = '" + paypalPojoItem + '\'' +
                        ",message = '" + message + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}