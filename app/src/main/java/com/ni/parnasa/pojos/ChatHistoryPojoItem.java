package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatHistoryPojoItem{

	@SerializedName("reciever_id")
	private String recieverId;

	@SerializedName("sender_id")
	private String senderId;

	@SerializedName("messages")
	private List<ChatHistoryMessagesPojoItem> chatHistoryMessagesPojo;

	public void setRecieverId(String recieverId){
		this.recieverId = recieverId;
	}

	public String getRecieverId(){
		return recieverId;
	}

	public void setSenderId(String senderId){
		this.senderId = senderId;
	}

	public String getSenderId(){
		return senderId;
	}

	public void setChatHistoryMessagesPojo(List<ChatHistoryMessagesPojoItem> chatHistoryMessagesPojo){
		this.chatHistoryMessagesPojo = chatHistoryMessagesPojo;
	}

	public List<ChatHistoryMessagesPojoItem> getChatHistoryMessagesPojo(){
		return chatHistoryMessagesPojo;
	}

	@Override
 	public String toString(){
		return 
			"ChatHistoryPojoItem{" + 
			"reciever_id = '" + recieverId + '\'' + 
			",sender_id = '" + senderId + '\'' + 
			",chatHistoryMessagesPojo = '" + chatHistoryMessagesPojo + '\'' + 
			"}";
		}
}