package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ChatUserDetailPojoItem{

	@SerializedName("last_msg")
	private String lastMsg;

	@SerializedName("last_msg_time")
	private String lastMsgTime;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("profile")
	private String profile;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("chat_id")
	private String chatId;

	@SerializedName("is_favorite")
	private boolean favorites;

	@SerializedName("mobile_no")
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public boolean isFavorites() {
		return favorites;
	}

	public void setFavorites(boolean favorites) {
		this.favorites = favorites;
	}

	public void setLastMsg(String lastMsg){
		this.lastMsg = lastMsg;
	}

	public String getLastMsg(){
		return lastMsg;
	}

	public void setLastMsgTime(String lastMsgTime){
		this.lastMsgTime = lastMsgTime;
	}

	public String getLastMsgTime(){
		return lastMsgTime;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setProfile(String profile){
		this.profile = profile;
	}

	public String getProfile(){
		return profile;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setChatId(String chatId){
		this.chatId = chatId;
	}

	public String getChatId(){
		return chatId;
	}

	@Override
 	public String toString(){
		return 
			"ChatUserDetailPojoItem{" + 
			"last_msg = '" + lastMsg + '\'' + 
			",last_msg_time = '" + lastMsgTime + '\'' + 
			",user_id = '" + userId + '\'' + 
			",profile = '" + profile + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",first_name = '" + firstName + '\'' + 
			",chat_id = '" + chatId + '\'' + 
			"}";
		}
}