package com.developer.mychatapp.Models;

public class UserMessage {
    String sender_id;
    String reciver_id;
    String sender;
    String reciver;
    String message;
    long time;
    boolean isseen;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public UserMessage() {
    }

    public UserMessage(String id,String sender_id, String reciver_id, String sender, String reciver, String message, long time,boolean isseen) {
        this.sender_id = sender_id;
        this.reciver_id = reciver_id;
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
        this.time = time;
        this.isseen = isseen;
        this.id =id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReciver_id() {
        return reciver_id;
    }

    public void setReciver_id(String reciver_id) {
        this.reciver_id = reciver_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
