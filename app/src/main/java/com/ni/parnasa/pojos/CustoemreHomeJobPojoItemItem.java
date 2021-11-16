package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class CustoemreHomeJobPojoItemItem{

	@SerializedName("assign_date")
	private String assignDate;

	@SerializedName("job_status")
	private String jobStatus;

	@SerializedName("job_date")
	private String jobDate;

	@SerializedName("service")
	private String service;

	@SerializedName("id")
	private String id;

	@SerializedName("job")
	private String job;

	@SerializedName("server_datetime")
	private String serverDatetime;

	@SerializedName("completed_date")
	private String completedDate;

	public void setAssignDate(String assignDate){
		this.assignDate = assignDate;
	}

	public String getAssignDate(){
		return assignDate;
	}

	public void setJobStatus(String jobStatus){
		this.jobStatus = jobStatus;
	}

	public String getJobStatus(){
		return jobStatus;
	}

	public void setJobDate(String jobDate){
		this.jobDate = jobDate;
	}

	public String getJobDate(){
		return jobDate;
	}

	public void setService(String service){
		this.service = service;
	}

	public String getService(){
		return service;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setJob(String job){
		this.job = job;
	}

	public String getJob(){
		return job;
	}

	public void setServerDatetime(String serverDatetime){
		this.serverDatetime = serverDatetime;
	}

	public String getServerDatetime(){
		return serverDatetime;
	}

	public void setCompletedDate(String completedDate){
		this.completedDate = completedDate;
	}

	public String getCompletedDate(){
		return completedDate;
	}

	@Override
 	public String toString(){
		return 
			"CustoemreHomeJobPojoItemItem{" + 
			"assign_date = '" + assignDate + '\'' + 
			",job_status = '" + jobStatus + '\'' + 
			",job_date = '" + jobDate + '\'' + 
			",service = '" + service + '\'' + 
			",id = '" + id + '\'' + 
			",job = '" + job + '\'' + 
			",server_datetime = '" + serverDatetime + '\'' + 
			",completed_date = '" + completedDate + '\'' + 
			"}";
		}
}