package com.example.socialMediaApplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chat {


    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("receiver")
    @Expose
    private String receiver;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("seen")
    @Expose
    private boolean seen;

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public boolean getSeen() {
        return seen;
    }
    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
