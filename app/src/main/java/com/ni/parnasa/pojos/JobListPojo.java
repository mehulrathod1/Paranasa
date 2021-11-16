package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobListPojo{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private List<JobListPojoItem> jobListPojoItem;

	@SerializedName("status")
	private String status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setJobListPojoItem(List<JobListPojoItem> jobListPojoItem){
		this.jobListPojoItem = jobListPojoItem;
	}

	public List<JobListPojoItem> getJobListPojoItem(){
		return jobListPojoItem;
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
			"JobListPojo{" + 
			"message = '" + message + '\'' + 
			",jobListPojoItem = '" + jobListPojoItem + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}