package com.example.diplom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplom.preferences.AppPreferences;
import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;
import com.example.diplom.retrofit.models.UserLogin;
import com.example.diplom.retrofit.models.UserRegister;
import com.google.android.material.button.MaterialButton;
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


public class RegisterActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    TextView goLoginBtn;
    EditText phoneInput;
    EditText passwordInput;
    EditText nameInput;
    Button registerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        appPreferences = new AppPreferences(this);

        goLoginBtn = findViewById(R.id.go_login_btn);

        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        nameInput = findViewById(R.id.name_input);

        registerBtn = findViewById(R.id.register_btn);

        Mask mask = new MaskImpl(PredefinedSlots.RUS_PHONE_NUMBER, true);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher watcher = new MaskFormatWatcher((MaskImpl) mask);
        watcher.installOnAndFill(phoneInput);

        goLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        registerBtn.setOnClickListener(v -> {
            String phoneNumber = phoneInput.getText().toString();
            String formattedPhoneNumber = "+7" + phoneNumber.replaceAll("[^0-9]", "").substring(1);
            String password = passwordInput.getText().toString();
            String name = nameInput.getText().toString();

            ApiService apiService = ApiClient.create();
            Call<ResponseBody> call = apiService.register(createRegisterRequestBody(formattedPhoneNumber, password, name));
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
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.putExtra("register", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(RegisterActivity.this, "Ошибка обработки ответа", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        // Обработка неуспешного запроса
                        Toast toast = Toast.makeText(RegisterActivity.this, "Номер уже зарегистрирован", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Обработка ошибок, например, сетевых проблем
                    t.printStackTrace();
                    Toast toast = Toast.makeText(RegisterActivity.this, "Ошибка сети", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        });


    }

    private RequestBody createRegisterRequestBody(String phoneNumber, String password, String name) {
        UserRegister userRegister = new UserRegister(phoneNumber, password, name);
        return RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(userRegister));
    }


}