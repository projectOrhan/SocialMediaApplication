package com.example.socialMediaApplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProfileDetail {

    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("onlineStatus")
    @Expose
    private String onlineStatus;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("likes")
    @Expose
    private String likes;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) { this.uid = uid; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) { this.age = age; }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername(){ return username; }

    public void setUsername(String username){
        this.username = username;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }


}