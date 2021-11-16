package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class PayPapForFeePojo{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("data")
	private PayPapForFeePojoItem payPapForFeePojoItem;

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

	public void setPayPapForFeePojoItem(PayPapForFeePojoItem payPapForFeePojoItem){
		this.payPapForFeePojoItem = payPapForFeePojoItem;
	}

	public PayPapForFeePojoItem getPayPapForFeePojoItem(){
		return payPapForFeePojoItem;
	}

	@Override
 	public String toString(){
		return 
			"PayPapForFeePojo{" + 
			"message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			",payPapForFeePojoItem = '" + payPapForFeePojoItem + '\'' + 
			"}";
		}
}