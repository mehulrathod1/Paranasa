package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;


public class ChatHistoryPojo {

    @SerializedName("data")
    private ChatHistoryPojoItem chatHistoryPojoItem;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public void setChatHistoryPojoItem(ChatHistoryPojoItem chatHistoryPojoItem) {
        this.chatHistoryPojoItem = chatHistoryPojoItem;
    }

    public ChatHistoryPojoItem getChatHistoryPojoItem() {
        return chatHistoryPojoItem;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "ChatHistoryPojo{" +
                        "chatHistoryPojoItem = '" + chatHistoryPojoItem + '\'' +
                        ",message = '" + message + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}