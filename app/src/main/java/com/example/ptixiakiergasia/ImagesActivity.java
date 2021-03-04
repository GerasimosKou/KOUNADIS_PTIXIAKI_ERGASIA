package com.example.ptixiakiergasia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;
    private String uId;

    private CollectionReference mFirestore;
    private FirebaseFirestore db;
    private List<Upload> mUploads;
    private DocumentSnapshot mdocumentsnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        db = FirebaseFirestore.getInstance();


        FirebaseUser currentU = FirebaseAuth.getInstance().getCurrentUser();

        if (currentU != null) {
             uId = currentU.getUid();
        }
            else{
                uId = Profile.getCurrentProfile().getId();
            }

            CollectionReference imagesCollectionRef = db.collection("Images");

            final com.google.firebase.firestore.Query imagesQuery = imagesCollectionRef
                    .whereEqualTo("USER ID", uId).orderBy("date", Query.Direction.DESCENDING);

            imagesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    imagesQuery
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.w("YourTag", "Listen failed.", e);
                                        return;
                                    }
                                    mUploads.clear();

                                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                        if (doc.exists()) {
                                            Upload upload = doc.toObject(Upload.class);
                                            mUploads.add(upload);
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();

                                    Log.d("YourTag", "messageList: " + mUploads);
                                }
                            });

                    mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);


                    mRecyclerView.setAdapter(mAdapter);
                    mProgressCircle.setVisibility(View.INVISIBLE);

                    mAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            mUploads.get(position);
                            Intent myIntent = new Intent(getApplicationContext(), ViewImage.class);
                            myIntent.putExtra("image_url", mUploads.get(position).getImageUrl());
                            startActivity(myIntent);
                        }
                    });

                }


            });
        }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(ImagesActivity.this, MainActivity.class));
    }

}