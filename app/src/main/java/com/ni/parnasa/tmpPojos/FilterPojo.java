package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilterPojo{

	@SerializedName("data")
	private List<FilterPojoItem> filterPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setFilterPojoItem(List<FilterPojoItem> filterPojoItem){
		this.filterPojoItem = filterPojoItem;
	}

	public List<FilterPojoItem> getFilterPojoItem(){
		return filterPojoItem;
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
			"FilterPojo{" + 
			"filterPojoItem = '" + filterPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}