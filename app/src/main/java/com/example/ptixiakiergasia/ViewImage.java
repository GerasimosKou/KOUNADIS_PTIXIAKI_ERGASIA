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

public class ViewImage extends AppCompatActivity {

    private FirebaseFirestore db;

    private static final String TAG = "GalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Log.d(TAG, "onCreate: started.");
        db = FirebaseFirestore.getInstance();
        ImageButton Deletebutton = findViewById(R.id.deleteimage);


        Deletebutton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {


                final CollectionReference imagesCollectionRef = db.collection("Images");
                String imageUrl = getIntent().getStringExtra("image_url");

                com.google.firebase.firestore.Query imagesQuery = imagesCollectionRef
                        .whereEqualTo("imageUrl",imageUrl);

                imagesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //     Log.d("TO ID EINAI", document.getId());

                                String imageID = document.getId();

                                Task<Void> imageDocRef =  imagesCollectionRef.document(imageID)
                                        .delete();



                            }
                        }
                    }
                 });
                startActivity(new Intent(ViewImage.this, ImagesActivity.class));
            }
        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.image_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String imageUrl = getIntent().getStringExtra("image_url");
        int id = item.getItemId();

        switch (id) {

            case R.id.delete:

                final CollectionReference imagesCollectionRef = db.collection("Images");


                com.google.firebase.firestore.Query imagesQuery = imagesCollectionRef
                        .whereEqualTo("imageUrl",imageUrl);

                imagesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                           //     Log.d("TO ID EINAI", document.getId());

                                String imageID = document.getId();

                                Task<Void> imageDocRef =  imagesCollectionRef.document(imageID)
                                        .delete();


                            }
                        }
                    }

        });
                finish();
                startActivity(new Intent(ViewImage.this, ImagesActivity.class));
                break;
        }
        return true;
    }


}

