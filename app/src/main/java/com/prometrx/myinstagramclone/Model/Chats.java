package com.prometrx.myinstagramclone.Model;

public class Chats {

    private String sender, receiver, messageText, profileImageUrl;

    public Chats(String sender, String receiver, String messageText, String profileImageUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageText = messageText;
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Chats() {

    }

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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
