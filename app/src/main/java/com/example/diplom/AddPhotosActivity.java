package com.example.diplom;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddPhotosActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private LinearLayout photosContainer;
    private Button addPhotoButton;
    private Button capturePhotoButton;
    private Button nextButton;

    private ArrayList<Uri> selectedPhotos;

    private String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        photosContainer = findViewById(R.id.photosContainer);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        capturePhotoButton = findViewById(R.id.capturePhotoButton);
        nextButton = findViewById(R.id.nextButton);

        selectedPhotos = new ArrayList<>();

        addPhotoButton.setOnClickListener(v -> openGallery());

        capturePhotoButton.setOnClickListener(v -> capturePhoto());

        nextButton.setOnClickListener(v -> {
            // Переход на следующую активность и передача URI фотографий
            Intent intent = new Intent(AddPhotosActivity.this, AddDescriptionActivity.class);
            intent.putParcelableArrayListExtra("selectedPhotos", selectedPhotos);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(
                            this,
                            "com.example.diplom.fileprovider",
                            photoFile
                    );
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                handleImagePickResult(data);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                addPhotoToContainer(Uri.fromFile(new File(currentPhotoPath)));
            }
        }
    }

    private void handleImagePickResult(Intent data) {
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            int count = clipData.getItemCount();
            for (int i = 0; i < count; i++) {
                Uri imageUri = clipData.getItemAt(i).getUri();
                addPhotoToContainer(imageUri);
            }
        } else if (data.getData() != null) {
            Uri imageUri = data.getData();
            addPhotoToContainer(imageUri);
            galleryAddPic();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void addPhotoToContainer(Uri photoUri) {
        selectedPhotos.add(photoUri);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.photo_size),
                getResources().getDimensionPixelSize(R.dimen.photo_size));
        layoutParams.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(photoUri);

        photosContainer.addView(imageView);
    }
}