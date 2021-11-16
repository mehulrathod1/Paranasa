package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;


public class ChatRoomPojoSender{

	@SerializedName("user_id")
	private String userId;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("logo_image")
	private String logoImage;

	@SerializedName("first_name")
	private String firstName;

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
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
			"ChatRoomPojoSender{" + 
			"user_id = '" + userId + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",logo_image = '" + logoImage + '\'' + 
			",first_name = '" + firstName + '\'' + 
			"}";
		}
}