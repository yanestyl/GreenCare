package com.example.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.example.diplom.retrofit.models.UserLogin;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;


public class LoginActivity extends AppCompatActivity {

    private AppPreferences appPreferences;

    TextView goRegisterBtn;
    EditText phoneInput;
    EditText passwordInput;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appPreferences = new AppPreferences(this);

        goRegisterBtn = findViewById(R.id.go_register_btn);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);

        Mask mask = new MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher watcher = new MaskFormatWatcher((MaskImpl) mask);
        watcher.installOnAndFill(phoneInput);


        goRegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            String phoneNumber = phoneInput.getText().toString();
            String formattedPhoneNumber = "+7" + phoneNumber.replaceAll("[^0-9]", "").substring(1);
            String password = passwordInput.getText().toString();

            ApiService apiService = ApiClient.create();
            Call<ResponseBody> call = apiService.login(createLoginRequestBody(formattedPhoneNumber, password));
            call.enqueue(new retrofit2.Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        try {
                            // Получение тела ответа в виде строки
                            String responseBody = response.body().string();
                            // Распарсивание JSON-объекта
                            JSONObject jsonObject = new JSONObject(responseBody);
                            // Извлечение токенов
                            String authToken = jsonObject.getString("token");
                            String refreshToken = jsonObject.getString("refreshToken");
                            // Сохранение токенов в SharedPreferences
                            appPreferences.saveAuthToken(authToken);
                            appPreferences.saveRefreshToken(refreshToken);

                            Log.d("TOKENS", appPreferences.getAll().toString());

                            // Откройте HomeActivity
                            openHomeActivity();

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(LoginActivity.this, "Ошибка обработки ответа", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 160);
                            toast.show();
                        }
                    } else {
                        // Обработка неуспешного запроса
                        Toast toast = Toast.makeText(LoginActivity.this, "Неверные данные", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 160);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Обработка ошибок, например, сетевых проблем
                    t.printStackTrace();
                    Toast toast = Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 160);
                    toast.show();
                }
            });
        });

    }

    private RequestBody createLoginRequestBody(String phoneNumber, String password) {
        UserLogin userLogin = new UserLogin(phoneNumber, password);
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(userLogin));
    }

    private void openHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}