package com.example.diplom.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    public UserLogin(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
