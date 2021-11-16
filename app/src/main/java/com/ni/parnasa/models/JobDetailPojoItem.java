package com.ni.parnasa.models;

import com.ni.parnasa.tmpPojos.RideDetailPojoItem;
import com.google.gson.annotations.SerializedName;

public class JobDetailPojoItem {

    @SerializedName("assign_datetime")
    private String assignDate;

    @SerializedName("location")
    private JobLocation jobLocation;

    @SerializedName("job_date")
    private String jobDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("professional_detail")
    private JobProfessionalDetail jobProfessionalDetail;

    @SerializedName("completed_datetime")
    private String completedDate;

    @SerializedName("job_price_rate")
    private JobPriceRate jobPriceRate;

    @SerializedName("job_status")
    private String jobStatus;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("job_id")
    private String jobId;

    @SerializedName("service")
    private String service;

    @SerializedName("customer_detail")
    private JobCustomerDetail jobCustomerDetail;

    @SerializedName("job")
    private String job;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("server_datetime")
    private String serverDatetime;

    @SerializedName("professional_status_for_job")
    private String professionalStatusForJob;

    public String getProfessionalStatusForJob() {
        return professionalStatusForJob;
    }

    @SerializedName("job_rating")
    private JobRate jobRate;

    @SerializedName("invoice_detail")
    private JobInvoiceDetail jobInvoiceDetail;

    @SerializedName("ride_detail")
    private RideDetailPojoItem rideDetail;

    @SerializedName("is_arrived")
    private boolean isArrived;

    public boolean isArrived() {
        return isArrived;
    }

    public RideDetailPojoItem getRideDetail() {
        return rideDetail;
    }

    public JobInvoiceDetail getJobInvoiceDetail() {
        return jobInvoiceDetail;
    }

    public String getServerDatetime() {
        return serverDatetime;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setJobLocation(JobLocation jobLocation) {
        this.jobLocation = jobLocation;
    }

    public JobLocation getJobLocation() {
        return jobLocation;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setJobProfessionalDetail(JobProfessionalDetail jobProfessionalDetail) {
        this.jobProfessionalDetail = jobProfessionalDetail;
    }

    public JobProfessionalDetail getJobProfessionalDetail() {
        return jobProfessionalDetail;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setJobPriceRate(JobPriceRate jobPriceRate) {
        this.jobPriceRate = jobPriceRate;
    }

    public JobPriceRate getJobPriceRate() {
        return jobPriceRate;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setJobCustomerDetail(JobCustomerDetail jobCustomerDetail) {
        this.jobCustomerDetail = jobCustomerDetail;
    }

    public JobCustomerDetail getJobCustomerDetail() {
        return jobCustomerDetail;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setJobRate(JobRate jobRate) {
        this.jobRate = jobRate;
    }

    public JobRate getJobRate() {
        return jobRate;
    }

    @Override
    public String toString() {
        return
                "JobDetailPojoItem{" +
                        "assign_date = '" + assignDate + '\'' +
                        ",jobLocation = '" + jobLocation + '\'' +
                        ",job_date = '" + jobDate + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",jobProfessionalDetail = '" + jobProfessionalDetail + '\'' +
                        ",completed_date = '" + completedDate + '\'' +
                        ",jobPriceRate = '" + jobPriceRate + '\'' +
                        ",job_status = '" + jobStatus + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",job_id = '" + jobId + '\'' +
                        ",service = '" + service + '\'' +
                        ",jobCustomerDetail = '" + jobCustomerDetail + '\'' +
                        ",job = '" + job + '\'' +
                        ",remarks = '" + remarks + '\'' +
                        ",jobRate = '" + jobRate + '\'' +
                        "}";
    }
}