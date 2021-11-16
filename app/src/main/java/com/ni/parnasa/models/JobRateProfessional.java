package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobRateProfessional{

	@SerializedName("professional_rating")
	private int professionalRating;

	@SerializedName("review_for_professional")
	private String reviewForProfessional;

	public void setProfessionalRating(int professionalRating){
		this.professionalRating = professionalRating;
	}

	public int getProfessionalRating(){
		return professionalRating;
	}

	public void setReviewForProfessional(String reviewForProfessional){
		this.reviewForProfessional = reviewForProfessional;
	}

	public String getReviewForProfessional(){
		return reviewForProfessional;
	}

	@Override
 	public String toString(){
		return 
			"JobRateProfessional{" + 
			"professional_rating = '" + professionalRating + '\'' + 
			",review_for_professional = '" + reviewForProfessional + '\'' + 
			"}";
		}
}