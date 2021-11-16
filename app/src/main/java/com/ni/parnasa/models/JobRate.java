package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobRate{

	@SerializedName("customer")
	private JobRateCustomer jobRateCustomer;

	@SerializedName("professional")
	private JobRateProfessional jobRateProfessional;

	public void setJobRateCustomer(JobRateCustomer jobRateCustomer){
		this.jobRateCustomer = jobRateCustomer;
	}

	public JobRateCustomer getJobRateCustomer(){
		return jobRateCustomer;
	}

	public void setJobRateProfessional(JobRateProfessional jobRateProfessional){
		this.jobRateProfessional = jobRateProfessional;
	}

	public JobRateProfessional getJobRateProfessional(){
		return jobRateProfessional;
	}

	@Override
 	public String toString(){
		return 
			"JobRate{" + 
			"jobRateCustomer = '" + jobRateCustomer + '\'' + 
			",jobRateProfessional = '" + jobRateProfessional + '\'' + 
			"}";
		}
}