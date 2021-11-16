package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class RatePojo{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("data")
	private RatePojoItem ratePojoItem;

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

	public void setRatePojoItem(RatePojoItem ratePojoItem){
		this.ratePojoItem = ratePojoItem;
	}

	public RatePojoItem getRatePojoItem(){
		return ratePojoItem;
	}

	@Override
 	public String toString(){
		return 
			"RatePojo{" + 
			"message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			",ratePojoItem = '" + ratePojoItem + '\'' + 
			"}";
		}
}