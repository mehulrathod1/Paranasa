package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class UploadImagePojo{

	@SerializedName("data")
	private UploadImagePojoItem uploadImagePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setUploadImagePojoItem(UploadImagePojoItem uploadImagePojoItem){
		this.uploadImagePojoItem = uploadImagePojoItem;
	}

	public UploadImagePojoItem getUploadImagePojoItem(){
		return uploadImagePojoItem;
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
			"uploadImagePojoItem = '" + uploadImagePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}