package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class SubscriptionPojo{

	@SerializedName("data")
	private SubscriptionPojoItem subscriptionPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setSubscriptionPojoItem(SubscriptionPojoItem subscriptionPojoItem){
		this.subscriptionPojoItem = subscriptionPojoItem;
	}

	public SubscriptionPojoItem getSubscriptionPojoItem(){
		return subscriptionPojoItem;
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
			"SubscriptionPojo{" + 
			"subscriptionPojoItem = '" + subscriptionPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}