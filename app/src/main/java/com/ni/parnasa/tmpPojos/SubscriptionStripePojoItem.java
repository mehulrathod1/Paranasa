package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionStripePojoItem {

    @SerializedName("stripe_url")
    private String stripeUrl;

    public String getStripeUrl() {
        return stripeUrl;
    }

    public void setStripeUrl(String stripeUrl) {
        this.stripeUrl = stripeUrl;
    }

    @Override
    public String toString() {
        return
                "SubscriptionStripePojoItem{" +
                        "stripe_url = '" + stripeUrl + '\'' +
                        "}";
    }
}