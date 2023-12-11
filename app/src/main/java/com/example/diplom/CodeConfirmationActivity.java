package com.example.diplom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.diplom.retrofit.ApiClient;
import com.example.diplom.retrofit.ApiService;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.tinkoff.decoro.Mask;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class CodeConfirmationActivity extends AppCompatActivity {

    private EditText codeInput;
    private TextView resendView;
    private TextView counterView;
    private TextView phoneTextView;
    private TextView changeNumber;

    SharedPreferences settings;

    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_TIME = 30000; // 30 секунд
    private boolean timerRunning;

    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_confirmation);

        settings = getSharedPreferences("Account", MODE_PRIVATE);


        codeInput = findViewById(R.id.code_input);
        resendView = findViewById(R.id.resend_view);
        phoneTextView = findViewById(R.id.phone_text_view);
        changeNumber = findViewById(R.id.change_number_view);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("PHONE_NUMBER")) {
            phoneNumber = intent.getStringExtra("PHONE_NUMBER");

            String textPhoneTextView = String.format("На номер %s отправлено смс-сообщение с кодом подтверждения. Введите код в поле ниже.", phoneNumber);

            // Отобразите номер телефона в CodeActivity
            phoneTextView.setText(textPhoneTextView);
        } else {
            phoneTextView.setText("Какая-то ошибка");
        }
        // Начальная настройка UI
        updateUI();

        startTimer();

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });

        resendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerRunning) {
                    startTimer();
                }
            }
        });

        Mask mask = new MaskImpl(new Slot[]{
                PredefinedSlots.digit(),                                          // слот для цифры
                PredefinedSlots.digit(),                                          // слот для цифры
                PredefinedSlots.digit(),                                          // слот для цифры
                PredefinedSlots.hardcodedSlot(' '), // декоративный hardcoded слот
                PredefinedSlots.digit(),                                          // слот для цифры
                PredefinedSlots.digit(),                                          // слот для цифры
                PredefinedSlots.digit()                                          // слот для цифры
        }, true);
        mask.setForbidInputWhenFilled(true);
        FormatWatcher watcher = new MaskFormatWatcher((MaskImpl) mask);

        watcher.installOnAndFill(codeInput);

        if (codeInput.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 7) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(codeInput.getWindowToken(), 0);
                    // вызываю функцию проверки кода
                    checkCode(s);
                }
            }
        });

    }

    private void startLoginActivity() {
        Intent intent = new Intent(CodeConfirmationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkCode(Editable s) {
        //checkCodeTrust();
        String token = "same_token";
        saveToken(token);

        String formattedPhoneNumber = "+7" + phoneNumber.replaceAll("[^0-9]", "").substring(1);

        // Выполняем асинхронную задачу для POST-запроса
        //new CreateUserTask().execute(formattedPhoneNumber);

        startHomeActivity();

    }

    private void saveToken(String token) {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("TOKEN", token);
        prefEditor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(CodeConfirmationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI() {
        if (timerRunning) {
            resendView.setAlpha(0.3f);
        } else {
            resendView.setAlpha(1.0f);
            resendView.setText("Отправить повторно");
        }
    }


    private void startTimer() {
        resendView.setAlpha(0.3f);
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                updateUI();
            }
        }.start();

        timerRunning = true;
        updateUI();
    }

    private void updateTimer(int secondsLeft) {
        resendView.setText(getString(R.string.timer_format, secondsLeft));
    }

//    private class CreateUserTask extends AsyncTask<String, Void, Integer> {
//
//        @Override
//        protected Integer doInBackground(String... params) {
//            if (params.length == 0) {
//                return 0;
//            }
//
//            String phoneNumber = params[0];
//
//            try {
//                // Создаем RequestBody с телефонным номером
//                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);
//
//                // Выполняем асинхронный POST-запрос с Retrofit
//                ApiService apiService = ApiClient.create();
//                Call<ResponseBody> call = apiService.createUserIfNotExists(requestBody);
//                Response<ResponseBody> response = call.execute();
//
//                if (response.isSuccessful()) {
//                    // Обработка успешного запроса, если необходимо
//                    return 1;
//                } else {
//                    // Обработка неуспешного запроса
//                    return 0;
//                }
//            } catch (IOException e) {
//                // Обработка ошибок, например, сетевых проблем
//                e.printStackTrace();
//                return 0;
//            }
//        }
//    }

}

