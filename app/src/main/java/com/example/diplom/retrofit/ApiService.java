package com.example.diplom.retrofit;

import android.graphics.Point;

import com.example.diplom.retrofit.models.OneRequestDTO;
import com.example.diplom.retrofit.models.RequestResponse;
import com.example.diplom.retrofit.models.RequestSend;
import com.example.diplom.retrofit.models.UserRequestsList;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/api/v1/location-issues/requests-count")
    Call<ResponseBody> getRequestsCount();

    @POST("/api/v1/auth/signin")
    Call<ResponseBody> login(@Body RequestBody requestBody);

    @POST("/api/v1/auth/signup")
    Call<ResponseBody> register(@Body RequestBody requestBody);

    @POST("/auth/refresh")
    Call<ResponseBody> refresh(@Body RequestBody requestBody);

    @POST("/api/v1/requests/create")
    Call<ResponseBody> sendRequest(@Body RequestSend requestSend);

    @GET("/api/v1/requests/getUserRequests")
    Call<List<UserRequestsList>> getUserRequests();

    @GET("/api/v1/requests/getOneUserRequest/{requestId}")
    Call<OneRequestDTO> getOneUserRequest(@Path("requestId") Long requestId);

    @GET("/api/v1/photos/{photoId}")
    Call<ResponseBody> getPhotoById(@Path("photoId") Long photoId);

    @Multipart
    @POST("/api/v1/photos/add")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part part, @Part("request_id") RequestBody requestId, @Part("is_main") RequestBody isMain);


}
