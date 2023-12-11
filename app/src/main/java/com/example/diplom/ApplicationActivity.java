package com.example.diplom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.example.diplom.retrofit.models.OneRequestDTO;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicationActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    TextView statusView;
    TextView descriptionView;
    MapView mapView;
    ViewPager2 viewPager;
    ImageButton backBtn;

    OneRequestDTO oneRequestDTO;
    List<byte[]> imageDataList = new ArrayList<>();
    AppPreferences appPreferences;
    Long requestId;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);

        appPreferences = new AppPreferences(this);

        statusView = findViewById(R.id.applicationStatus);
        descriptionView = findViewById(R.id.applicationDescription);
        viewPager = findViewById(R.id.photoViewPager);
        mapView = findViewById(R.id.mapContainer);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        requestLocationPermission();



        Intent intent = this.getIntent();
        if (intent != null) {
            requestId = intent.getLongExtra("requestId", 0);
        }

        loadingData();

        mapView.getMapWindow().getMap().setScrollGesturesEnabled(false);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void loadingData() {
        ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
        Call<OneRequestDTO> call = apiService.getOneUserRequest(requestId);
        call.enqueue(new Callback<OneRequestDTO>() {
            @Override
            public void onResponse(Call<OneRequestDTO> call, Response<OneRequestDTO> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного запроса
                    oneRequestDTO = response.body();
                    System.out.println(oneRequestDTO.toString());
                    loadingPhotos(oneRequestDTO.getPhotoIdList());
                    Log.d("ОТПРАВКА", "ВСЕ ХОРОШО");
                } else {
                    // Обработка несупешного запроса
                    Log.d("ОТПРАВКА", "ВСЕ ПЛОХО");
                }
            }

            @Override
            public void onFailure(Call<OneRequestDTO> call, Throwable t) {
                // Обработка ошибок, например, сетевых проблем
                // Обработка неуспешного запроса
                Toast toast = Toast.makeText(getApplicationContext(), "Чет с сетью", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // заводим счетчик. Чтоб не выполнялось сразу
    private int requestsCounter;
    private void loadingPhotos(List<Long> photoIdList) {
        // Инициализируем счетчик
        requestsCounter = photoIdList.size();
        ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
        for (Long photoId: photoIdList) {
            Call<ResponseBody> call = apiService.getPhotoById(photoId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        try {
                            imageDataList.add(response.body().bytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // Уменьшение счетчика после каждого запроса
                        requestsCounter--;

                        // Вызов fillDataArray, если все запросы завершены
                        if (requestsCounter == 0) {
                            fillData();
                        }

                        Log.d("ОТПРАВКА", "ВСЕ ХОРОШО");
                    } else {
                        // Обработка неуспешного запроса
                        Log.d("ОТПРАВКА", "ВСЕ ПЛОХО");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Обработка ошибок, например, сетевых проблем
                    // Обработка неуспешного запроса
                    Toast toast = Toast.makeText(getApplicationContext(), "Чет с сетью", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    private void fillData() {

        // Заполняем статус
        if (oneRequestDTO.getStatus() == 0) {
            statusView.setText("В процессе");
            statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_watch,0, 0, 0);
        } else {
            statusView.setText("Исправлено");
            statusView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_status_good,0, 0, 0);
        }

        // Заполняем описание
        descriptionView.setText(oneRequestDTO.getDescription());


        double lat = oneRequestDTO.getLat();
        double lng = oneRequestDTO.getLng();
        // Ставим точку на карте
        mapView.getMapWindow().getMap().move(
                new CameraPosition(new Point(lat, lng), 16.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);
        PlacemarkMapObject getPoint = mapView.getMapWindow().getMap().getMapObjects().addPlacemark();
        getPoint.setIcon(ImageProvider.fromResource(this, R.drawable.search_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f));
        getPoint.setGeometry(new Point(lat, lng));

        // Ставим картинки
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(imageDataList);
        viewPager.setAdapter(photoPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //updatePhotoCount(position, imageDataList.size());
            }
        });

    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }


}
