package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class UserLoginPojo{

	@SerializedName("data")
	private UserLoginPojoItem userLoginPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setUserLoginPojoItem(UserLoginPojoItem userLoginPojoItem){
		this.userLoginPojoItem = userLoginPojoItem;
	}

	public UserLoginPojoItem getUserLoginPojoItem(){
		return userLoginPojoItem;
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
			"data{" +
			"userLoginPojoItem = '" + userLoginPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}