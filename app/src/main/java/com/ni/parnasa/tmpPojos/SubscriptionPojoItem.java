package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionPojoItem {

    @SerializedName("webview_url")
    private String webviewUrl;

    public void setWebviewUrl(String webviewUrl) {
        this.webviewUrl = webviewUrl;
    }

    public String getWebviewUrl() {
        return webviewUrl;
    }

    @Override
    public String toString() {
        return
                "SubscriptionPojoItem{" +
                        "webview_url = '" + webviewUrl + '\'' +
                        "}";
    }
}