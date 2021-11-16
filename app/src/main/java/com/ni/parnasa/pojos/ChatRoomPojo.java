package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ChatRoomPojo{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private ChatRoomPojoItem chatRoomPojoItem;

	@SerializedName("status")
	private String status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setChatRoomPojoItem(ChatRoomPojoItem chatRoomPojoItem){
		this.chatRoomPojoItem = chatRoomPojoItem;
	}

	public ChatRoomPojoItem getChatRoomPojoItem(){
		return chatRoomPojoItem;
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
			"ChatRoomPojo{" + 
			"message = '" + message + '\'' + 
			",chatRoomPojoItem = '" + chatRoomPojoItem + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}