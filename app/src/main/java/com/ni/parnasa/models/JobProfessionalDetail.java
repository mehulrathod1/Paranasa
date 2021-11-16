package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

public class JobProfessionalDetail{

	@SerializedName("professional_profile")
	private String professionalProfile;

	@SerializedName("professional_name")
	private String professionalName;

	@SerializedName("pro_average_rating")
	private int proAverageRating;

	@SerializedName("professional_id")
	private String professionalId;

	@SerializedName("lat")
	private String lat;

	public String getLat() {
		return lat;
	}

	@SerializedName("long")
	private String lng;

	@SerializedName("service_icon")
	private String serviceIcon;

	@SerializedName("professional_status")
	private String professionalStatus;

	@SerializedName("customer_favorited_professional")
	private boolean customer_favorited_professional;

	public boolean getCustomer_favorited_professional() {
		return customer_favorited_professional;
	}

	public String getServiceIcon() {
		return serviceIcon;
	}

	public String getLng() {
		return lng;
	}

	public String getProfessionalStatus() {
		return professionalStatus;
	}

	public void setProfessionalProfile(String professionalProfile){
		this.professionalProfile = professionalProfile;
	}

	public String getProfessionalProfile(){
		return professionalProfile;
	}

	public void setProfessionalName(String professionalName){
		this.professionalName = professionalName;
	}

	public String getProfessionalName(){
		return professionalName;
	}

	public void setProAverageRating(int proAverageRating){
		this.proAverageRating = proAverageRating;
	}

	public int getProAverageRating(){
		return proAverageRating;
	}

	public void setProfessionalId(String professionalId){
		this.professionalId = professionalId;
	}

	public String getProfessionalId(){
		return professionalId;
	}

	@Override
 	public String toString(){
		return 
			"JobProfessionalDetail{" + 
			"professional_profile = '" + professionalProfile + '\'' + 
			",professional_name = '" + professionalName + '\'' + 
			",pro_average_rating = '" + proAverageRating + '\'' + 
			",professional_id = '" + professionalId + '\'' + 
			"}";
		}
}