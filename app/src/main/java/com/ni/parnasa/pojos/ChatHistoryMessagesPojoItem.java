package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;

public class ChatHistoryMessagesPojoItem {

    @SerializedName("is_read")
    private String isRead;

    @SerializedName("image")
    private String image;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("content")
    private String content;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("last_msg_id")
    private String messageId;

    private String dateHeader = "";

    public String getDateHeader() {
        return dateHeader;
    }

    public void setDateHeader(String dateHeaer) {
        this.dateHeader = dateHeaer;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return
                "ChatHistoryMessagesPojoItem{" +
                        "is_read = '" + isRead + '\'' +
                        ",image = '" + image + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",content = '" + content + '\'' +
                        "}";
    }
}