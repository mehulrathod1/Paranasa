package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobDetailPojo{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("data")
	private JobDetailPojoItem jobDetailPojoItem;

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

	public void setJobDetailPojoItem(JobDetailPojoItem jobDetailPojoItem){
		this.jobDetailPojoItem = jobDetailPojoItem;
	}

	public JobDetailPojoItem getJobDetailPojoItem(){
		return jobDetailPojoItem;
	}

	@Override
 	public String toString(){
		return 
			"JobDetailPojo{" + 
			"message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			",jobDetailPojoItem = '" + jobDetailPojoItem + '\'' + 
			"}";
		}
}