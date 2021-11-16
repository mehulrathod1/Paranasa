package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class ReviewRatingPojo{

	@SerializedName("data")
	private ReviewRatingPojoItem reviewRatingPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setReviewRatingPojoItem(ReviewRatingPojoItem reviewRatingPojoItem){
		this.reviewRatingPojoItem = reviewRatingPojoItem;
	}

	public ReviewRatingPojoItem getReviewRatingPojoItem(){
		return reviewRatingPojoItem;
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
			"ReviewRatingPojo{" + 
			"reviewRatingPojoItem = '" + reviewRatingPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}