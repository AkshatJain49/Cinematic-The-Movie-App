package com.example.cinematic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SaveImageActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    ImageView imageSave;
    Bitmap bitmap;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        Intent intent = getIntent();
        String posterURL = intent.getStringExtra("URL");
        name = intent.getStringExtra("NAME");

        // REMOVING WHITE SPACE AN SPECIAL CHARACTERS FROM NAME
        name = name.replaceAll("[^a-zA-Z]","");

        constraintLayout = findViewById(R.id.constraintLayout);
        imageSave = findViewById(R.id.imageSave);
        ImageButton buttonSave = findViewById(R.id.buttonSave);

        Picasso.get().load(posterURL).into(imageSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ActivityCompat.checkSelfPermission(SaveImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(SaveImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);

                else
                    saveImage();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==101)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                saveImage();

            else
                Snackbar.make(constraintLayout, "PERMISSION DENIED", BaseTransientBottomBar.LENGTH_SHORT).show();

        }
    }


    protected void saveImage() {

        bitmap = ((BitmapDrawable)imageSave.getDrawable()).getBitmap();
        File path = Environment.getExternalStorageDirectory();
        File directory = new File(path + "/DCIM/Cinematic");
        directory.mkdir();
        String imageName = name + ".png";
        File file = new File(directory, imageName);
        OutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Snackbar.make(constraintLayout, "IMAGE SAVED", BaseTransientBottomBar.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);
        }

        catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(constraintLayout, "UNABLE TO SAVE IMAGE", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

}
