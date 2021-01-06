package com.example.imagetotextusingmlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.imagetotextusingmlkit.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpViewClickListener();
    }

    private void setUpViewClickListener() {

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });

    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 1010);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1010 && resultCode == RESULT_OK) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            binding.image.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage() {

        if (imageBitmap == null) {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        recognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                // Task completed successfully
                String text = firebaseVisionText.getText();
                binding.resultText.setText(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Task failed with an exception
                Toast.makeText(MainActivity.this, "Failed to detect text", Toast.LENGTH_SHORT).show();
            }
        });

    }

}