package com.example.ptixiakiergasia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ViewAllImage extends AppCompatActivity {

    private FirebaseFirestore db;

    private static final String TAG = "GalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_image);
        Log.d(TAG, "onCreate: started.");
        db = FirebaseFirestore.getInstance();


        getIncomingIntent();



    }



    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if (getIntent().hasExtra("image_url")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String imageUrl = getIntent().getStringExtra("image_url");
            Log.d(TAG, imageUrl);

            setImage(imageUrl);
        }
    }


    private void setImage(String imageUrl) {
        Log.d(TAG, "setImage: setting the image and name to widgets.");


        ImageView image = findViewById(R.id.fullScreen);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(image);
    }



    }


