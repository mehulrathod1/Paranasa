package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfessionalFilterPojo{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private List<ProfessionalFilterPojoItem> professionalFilterPojoItem;

	@SerializedName("status")
	private String status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setProfessionalFilterPojoItem(List<ProfessionalFilterPojoItem> professionalFilterPojoItem){
		this.professionalFilterPojoItem = professionalFilterPojoItem;
	}

	public List<ProfessionalFilterPojoItem> getProfessionalFilterPojoItem(){
		return professionalFilterPojoItem;
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
			"ProfessionalFilterPojo{" + 
			"message = '" + message + '\'' + 
			",professionalFilterPojoItem = '" + professionalFilterPojoItem + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}