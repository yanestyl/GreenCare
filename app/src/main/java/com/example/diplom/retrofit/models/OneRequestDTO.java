package com.example.diplom.retrofit.models;

import java.util.List;

public class OneRequestDTO {
    public OneRequestDTO(int status, List<Long> photoIdList, String description, double lng, double lat) {
        this.status = status;
        this.photoIdList = photoIdList;
        this.description = description;
        this.lng = lng;
        this.lat = lat;
    }

    private int status;
    private List<Long> photoIdList;
    private String description;
    private double lng;
    private double lat;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Long> getPhotoIdList() {
        return photoIdList;
    }

    public void setPhotoIdList(List<Long> photoIdList) {
        this.photoIdList = photoIdList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "OneRequestDTO{" +
                "status=" + status +
                ", photoIdList=" + photoIdList +
                ", description='" + description + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
