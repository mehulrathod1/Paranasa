package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobRateCustomer{

	@SerializedName("customer_rating")
	private int customerRating;

	@SerializedName("review_for_customer")
	private String reviewForCustomer;

	public void setCustomerRating(int customerRating){
		this.customerRating = customerRating;
	}

	public int getCustomerRating(){
		return customerRating;
	}

	public void setReviewForCustomer(String reviewForCustomer){
		this.reviewForCustomer = reviewForCustomer;
	}

	public String getReviewForCustomer(){
		return reviewForCustomer;
	}

	@Override
 	public String toString(){
		return 
			"JobRateCustomer{" + 
			"customer_rating = '" + customerRating + '\'' + 
			",review_for_customer = '" + reviewForCustomer + '\'' + 
			"}";
		}
}