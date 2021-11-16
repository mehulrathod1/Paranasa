package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class RegistrationFeePojo{

	@SerializedName("data")
	private RegistrationFeePojoItem registrationFeePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setRegistrationFeePojoItem(RegistrationFeePojoItem registrationFeePojoItem){
		this.registrationFeePojoItem = registrationFeePojoItem;
	}

	public RegistrationFeePojoItem getRegistrationFeePojoItem(){
		return registrationFeePojoItem;
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
			"RegistrationFeePojo{" + 
			"registrationFeePojoItem = '" + registrationFeePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}