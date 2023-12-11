package com.example.diplom;

public class ApplicationsListData {

    String description;
    int status;
    Long requestId;

    byte[] imageData;


    public ApplicationsListData(byte[] imageData, String description, int status, Long requestId) {
        this.imageData = imageData;
        this.description = description;
        this.status = status;
        this.requestId = requestId;
    }
}
