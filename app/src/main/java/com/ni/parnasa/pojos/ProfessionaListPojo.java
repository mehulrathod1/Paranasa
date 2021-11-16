package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfessionaListPojo{

	@SerializedName("data")
	private List<ProfessionaListPojoItem> professionaListPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setProfessionaListPojoItem(List<ProfessionaListPojoItem> professionaListPojoItem){
		this.professionaListPojoItem = professionaListPojoItem;
	}

	public List<ProfessionaListPojoItem> getProfessionaListPojoItem(){
		return professionaListPojoItem;
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
			"professionaListPojoItem = '" + professionaListPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}