package com.example.diplom.retrofit.models;

import android.net.Uri;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Photo {
    private Uri uri;

    public Photo(Uri uri) {
        this.uri = uri;
    }

    public MultipartBody.Part toMultipartBodyPart() {
        File file = new File(uri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("photos", file.getName(), requestFile);
    }
}