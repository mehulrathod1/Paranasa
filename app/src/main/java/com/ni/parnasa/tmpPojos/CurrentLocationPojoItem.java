package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class CurrentLocationPojoItem{

	@SerializedName("current_latitude")
	private String currentLatitude;

	@SerializedName("current_longitude")
	private String currentLongitude;

	public void setCurrentLatitude(String currentLatitude){
		this.currentLatitude = currentLatitude;
	}

	public String getCurrentLatitude(){
		return currentLatitude;
	}

	public void setCurrentLongitude(String currentLongitude){
		this.currentLongitude = currentLongitude;
	}

	public String getCurrentLongitude(){
		return currentLongitude;
	}

	@Override
 	public String toString(){
		return 
			"CurrentLocationPojoItem{" + 
			"current_latitude = '" + currentLatitude + '\'' + 
			",current_longitude = '" + currentLongitude + '\'' + 
			"}";
		}
}