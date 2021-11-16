package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class CurrentLocationPojo{

	@SerializedName("data")
	private CurrentLocationPojoItem currentLocationPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setCurrentLocationPojoItem(CurrentLocationPojoItem currentLocationPojoItem){
		this.currentLocationPojoItem = currentLocationPojoItem;
	}

	public CurrentLocationPojoItem getCurrentLocationPojoItem(){
		return currentLocationPojoItem;
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
			"CurrentLocationPojo{" + 
			"currentLocationPojoItem = '" + currentLocationPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}