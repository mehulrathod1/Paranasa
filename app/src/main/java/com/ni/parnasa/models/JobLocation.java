package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobLocation{

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("lng")
	private String lng;

	@SerializedName("lat")
	private String lat;

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setLat(String lat){
		this.lat = lat;
	}

	public String getLat(){
		return lat;
	}

	@Override
 	public String toString(){
		return 
			"JobLocation{" + 
			"customer_address = '" + customerAddress + '\'' + 
			",lng = '" + lng + '\'' + 
			",lat = '" + lat + '\'' + 
			"}";
		}
}