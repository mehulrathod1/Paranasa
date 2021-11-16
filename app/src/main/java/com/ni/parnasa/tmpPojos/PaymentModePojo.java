package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class PaymentModePojo{

	@SerializedName("data")
	private PaymentModePojoItem paymentModePojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setPaymentModePojoItem(PaymentModePojoItem paymentModePojoItem){
		this.paymentModePojoItem = paymentModePojoItem;
	}

	public PaymentModePojoItem getPaymentModePojoItem(){
		return paymentModePojoItem;
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
			"PaymentModePojo{" + 
			"paymentModePojoItem = '" + paymentModePojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}