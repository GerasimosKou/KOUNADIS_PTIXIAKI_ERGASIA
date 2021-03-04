package com.example.ptixiakiergasia;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadImage extends AppCompatActivity {

    private static final int PICK_IMAGE= 223;
    private Uri mImageUri,camImageUri;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressCircle;
    private Spinner Spvisible;
    private String collection;
    private String[] visible = {"Select who can see your photo...", "Private", "Public"};
    private static final String PHOTO_URL = "imageUrl";
    private static final String USER_ID = "USER ID";
    private static final String DATE_TIME = "date";
    public Uri CameraUri = null;

    private String uId;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    FirebaseFirestore db;

    private StorageReference idStorageRef;
    private FirebaseFirestore mFirestoreRef;

    private static String path = null;

    final List<String> visibleList = new ArrayList<>(Arrays.asList(visible));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        db = FirebaseFirestore.getInstance();


        mAuth = FirebaseAuth.getInstance();

        mFirestoreRef = FirebaseFirestore.getInstance();

        Button mButtonUpload = findViewById(R.id.UploadImage);
        final Button mButtonChooseImage = findViewById(R.id.chooseimage);
        Spvisible = (Spinner) findViewById(R.id.whosee);
        mProgressCircle = findViewById(R.id.progress_circle);

        try{
            String path = getIntent().getExtras().getString("my_key");
            Log.wtf("TO PATH EINAI",path);
            Uri camImageUri = Uri.parse(path);

            Log.wtf("TO URI EINAI", String.valueOf(camImageUri));
            ImageView mImageView = findViewById(R.id.fullScreen);
            CameraUri = camImageUri;
            mButtonChooseImage.setVisibility(View.GONE);
            Glide.with(this)
                    .asBitmap()
                    .load(camImageUri)
                    .into(mImageView);
        }catch(Exception e){
            e.printStackTrace();
            Log.e("",e.getMessage());
        }


        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visible = (String) Spvisible.getSelectedItem();

                if (visible == "Select who can see your photo...") {
                    Toast.makeText(getApplicationContext(), "Please select who can see your photo", Toast.LENGTH_SHORT).show();
                    return;
            }
                if(CameraUri != null){
                    uploadImage(CameraUri);
                    mProgressCircle.setVisibility(View.VISIBLE);
                }
                else{
                uploadImage(mImageUri);
                    mProgressCircle.setVisibility(View.VISIBLE);
                }
        }
        });



        if((Build.VERSION.SDK_INT < Build.VERSION_CODES.N) && path != null) {


        }


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openFileChooser();

                mButtonChooseImage.setVisibility(View.GONE);

            }
        });

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, visibleList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        Spvisible.setAdapter(spinnerArrayAdapter);

        Spvisible.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView mImageView = findViewById(R.id.fullScreen);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Glide.with(this)
                    .asBitmap()
                    .load(mImageUri)
                    .into(mImageView);

        }
    }

    private void uploadImage(Uri imageUri){
        if(imageUri!=null) {

            FirebaseUser currentU = FirebaseAuth.getInstance().getCurrentUser();
            if (currentU != null){
            uId = currentU.getUid();}
            else{
                uId = Profile.getCurrentProfile().getId();
            }
            Log.wtf("TO UID EINAI",uId);

            idStorageRef = FirebaseStorage.getInstance().getReference(uId);

            StorageReference idStorage = idStorageRef.child(UUID.randomUUID().toString());



            final Date currentTime = Calendar.getInstance().getTime();



            idStorage.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                            Upload upload = new Upload(taskSnapshot.getDownloadUrl().toString(),currentTime);
                            Log.wtf("TO DOWNLOAD URI EINAI", String.valueOf(downloadUrl));

                            String visibility;

                            visibility = Spvisible.getSelectedItem().toString();

                            if(visibility == "Private"){
                                collection = "Images";
                            }
                            else{
                                collection = "ImagesPub";
                            }

                            Map<String, Object> newUser = new HashMap<>();

                            newUser.put(PHOTO_URL, downloadUrl);
                            newUser.put(USER_ID,uId);
                            newUser.put(DATE_TIME,currentTime);

                            mFirestoreRef.collection(collection)
                                    .add(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            Toast.makeText(getApplicationContext(),"Upload Complete",Toast.LENGTH_LONG).show();
                                            mProgressCircle.setVisibility(View.GONE);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });

                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });
        }
        else{
            Log.wtf("EMPTY","EMPTY");
        }
    }
}

