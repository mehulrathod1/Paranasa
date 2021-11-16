package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserItemPojo{

	@SerializedName("user_detail")
	private List<ChatUserDetailPojoItem> chatUserDetailPojo;

	public void setChatUserDetailPojo(List<ChatUserDetailPojoItem> chatUserDetailPojo){
		this.chatUserDetailPojo = chatUserDetailPojo;
	}

	public List<ChatUserDetailPojoItem> getChatUserDetailPojo(){
		return chatUserDetailPojo;
	}

	@Override
 	public String toString(){
		return 
			"ChatUserItemPojo{" + 
			"chatUserDetailPojo = '" + chatUserDetailPojo + '\'' + 
			"}";
		}
}