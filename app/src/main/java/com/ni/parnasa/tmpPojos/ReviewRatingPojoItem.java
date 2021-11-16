package com.ni.parnasa.tmpPojos;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ReviewRatingPojoItem{

	@SerializedName("review_ratings")
	private List<RatingPojoItems> reviewRatings;

	@SerializedName("ratings_summery")
	private RatingsSummery ratingsSummery;

	@SerializedName("pagination")
	private Pagination pagination;

	public void setReviewRatings(List<RatingPojoItems> reviewRatings){
		this.reviewRatings = reviewRatings;
	}

	public List<RatingPojoItems> getReviewRatings(){
		return reviewRatings;
	}

	public void setRatingsSummery(RatingsSummery ratingsSummery){
		this.ratingsSummery = ratingsSummery;
	}

	public RatingsSummery getRatingsSummery(){
		return ratingsSummery;
	}

	public void setPagination(Pagination pagination){
		this.pagination = pagination;
	}

	public Pagination getPagination(){
		return pagination;
	}

	@Override
 	public String toString(){
		return 
			"ReviewRatingPojoItem{" + 
			"review_ratings = '" + reviewRatings + '\'' + 
			",ratings_summery = '" + ratingsSummery + '\'' + 
			",pagination = '" + pagination + '\'' + 
			"}";
		}
}