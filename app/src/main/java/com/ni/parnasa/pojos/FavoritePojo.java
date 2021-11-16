package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class FavoritePojo{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private FavoriteSubPojo favoriteSubPojo;

	@SerializedName("status")
	private String status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public FavoriteSubPojo getFavoriteSubPojo() {
		return favoriteSubPojo;
	}

	public void setFavoriteSubPojo(FavoriteSubPojo favoriteSubPojo) {
		this.favoriteSubPojo = favoriteSubPojo;
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
			"FavoritePojo{" + 
			"message = '" + message + '\'' + 
			",favoritePojoItem = '" + favoriteSubPojo + '\'' +
			",status = '" + status + '\'' + 
			"}";
		}
}