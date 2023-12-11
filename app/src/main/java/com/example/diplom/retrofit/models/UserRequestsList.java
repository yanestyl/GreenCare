package com.example.diplom.retrofit.models;

public class UserRequestsList {
    private Long request_id;
    private String description;
    private Long photo_id;
    private int status;

    public Long getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Long request_id) {
        this.request_id = request_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(Long photo_id) {
        this.photo_id = photo_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
