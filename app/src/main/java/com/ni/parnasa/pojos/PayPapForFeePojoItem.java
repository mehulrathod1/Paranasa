package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class PayPapForFeePojoItem{

	@SerializedName("user_id")
	private String userId;

	@SerializedName("paypal_url")
	private String paypalUrl;

	@SerializedName("stripe_url")
	private String stripeUrl;

	public String getStripeUrl() {
		return stripeUrl;
	}

	public void setStripeUrl(String stripeUrl) {
		this.stripeUrl = stripeUrl;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setPaypalUrl(String paypalUrl){
		this.paypalUrl = paypalUrl;
	}

	public String getPaypalUrl(){
		return paypalUrl;
	}

	@Override
 	public String toString(){
		return 
			"PayPapForFeePojoItem{" + 
			"user_id = '" + userId + '\'' + 
			",paypal_url = '" + paypalUrl + '\'' +
					",stripe_url = '" + stripeUrl + '\'' +
			"}";
		}
}