package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAllServicePojo{

	@SerializedName("data")
	private List<GetAllServicePojoItem> getAllServicePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setGetAllServicePojoItem(List<GetAllServicePojoItem> getAllServicePojoItem){
		this.getAllServicePojoItem = getAllServicePojoItem;
	}

	public List<GetAllServicePojoItem> getGetAllServicePojoItem(){
		return getAllServicePojoItem;
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
			"GetAllServicePojo{" + 
			"getAllServicePojoItem = '" + getAllServicePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}