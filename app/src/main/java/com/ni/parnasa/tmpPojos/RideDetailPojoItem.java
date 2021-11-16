package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;


public class RideDetailPojoItem{

	@SerializedName("ride_id")
	private String rideId;

	@SerializedName("ride_end_lat")
	private String rideEndLat;

	@SerializedName("ride_start_lat")
	private String rideStartLat;

	@SerializedName("ride_end_lng")
	private String rideEndLng;

	@SerializedName("ride_start_lng")
	private String rideStartLng;

	public void setRideId(String rideId){
		this.rideId = rideId;
	}

	public String getRideId(){
		return rideId;
	}

	public void setRideEndLat(String rideEndLat){
		this.rideEndLat = rideEndLat;
	}

	public String getRideEndLat(){
		return rideEndLat;
	}

	public void setRideStartLat(String rideStartLat){
		this.rideStartLat = rideStartLat;
	}

	public String getRideStartLat(){
		return rideStartLat;
	}

	public void setRideEndLng(String rideEndLng){
		this.rideEndLng = rideEndLng;
	}

	public String getRideEndLng(){
		return rideEndLng;
	}

	public void setRideStartLng(String rideStartLng){
		this.rideStartLng = rideStartLng;
	}

	public String getRideStartLng(){
		return rideStartLng;
	}

	@Override
 	public String toString(){
		return 
			"RideDetailPojoItem{" + 
			"ride_id = '" + rideId + '\'' + 
			",ride_end_lat = '" + rideEndLat + '\'' + 
			",ride_start_lat = '" + rideStartLat + '\'' + 
			",ride_end_lng = '" + rideEndLng + '\'' + 
			",ride_start_lng = '" + rideStartLng + '\'' + 
			"}";
		}
}