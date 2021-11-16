package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class RatingPojoItems{

	@SerializedName("comments")
	private String comments;

	@SerializedName("rating_id")
	private String ratingId;

	@SerializedName("rate_to")
	private String rateTo;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("gender")
	private String gender;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("rating")
	private String rating;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("logo_image")
	private String logoImage;

	@SerializedName("first_name")
	private String firstName;

	public void setComments(String comments){
		this.comments = comments;
	}

	public String getComments(){
		return comments;
	}

	public void setRatingId(String ratingId){
		this.ratingId = ratingId;
	}

	public String getRatingId(){
		return ratingId;
	}

	public void setRateTo(String rateTo){
		this.rateTo = rateTo;
	}

	public String getRateTo(){
		return rateTo;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setRating(String rating){
		this.rating = rating;
	}

	public String getRating(){
		return rating;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setLogoImage(String logoImage){
		this.logoImage = logoImage;
	}

	public String getLogoImage(){
		return logoImage;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	@Override
 	public String toString(){
		return 
			"RatingPojoItems{" + 
			"comments = '" + comments + '\'' + 
			",rating_id = '" + ratingId + '\'' + 
			",rate_to = '" + rateTo + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",gender = '" + gender + '\'' + 
			",user_id = '" + userId + '\'' + 
			",rating = '" + rating + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",logo_image = '" + logoImage + '\'' + 
			",first_name = '" + firstName + '\'' + 
			"}";
		}
}