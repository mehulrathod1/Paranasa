package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionFeePojo{

	@SerializedName("data")
	private SubscriptionFeePojoItem subscriptionFeePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setSubscriptionFeePojoItem(SubscriptionFeePojoItem subscriptionFeePojoItem){
		this.subscriptionFeePojoItem = subscriptionFeePojoItem;
	}

	public SubscriptionFeePojoItem getSubscriptionFeePojoItem(){
		return subscriptionFeePojoItem;
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
			"SubscriptionFeePojo{" + 
			"subscriptionFeePojoItem = '" + subscriptionFeePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}