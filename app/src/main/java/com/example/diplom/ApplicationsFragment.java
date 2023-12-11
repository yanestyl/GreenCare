package com.example.diplom;

import android.app.Application;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diplom.databinding.ActivityHomeBinding;
import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.example.diplom.retrofit.models.UserRequestsList;
import com.example.diplom.retrofit.models.UserRequestsListWithImages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicationsFragment extends Fragment {

    ApplicationsListData listData;

    ApplicationsListAdapter listAdapter;

    ListView applicationsList;
    List<ApplicationsListData> dataArrayList = new ArrayList<>();
    private AppPreferences appPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_applications, container, false);

        appPreferences = new AppPreferences(getContext());

        applicationsList = rootView.findViewById(R.id.listView);

        getUserApplications();



        return rootView;
    }

    List<UserRequestsList> userRequestsList;

    private void getUserApplications() {
        ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
        Call<List<UserRequestsList>> call = apiService.getUserRequests();
        call.enqueue(new Callback<List<UserRequestsList>>() {
            @Override
            public void onResponse(Call<List<UserRequestsList>> call, Response<List<UserRequestsList>> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного запроса
                    userRequestsList = response.body();
                    uploadImages(userRequestsList);
                    Log.d("ОТПРАВКА", "ВСЕ ХОРОШО");
                } else {
                    // Обработка неуспешного запроса
                    Log.d("ОТПРАВКА", "ВСЕ ПЛОХО");
                }

            }

            @Override
            public void onFailure(Call<List<UserRequestsList>> call, Throwable t) {
                // Обработка ошибок, например, сетевых проблем
                // Обработка неуспешного запроса
                Toast toast = Toast.makeText(getContext(), "Чет с сетью", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // заводим счетчик. Чтоб не выполнялось сразу
    private int requestsCounter;
    private void uploadImages(List<UserRequestsList> userRequestsList) {
        List<Long> photosIdList = userRequestsList.stream()
                .map(UserRequestsList::getPhoto_id)
                .collect(Collectors.toList());

        List<byte[]> imageDataList = new ArrayList<>();

        // Инициализируем счетчик
        requestsCounter = photosIdList.size();

        ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
        for (Long photoId : photosIdList) {
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
                            fillDataArray(userRequestsList, imageDataList);
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
                    Toast toast = Toast.makeText(getContext(), "Чет с сетью", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

    private void fillDataArray(List<UserRequestsList> userRequestsList, List<byte[]> imageDataList) {
        for (int i = 0; i < userRequestsList.size(); i++) {
            byte[] imageData = imageDataList.get(i);
            UserRequestsList userRequest = userRequestsList.get(i);
            listData = new ApplicationsListData(
                    imageData,
                    userRequest.getDescription(),
                    userRequest.getStatus(),
                    userRequest.getRequest_id()
            );
            dataArrayList.add(listData);
        }
        startAdapter();
    }

    private void startAdapter() {
        listAdapter = new ApplicationsListAdapter(getContext(), (ArrayList<ApplicationsListData>) dataArrayList);
        applicationsList.setAdapter(listAdapter);
        applicationsList.setClickable(true);

        applicationsList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), ApplicationActivity.class);
            intent.putExtra("requestId", dataArrayList.get(position).requestId);
            startActivity(intent);
        });
    }

}
