package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteSubPojo {

	@SerializedName("favorite_users")
	private List<FavoritePojoItem> favoritePojoItem;

	public void setFavoritePojoItem(List<FavoritePojoItem> favoritePojoItem){
		this.favoritePojoItem = favoritePojoItem;
	}

	public List<FavoritePojoItem> getFavoritePojoItem(){
		return favoritePojoItem;
	}

	@Override
	public String toString() {
		return "FavoriteSubPojo{" +
				"favoritePojoItem=" + favoritePojoItem +
				'}';
	}
}