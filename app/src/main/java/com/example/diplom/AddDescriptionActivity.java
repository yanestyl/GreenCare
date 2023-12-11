package com.example.diplom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddDescriptionActivity extends AppCompatActivity {

    private EditText descriptionEditText;
    private Button nextButton;

    private ArrayList<Uri> selectedPhotos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        descriptionEditText = findViewById(R.id.descriptionEditText);
        nextButton = findViewById(R.id.nextButton);

        // Получение данных из предыдущей активности
        Intent intent = getIntent();
        if (intent != null) {
            selectedPhotos = intent.getParcelableArrayListExtra("selectedPhotos");
        }

        nextButton.setOnClickListener(v -> goToNextActivity());
    }

    private void goToNextActivity() {
        String description = descriptionEditText.getText().toString().trim();

        Intent nextIntent = new Intent(AddDescriptionActivity.this, AddLocationActivity.class);
        nextIntent.putParcelableArrayListExtra("selectedPhotos", selectedPhotos);
        nextIntent.putExtra("description", description);
        startActivity(nextIntent);
    }
}
