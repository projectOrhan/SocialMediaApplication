package com.example.socialMediaApplication.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileList {

    @SerializedName("profileDetail")
    @Expose
    private List<ProfileDetail> profileDetail = null;

    public List<ProfileDetail> getProfileDetail() {
        return profileDetail;
    }

    public void setProfileDetail(List<ProfileDetail> profileDetail) {
        this.profileDetail = profileDetail;
    }

}