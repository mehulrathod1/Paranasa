package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class SocialResponsePojo{

	@SerializedName("data")
	private SocialResponsePojoItem socialResponsePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setSocialResponsePojoItem(SocialResponsePojoItem socialResponsePojoItem){
		this.socialResponsePojoItem = socialResponsePojoItem;
	}

	public SocialResponsePojoItem getSocialResponsePojoItem(){
		return socialResponsePojoItem;
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
			"SocialResponsePojo{" + 
			"socialResponsePojoItem = '" + socialResponsePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}