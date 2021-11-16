package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class JobListPojoItem {

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("lng")
	private String lng;

	@SerializedName("job_date")
	private String jobDate;

	@SerializedName("assign_to")
	private String assignTo;

	@SerializedName("customer_phone")
	private String customerPhone;

	@SerializedName("assign_to_user_id")
	private String assignToUserId;

	@SerializedName("completed_date")
	private String completedDate;

	@SerializedName("job_status")
	private String jobStatus;

	@SerializedName("customer_rating")
	private String customerRating;

	@SerializedName("service")
	private String service;

	@SerializedName("customer_email")
	private String customerEmail;

	@SerializedName("id")
	private String id;

	@SerializedName("customer_name")
	private String customerName;

	@SerializedName("job")
	private String job;

	@SerializedName("lat")
	private String lat;

	@SerializedName("remarks")
	private String remarks;

	@SerializedName("customer_profile")
	private String customerProfile;

	@SerializedName("professional_profile")
	private String professionalProfile;

	@SerializedName("invoice_status")
	private String invoiceStatus;

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public String getProfessionalProfile() {
		return professionalProfile;
	}

	public String getCustomerProfile() {
		return customerProfile;
	}

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setJobDate(String jobDate){
		this.jobDate = jobDate;
	}

	public String getJobDate(){
		return jobDate;
	}

	public void setAssignTo(String assignTo){
		this.assignTo = assignTo;
	}

	public String getAssignTo(){
		return assignTo;
	}

	public void setCustomerPhone(String customerPhone){
		this.customerPhone = customerPhone;
	}

	public String getCustomerPhone(){
		return customerPhone;
	}

	public void setAssignToUserId(String assignToUserId){
		this.assignToUserId = assignToUserId;
	}

	public String getAssignToUserId(){
		return assignToUserId;
	}

	public void setCompletedDate(String completedDate){
		this.completedDate = completedDate;
	}

	public String getCompletedDate(){
		return completedDate;
	}

	public void setJobStatus(String jobStatus){
		this.jobStatus = jobStatus;
	}

	public String getJobStatus(){
		return jobStatus;
	}

	public void setCustomerRating(String customerRating){
		this.customerRating = customerRating;
	}

	public String getCustomerRating(){
		return customerRating;
	}

	public void setService(String service){
		this.service = service;
	}

	public String getService(){
		return service;
	}

	public void setCustomerEmail(String customerEmail){
		this.customerEmail = customerEmail;
	}

	public String getCustomerEmail(){
		return customerEmail;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCustomerName(String customerName){
		this.customerName = customerName;
	}

	public String getCustomerName(){
		return customerName;
	}

	public void setJob(String job){
		this.job = job;
	}

	public String getJob(){
		return job;
	}

	public void setLat(String lat){
		this.lat = lat;
	}

	public String getLat(){
		return lat;
	}

	public void setRemarks(String remarks){
		this.remarks = remarks;
	}

	public String getRemarks(){
		return remarks;
	}

	@Override
 	public String toString(){
		return 
			"JobListPojoItem{" +
			"customer_address = '" + customerAddress + '\'' + 
			",lng = '" + lng + '\'' + 
			",job_date = '" + jobDate + '\'' + 
			",assign_to = '" + assignTo + '\'' + 
			",customer_phone = '" + customerPhone + '\'' + 
			",assign_to_user_id = '" + assignToUserId + '\'' + 
			",completed_date = '" + completedDate + '\'' + 
			",job_status = '" + jobStatus + '\'' + 
			",customer_rating = '" + customerRating + '\'' + 
			",service = '" + service + '\'' + 
			",customer_email = '" + customerEmail + '\'' + 
			",id = '" + id + '\'' + 
			",customer_name = '" + customerName + '\'' + 
			",job = '" + job + '\'' + 
			",lat = '" + lat + '\'' + 
			",remarks = '" + remarks + '\'' + 
			"}";
		}
}