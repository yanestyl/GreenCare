package com.example.diplom.retrofit.models;

import com.google.gson.annotations.SerializedName;

public class UserRegister {

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    public UserRegister(String phoneNumber, String password, String name) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.name = name;
    }
}
