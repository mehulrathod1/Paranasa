package com.ni.parnasa.tmpPojos;

import com.google.gson.annotations.SerializedName;

public class ProfessionalSinglePojoItem {

	@SerializedName("birthdate")
	private String birthdate;

	@SerializedName("gender")
	private String gender;

	@SerializedName("lng")
	private String lng;

	@SerializedName("keywords")
	private String keywords;

	@SerializedName("is_my_favorite")
	private boolean isMyFavorite;

	@SerializedName("rating")
	private int rating;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("company_email")
	private String companyEmail;

	@SerializedName("user_profile_picture")
	private String userProfilePicture;

	@SerializedName("service_icon")
	private String serviceIcon;

	@SerializedName("user_code")
	private String userCode;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("service")
	private String service;

	@SerializedName("company_name")
	private String companyName;

	@SerializedName("location")
	private String location;

	@SerializedName("commerce_number")
	private String commerceNumber;

	@SerializedName("mobile_number")
	private String mobileNumber;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("lat")
	private String lat;

	@SerializedName("professional_status")
	private String professionalStatus;

	public void setBirthdate(String birthdate){
		this.birthdate = birthdate;
	}

	public String getBirthdate(){
		return birthdate;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setKeywords(String keywords){
		this.keywords = keywords;
	}

	public String getKeywords(){
		return keywords;
	}

	public void setIsMyFavorite(boolean isMyFavorite){
		this.isMyFavorite = isMyFavorite;
	}

	public boolean isIsMyFavorite(){
		return isMyFavorite;
	}

	public void setRating(int rating){
		this.rating = rating;
	}

	public int getRating(){
		return rating;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setCompanyEmail(String companyEmail){
		this.companyEmail = companyEmail;
	}

	public String getCompanyEmail(){
		return companyEmail;
	}

	public void setUserProfilePicture(String userProfilePicture){
		this.userProfilePicture = userProfilePicture;
	}

	public String getUserProfilePicture(){
		return userProfilePicture;
	}

	public void setServiceIcon(String serviceIcon){
		this.serviceIcon = serviceIcon;
	}

	public String getServiceIcon(){
		return serviceIcon;
	}

	public void setUserCode(String userCode){
		this.userCode = userCode;
	}

	public String getUserCode(){
		return userCode;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setService(String service){
		this.service = service;
	}

	public String getService(){
		return service;
	}

	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}

	public String getCompanyName(){
		return companyName;
	}

	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}

	public void setCommerceNumber(String commerceNumber){
		this.commerceNumber = commerceNumber;
	}

	public String getCommerceNumber(){
		return commerceNumber;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setLat(String lat){
		this.lat = lat;
	}

	public String getLat(){
		return lat;
	}

	public void setProfessionalStatus(String professionalStatus){
		this.professionalStatus = professionalStatus;
	}

	public String getProfessionalStatus(){
		return professionalStatus;
	}

	@Override
 	public String toString(){
		return 
			"ProfessionalSinglePojoItem{" +
			"birthdate = '" + birthdate + '\'' + 
			",gender = '" + gender + '\'' + 
			",lng = '" + lng + '\'' + 
			",keywords = '" + keywords + '\'' + 
			",is_my_favorite = '" + isMyFavorite + '\'' + 
			",rating = '" + rating + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",company_email = '" + companyEmail + '\'' + 
			",user_profile_picture = '" + userProfilePicture + '\'' + 
			",service_icon = '" + serviceIcon + '\'' + 
			",user_code = '" + userCode + '\'' + 
			",user_id = '" + userId + '\'' + 
			",service = '" + service + '\'' + 
			",company_name = '" + companyName + '\'' + 
			",location = '" + location + '\'' + 
			",commerce_number = '" + commerceNumber + '\'' + 
			",mobile_number = '" + mobileNumber + '\'' + 
			",first_name = '" + firstName + '\'' + 
			",lat = '" + lat + '\'' + 
			",professional_status = '" + professionalStatus + '\'' + 
			"}";
		}
}