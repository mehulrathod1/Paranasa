package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ChatUserPojo{

	@SerializedName("data")
	private ChatUserItemPojo chatUserItemPojo;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	public void setChatUserItemPojo(ChatUserItemPojo chatUserItemPojo){
		this.chatUserItemPojo = chatUserItemPojo;
	}

	public ChatUserItemPojo getChatUserItemPojo(){
		return chatUserItemPojo;
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
			"ChatUserPojo{" + 
			"chatUserItemPojo = '" + chatUserItemPojo + '\'' + 
			",message = '" + message + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}