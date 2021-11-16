package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionStripePojo {

	@SerializedName("data")
	private SubscriptionStripePojoItem subscriptionStripePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public SubscriptionStripePojoItem getSubscriptionStripePojoItem() {
		return subscriptionStripePojoItem;
	}

	public void setSubscriptionStripePojoItem(SubscriptionStripePojoItem subscriptionStripePojoItem) {
		this.subscriptionStripePojoItem = subscriptionStripePojoItem;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"SubscriptionStripePojo{" +
			"subscriptionStripePojoItem = '" + subscriptionStripePojoItem + '\'' +
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}