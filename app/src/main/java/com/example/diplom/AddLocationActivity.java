package com.example.diplom;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.example.diplom.retrofit.models.Photo;
import com.example.diplom.retrofit.models.RequestResponse;
import com.example.diplom.retrofit.models.RequestSend;
import com.google.android.gms.common.api.Api;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.runtime.image.ImageProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddLocationActivity extends AppCompatActivity implements InputListener {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    PlacemarkMapObject selectedGeo;

    private MapView mapView;
    private Button submitButton;

    private ArrayList<Uri> selectedPhotos;
    private String description;
    private Point selectedLocation;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mapView = findViewById(R.id.mapContainer);

        submitButton = findViewById(R.id.submitButton);

        selectedGeo = mapView.getMapWindow().getMap().getMapObjects().addPlacemark();

        // Получение данных из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            selectedPhotos = intent.getParcelableArrayListExtra("selectedPhotos");
            description = intent.getStringExtra("description");
        }

        MapKit mapKit = MapKitFactory.getInstance();

        mapKit.createLocationManager().requestSingleUpdate(new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                double lat = location.getPosition().getLatitude();
                double lng = location.getPosition().getLongitude();
                mapView.getMapWindow().getMap().move(
                        new CameraPosition(new Point(lat, lng), 17.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 1),
                        null);

                selectedGeo.setGeometry(new Point(lat, lng));
                selectedGeo.setIcon(ImageProvider.fromResource(AddLocationActivity.this, R.drawable.search_result),
                        new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                                .setRotationType(RotationType.ROTATE)
                                .setZIndex(1f)
                                .setScale(0.5f));
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {

            }
        });


        requestLocationPermission();


        mapView.getMapWindow().getMap().addInputListener(this);

        submitButton.setOnClickListener(v -> {
            selectedLocation = selectedGeo.getGeometry();

            appPreferences = new AppPreferences(this);


            RequestSend request = new RequestSend(description, selectedLocation.getLongitude(), selectedLocation.getLatitude());

            ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
            Call<ResponseBody> call = apiService.sendRequest(request);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String bodyString;
                    try {
                        bodyString = new String(response.body().bytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        uploadFiles(bodyString);
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
                    Toast toast = Toast.makeText(AddLocationActivity.this, "Чет с сетью", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        });

    }

    private void openNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void uploadFiles(String requestIdStr) {
        // Добавьте этот код в ваш активити перед тем, как вы будете пытаться открыть файл
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запросите его
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        String isMainStr = "1";
        for (Uri photoUri: selectedPhotos) {

            String filePath = getRealPathFromURI(photoUri);

            if (filePath == null) {
                filePath = Objects.requireNonNull(photoUri.getPath());
            }

            File file = new File(filePath);

            RequestBody requestIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), requestIdStr);
            RequestBody isMainRequestBody = RequestBody.create(MediaType.parse("text/plain"), isMainStr);
            isMainStr = "0";

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

            ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
            Call<ResponseBody> call1 = apiService.uploadImage(imagePart, requestIdRequestBody, isMainRequestBody);
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
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
                    Toast toast = Toast.makeText(AddLocationActivity.this, "Чет с сетью", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        openNextActivity();
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


    @Override
    public void onMapTap(@NonNull Map map, @NonNull Point point) {
        selectedGeo.setGeometry(point);
        Log.d("ЛОКАЦИЯ", selectedGeo.getGeometry().getLatitude() + "");
        Log.d("ЛОКАЦИЯ", selectedGeo.getGeometry().getLongitude() + "");



    }

    @Override
    public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, выполните вашу операцию
            } else {
                // Разрешение не предоставлено, обработайте эту ситуацию
            }
        }
    }


}
