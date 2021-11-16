package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class SiteSettingPojoItem{

	@SerializedName("map_refresh_rate")
	private String mapRefreshRate;

	public void setMapRefreshRate(String mapRefreshRate){
		this.mapRefreshRate = mapRefreshRate;
	}

	public String getMapRefreshRate(){
		return mapRefreshRate;
	}

	@Override
 	public String toString(){
		return 
			"SiteSettingPojoItem{" + 
			"map_refresh_rate = '" + mapRefreshRate + '\'' + 
			"}";
		}
}