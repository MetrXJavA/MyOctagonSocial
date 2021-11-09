package com.prometrx.myinstagramclone.Model;

public class UsersChats {

    private String userId, userImageUrl, userUsername, userLastMessage;


    public UsersChats(String userId, String userImageUrl, String userUsername, String userLastMessage) {
        this.userId = userId;
        this.userImageUrl = userImageUrl;
        this.userUsername = userUsername;
        this.userLastMessage = userLastMessage;
    }

    public String getUserLastMessage() {
        return userLastMessage;
    }

    public void setUserLastMessage(String userLastMessage) {
        this.userLastMessage = userLastMessage;
    }

    public UsersChats() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

}
