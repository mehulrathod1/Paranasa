package com.ni.parnasa.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class ViewInvoicePojoItem{

	@SerializedName("discount")
	private String discount;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("tax")
	private String tax;

	@SerializedName("how_to_pay")
	private String howToPay;

	@SerializedName("professional_id")
	private String professionalId;

	@SerializedName("tax_rate")
	private String taxRate;

	@SerializedName("parts_dicers")
	private String partsDicers;

	@SerializedName("basic_rate")
	private String basicRate;

	@SerializedName("working_hour")
	private String workingHour;

	@SerializedName("parts_dicers_rate")
	private String partsDicersRate;

	@SerializedName("total")
	private String total;

	@SerializedName("job_booking_id")
	private String jobBookingId;

	@SerializedName("job_id")
	private String jobId;

	@SerializedName("Payment_status")
	private String paymentStatus;

	@SerializedName("invoice_id")
	private String invoiceId;

	@SerializedName("tip")
	private String tip;

	@SerializedName("working_hour_rate")
	private String workingHourRate;

	@SerializedName("grand_total")
	private String grandTotal;

	@SerializedName("customer_id")
	private String customerId;

	@SerializedName("payment_method")
	private String paymentMethod;

	public void setDiscount(String discount){
		this.discount = discount;
	}

	public String getDiscount(){
		if(!TextUtils.isEmpty(discount)) {
			return discount;
		}else {
			return "0";
		}
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setTax(String tax){
		this.tax = tax;
	}

	public String getTax(){
		return tax;
	}

	public void setHowToPay(String howToPay){
		this.howToPay = howToPay;
	}

	public String getHowToPay(){
		return howToPay;
	}

	public void setProfessionalId(String professionalId){
		this.professionalId = professionalId;
	}

	public String getProfessionalId(){
		return professionalId;
	}

	public void setTaxRate(String taxRate){
		this.taxRate = taxRate;
	}

	public String getTaxRate(){
		return taxRate;
	}

	public void setPartsDicers(String partsDicers){
		this.partsDicers = partsDicers;
	}

	public String getPartsDicers(){
		return partsDicers;
	}

	public void setBasicRate(String basicRate){
		this.basicRate = basicRate;
	}

	public String getBasicRate(){
		return basicRate;
	}

	public void setWorkingHour(String workingHour){
		this.workingHour = workingHour;
	}

	public String getWorkingHour(){
		return workingHour;
	}

	public void setPartsDicersRate(String partsDicersRate){
		this.partsDicersRate = partsDicersRate;
	}

	public String getPartsDicersRate(){
		return partsDicersRate;
	}

	public void setTotal(String total){
		this.total = total;
	}

	public String getTotal(){
		return total;
	}

	public void setJobBookingId(String jobBookingId){
		this.jobBookingId = jobBookingId;
	}

	public String getJobBookingId(){
		return jobBookingId;
	}

	public void setJobId(String jobId){
		this.jobId = jobId;
	}

	public String getJobId(){
		return jobId;
	}

	public void setPaymentStatus(String paymentStatus){
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentStatus(){
		return paymentStatus;
	}

	public void setInvoiceId(String invoiceId){
		this.invoiceId = invoiceId;
	}

	public String getInvoiceId(){
		return invoiceId;
	}

	public void setTip(String tip){
		this.tip = tip;
	}

	public String getTip(){
		return tip;
	}

	public void setWorkingHourRate(String workingHourRate){
		this.workingHourRate = workingHourRate;
	}

	public String getWorkingHourRate(){
		return workingHourRate;
	}

	public void setGrandTotal(String grandTotal){
		this.grandTotal = grandTotal;
	}

	public String getGrandTotal(){
		return grandTotal;
	}

	public void setCustomerId(String customerId){
		this.customerId = customerId;
	}

	public String getCustomerId(){
		return customerId;
	}

	public void setPaymentMethod(String paymentMethod){
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethod(){
		return paymentMethod;
	}

	@Override
 	public String toString(){
		return 
			"ViewInvoicePojoItem{" + 
			"discount = '" + discount + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",tax = '" + tax + '\'' + 
			",how_to_pay = '" + howToPay + '\'' + 
			",professional_id = '" + professionalId + '\'' + 
			",tax_rate = '" + taxRate + '\'' + 
			",parts_dicers = '" + partsDicers + '\'' + 
			",basic_rate = '" + basicRate + '\'' + 
			",working_hour = '" + workingHour + '\'' + 
			",parts_dicers_rate = '" + partsDicersRate + '\'' + 
			",total = '" + total + '\'' + 
			",job_booking_id = '" + jobBookingId + '\'' + 
			",job_id = '" + jobId + '\'' + 
			",payment_status = '" + paymentStatus + '\'' + 
			",invoice_id = '" + invoiceId + '\'' + 
			",tip = '" + tip + '\'' + 
			",working_hour_rate = '" + workingHourRate + '\'' + 
			",grand_total = '" + grandTotal + '\'' + 
			",customer_id = '" + customerId + '\'' + 
			",payment_method = '" + paymentMethod + '\'' + 
			"}";
		}
}