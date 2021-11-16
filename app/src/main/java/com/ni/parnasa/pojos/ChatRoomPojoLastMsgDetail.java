package com.ni.parnasa.pojos;

import com.google.gson.annotations.SerializedName;


public class ChatRoomPojoLastMsgDetail {

    @SerializedName("last_msg_user")
    private String lastMsgUser;

    @SerializedName("last_msg_time")
    private String lastMsgTime;

    @SerializedName("last_msg_id")
    private String lastMsgId;

    public void setLastMsgUser(String lastMsgUser) {
        this.lastMsgUser = lastMsgUser;
    }

    public String getLastMsgUser() {
        return lastMsgUser;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgId(String lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public String getLastMsgId() {
        return lastMsgId;
    }

    @Override
    public String toString() {
        return
                "ChatRoomPojoLastMsgDetail{" +
                        "last_msg_user = '" + lastMsgUser + '\'' +
                        ",last_msg_time = '" + lastMsgTime + '\'' +
                        ",last_msg_id = '" + lastMsgId + '\'' +
                        "}";
    }
}