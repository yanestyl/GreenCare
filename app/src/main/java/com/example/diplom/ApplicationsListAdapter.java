package com.example.diplom;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ApplicationsListAdapter extends ArrayAdapter<ApplicationsListData> {


    public ApplicationsListAdapter(@NonNull Context context, ArrayList<ApplicationsListData> applicationsDataArrayList) {
        super(context, R.layout.list_item, applicationsDataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        ApplicationsListData listData = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView listImage = view.findViewById(R.id.listImage);
        TextView listDescription = view.findViewById(R.id.listDescription);
        TextView listStatus = view.findViewById(R.id.listStatus);

        // Работаем с описанием
        assert listData != null;
        listDescription.setText(listData.description);

        // Работаем со статусами
        if (listData.status == 0) {
            listStatus.setText("В процессе");
            listStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_watch,0, 0, 0);
        } else {
            listStatus.setText("Исправлено");
            listStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_status_good,0, 0, 0);
        }

        // Работаем с изображением
        listImage.setImageBitmap(BitmapFactory.decodeByteArray(listData.imageData, 0, listData.imageData.length));

        return view;

    }
}
