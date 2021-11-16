package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriesPojo{

	@SerializedName("data")
	private List<CategoriesPojoItem> categoriesPojoItem;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setCategoriesPojoItem(List<CategoriesPojoItem> categoriesPojoItem){
		this.categoriesPojoItem = categoriesPojoItem;
	}

	public List<CategoriesPojoItem> getCategoriesPojoItem(){
		return categoriesPojoItem;
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
			"CategoriesPojo{" + 
			"categoriesPojoItem = '" + categoriesPojoItem + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}