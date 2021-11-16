package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class PaymentModePojoItem{

	@SerializedName("payment_mode")
	private String paymentMode;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("paypal_email")
	private String paypalEmail;

	public void setPaymentMode(String paymentMode){
		this.paymentMode = paymentMode;
	}

	public String getPaymentMode(){
		return paymentMode;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setPaypalEmail(String paypalEmail){
		this.paypalEmail = paypalEmail;
	}

	public String getPaypalEmail(){
		return paypalEmail;
	}

	@Override
 	public String toString(){
		return 
			"PaymentModePojoItem{" + 
			"payment_mode = '" + paymentMode + '\'' + 
			",user_id = '" + userId + '\'' + 
			",paypal_email = '" + paypalEmail + '\'' + 
			"}";
		}
}