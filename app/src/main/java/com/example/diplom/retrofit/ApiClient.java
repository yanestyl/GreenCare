package com.example.diplom.retrofit;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://192.168.1.102:8080";


    // Создание экземпляра сервиса
    public static ApiService create(String authToken) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Добавление Interceptor для логгирования запросов
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        httpClient.addInterceptor(loggingInterceptor);


            Log.d("TOKEN", authToken);
            // Добавление Interceptor для вставки токена в заголовок
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + authToken)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(ApiService.class);
    }

    public static ApiService create() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // Добавление Interceptor для логгирования запросов
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(ApiService.class);
    }
}
