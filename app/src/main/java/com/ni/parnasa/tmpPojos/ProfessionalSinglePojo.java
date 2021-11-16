package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class ProfessionalSinglePojo {

	@SerializedName("data")
	private ProfessionalSinglePojoItem professionalSinglePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setProfessionalSinglePojoItem(ProfessionalSinglePojoItem professionalSinglePojoItem){
		this.professionalSinglePojoItem = professionalSinglePojoItem;
	}

	public ProfessionalSinglePojoItem getProfessionalSinglePojoItem(){
		return professionalSinglePojoItem;
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
			"ProfessionalSinglePojo{" +
			"professionalSinglePojoItem = '" + professionalSinglePojoItem + '\'' +
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}