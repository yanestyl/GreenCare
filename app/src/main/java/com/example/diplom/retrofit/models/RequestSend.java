package com.example.diplom.retrofit.models;


import com.google.gson.annotations.SerializedName;
import com.yandex.mapkit.geometry.Point;

import java.io.File;
import java.util.List;

public class RequestSend {
    private String description;

    private double lng;
    private double lat;

    public RequestSend(String description, double lng, double lat) {
        this.description = description;
        this.lng = lng;
        this.lat = lat;
    }
}
