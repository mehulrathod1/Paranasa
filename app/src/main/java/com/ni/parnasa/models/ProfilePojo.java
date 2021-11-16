package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class ProfilePojo{

	@SerializedName("data")
	private ProfilePojoItem profilePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setProfilePojoItem(ProfilePojoItem profilePojoItem){
		this.profilePojoItem = profilePojoItem;
	}

	public ProfilePojoItem getProfilePojoItem(){
		return profilePojoItem;
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
			"ProfilePojo{" + 
			"profilePojoItem = '" + profilePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}