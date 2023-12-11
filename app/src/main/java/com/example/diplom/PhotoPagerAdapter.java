package com.example.diplom;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoPagerAdapter extends RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder> {
    private List<byte[]> imageDataList;

    public PhotoPagerAdapter(List<byte[]> imageDataList) {
        this.imageDataList = imageDataList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_pager_frame, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        byte[] imageData = imageDataList.get(position);
        holder.photoImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));

        holder.photoTextView.setText(position+1 + "");

    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView photoTextView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            photoTextView = itemView.findViewById(R.id.photoTextView);
        }
    }
}
