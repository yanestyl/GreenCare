package com.example.diplom;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.diplom.R;
import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private AppPreferences appPreferences;

    TextView textViewProcessedRequests;
    Button btnFAQ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        textViewProcessedRequests = rootView.findViewById(R.id.textViewProcessedRequests);

        appPreferences = new AppPreferences(getContext());

        getRequestsCount();



        btnFAQ = rootView.findViewById(R.id.btnFAQ);

        btnFAQ.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FAQActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        rootView.findViewById(R.id.btnLeaveRequest).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddPhotosActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void getRequestsCount() {
        ApiService apiService = ApiClient.create(appPreferences.getAuthToken());
        Call<ResponseBody> call = apiService.getRequestsCount();
        call.enqueue(new retrofit2.Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного запроса
                    try {
                        // Получение тела ответа в виде строки
                        String responseBodyString = response.body().string();
                        int requestsCount = Integer.parseInt(responseBodyString);

                        textViewProcessedRequests.setText(getString(R.string.applications_format, requestsCount));


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getContext(), "Ошибка обработки ответа", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    // Обработка неуспешного запроса
                    Toast toast = Toast.makeText(getContext(), "Нет доступа", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Обработка ошибок, например, сетевых проблем
                t.printStackTrace();
                Toast toast = Toast.makeText(getContext(), "Ошибка сети", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

}
