package com.example.diplom.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREFS_NAME = "PREFERENCES";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";

    private final SharedPreferences preferences;

    public AppPreferences(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public void deleteAuthToken() {
        preferences.edit().remove(KEY_TOKEN).apply();
    }

    public void deleteRefreshToken() {
        preferences.edit().remove(KEY_REFRESH_TOKEN).apply();
    }

    public String getAuthToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void saveRefreshToken(String token) {
        preferences.edit().putString(KEY_REFRESH_TOKEN, token).apply();
    }

    public String getRefreshToken() {
        return preferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public Object getAll() {
        return preferences.getAll();
    }

}
