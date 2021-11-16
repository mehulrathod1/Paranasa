package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class PaypalPojoItem {

    @SerializedName("redirect_url")
    private String redirectUrl;

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String toString() {
        return
                "PaypalPojoItem{" +
                        "redirect_url = '" + redirectUrl + '\'' +
                        "}";
    }
}