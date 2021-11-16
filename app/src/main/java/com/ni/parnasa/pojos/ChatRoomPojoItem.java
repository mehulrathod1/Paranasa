package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ChatRoomPojoItem{

	@SerializedName("lastMsgDetail")
	private ChatRoomPojoLastMsgDetail chatRoomPojoLastMsgDetail;

	@SerializedName("room_create_date")
	private String roomCreateDate;

	@SerializedName("sender")
	private ChatRoomPojoSender chatRoomPojoSender;

	@SerializedName("reciever")
	private ChatRoomPojoReceiver chatRoomPojoReceiver;

	@SerializedName("chat_id")
	private int chatId;

	public void setChatRoomPojoLastMsgDetail(ChatRoomPojoLastMsgDetail chatRoomPojoLastMsgDetail){
		this.chatRoomPojoLastMsgDetail = chatRoomPojoLastMsgDetail;
	}

	public ChatRoomPojoLastMsgDetail getChatRoomPojoLastMsgDetail(){
		return chatRoomPojoLastMsgDetail;
	}

	public void setRoomCreateDate(String roomCreateDate){
		this.roomCreateDate = roomCreateDate;
	}

	public String getRoomCreateDate(){
		return roomCreateDate;
	}

	public void setChatRoomPojoSender(ChatRoomPojoSender chatRoomPojoSender){
		this.chatRoomPojoSender = chatRoomPojoSender;
	}

	public ChatRoomPojoSender getChatRoomPojoSender(){
		return chatRoomPojoSender;
	}

	public void setChatRoomPojoReceiver(ChatRoomPojoReceiver chatRoomPojoReceiver){
		this.chatRoomPojoReceiver = chatRoomPojoReceiver;
	}

	public ChatRoomPojoReceiver getChatRoomPojoReceiver(){
		return chatRoomPojoReceiver;
	}

	public void setChatRoomId(int chatId){
		this.chatId = chatId;
	}

	public int getChatRoomId(){
		return chatId;
	}

	@Override
 	public String toString(){
		return 
			"ChatRoomPojoItem{" + 
			"chatRoomPojoLastMsgDetail = '" + chatRoomPojoLastMsgDetail + '\'' + 
			",room_create_date = '" + roomCreateDate + '\'' + 
			",chatRoomPojoSender = '" + chatRoomPojoSender + '\'' + 
			",chatRoomPojoReceiver = '" + chatRoomPojoReceiver + '\'' + 
			",chat_id = '" + chatId + '\'' + 
			"}";
		}
}