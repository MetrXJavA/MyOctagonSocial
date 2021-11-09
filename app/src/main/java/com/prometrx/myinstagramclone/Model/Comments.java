package com.prometrx.myinstagramclone.Model;

public class Comments {

    private String comment, userId, profileImageUrl, username;

    public Comments(String comment, String userId, String profileImageUrl, String username) {
        this.comment = comment;
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
    }

    public Comments() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
