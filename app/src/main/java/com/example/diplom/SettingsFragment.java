package com.example.diplom;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diplom.LoginActivity;
import com.example.diplom.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        ListView settingsListView = rootView.findViewById(R.id.settingsListView);

        // Создание списка элементов
        String[] settingsItems = {"Информация о пользователе", "Настройки приложения", "Помощь и обратная связь"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_settings_item, settingsItems);
        settingsListView.setAdapter(adapter);

        // Обработка кликов по элементам списка
        settingsListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = null;
            switch (position) {
                case 0:
                    intent = new Intent(getContext(), UserInfoActivity.class);
                    break;
                case 1:
                    intent = new Intent(getContext(), AppSettingsActivity.class);
                    break;
                case 2:
                    intent = new Intent(getContext(), HelpFeedbackActivity.class);
                    break;
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
