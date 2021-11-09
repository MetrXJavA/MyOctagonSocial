package com.prometrx.myinstagramclone.Model;

import android.net.Uri;

public class Posts {

    private String id,comment,username,profileImageUrl;
    private String imageUrl, docName;

    public Posts(String id, String comment, String username, String profileImageUrl, String imageUrl, String docName) {
        this.id = id;
        this.comment = comment;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.imageUrl = imageUrl;
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
