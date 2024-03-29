package com.apkdv.leanote.model;


import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;

import com.apkdv.leanote.service.AccountService;

public class User extends BaseResponse {

    @SerializedName("UserId")
    String userId = "";
    @SerializedName("Username")
    String userName = "";
    @Column(name = "email")
    @SerializedName("Email")
    String email = "";
    @SerializedName("Verified")
    boolean isVerified;
    @SerializedName("Logo")
    String avatar = "";

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getAvatar() {
        Account current = AccountService.getCurrent();
        String host = current.getHost();
        if(host.equals("https://leanote.com")){
            return avatar;}
        else
        {return host+avatar;}
    }
}
