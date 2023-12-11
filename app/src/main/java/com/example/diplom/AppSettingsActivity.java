package com.example.diplom;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class AppSettingsActivity extends AppCompatActivity {

    private SwitchCompat switchTheme;
    private boolean nightMode;
    private boolean isNotifications;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SwitchCompat switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        switchTheme = findViewById(R.id.switchTheme);
        switchNotifications = findViewById(R.id.switchNotifications);

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("nightMode", false);
        isNotifications = sharedPreferences.getBoolean("isNotifications", true);

        if (isNotifications) {
            switchNotifications.setChecked(true);
        }

        if (nightMode) {
            switchTheme.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switchTheme.setOnClickListener(v -> {
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("nightMode", false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("nightMode", true);
            }
            editor.apply();
        });



        switchNotifications.setOnClickListener(v -> {
            if (isNotifications) {
                editor = sharedPreferences.edit();
                editor.putBoolean("isNotifications", false);
            } else {
                editor = sharedPreferences.edit();
                editor.putBoolean("isNotifications", true);
            }
            editor.apply();
        });
    }
}
