package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class RatingsSummery{

	@SerializedName("ratings_ratio")
	private RatingsRatio ratingsRatio;

	@SerializedName("average_ratings")
	private String averageRatings;

	public void setRatingsRatio(RatingsRatio ratingsRatio){
		this.ratingsRatio = ratingsRatio;
	}

	public RatingsRatio getRatingsRatio(){
		return ratingsRatio;
	}

	public void setAverageRatings(String averageRatings){
		this.averageRatings = averageRatings;
	}

	public String getAverageRatings(){
		return averageRatings;
	}

	@Override
 	public String toString(){
		return 
			"RatingsSummery{" + 
			"ratings_ratio = '" + ratingsRatio + '\'' + 
			",average_ratings = '" + averageRatings + '\'' + 
			"}";
		}
}