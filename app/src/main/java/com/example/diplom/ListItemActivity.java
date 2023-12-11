package com.example.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ListItemActivity extends AppCompatActivity {
    private ImageView itemImage;
    private TextView itemDesc;
    private TextView itemStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
//
//        itemDesc = findViewById(R.id.listDescription);
//        itemStatus = findViewById(R.id.listStatus);
//        itemImage = findViewById(R.id.listImage);
//
//        Intent intent = getIntent();
//        ApplicationsListData application = intent.getParcelableExtra("application");
//
//
//        itemImage.setImageResource(R.drawable.bg_login);
//
//        itemDesc.setText(application.getDescription());
//        if (application.getStatus() == 0) {
//            itemStatus.setText("В процессе");
//            itemStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_status_bad,0, 0, 0);
//        } else {
//            itemStatus.setText("Исправлено");
//            itemStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_status_good,0, 0, 0);
//        }

    }

}
