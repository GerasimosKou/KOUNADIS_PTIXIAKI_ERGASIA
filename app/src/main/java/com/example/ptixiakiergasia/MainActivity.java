package com.example.ptixiakiergasia;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;

    FirebaseFirestore db;
    Uri imageUri,mImageUri;

    private StorageReference idStorageRef;
    private FirebaseFirestore mFirestoreRef;


    private static final String PHOTO_URL = "imageUrl";
    private static final String USER_ID = "USER ID";
    private static final String DATE_TIME = "date";
    private static final String IMAGE_DIRECTORY_NAME = "PtixiakiImages";
    public static int RC_LOCATION_CONTACTS_PERM = 555;
    final int TAKE_PICTURE = 115;
    final int SELECT_IMAGE = 421;
    final int UPLOAD_IMAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mFirestoreRef = FirebaseFirestore.getInstance();


        Button takePictureButton = findViewById(R.id.button_take_photo);
        Button uploadPictureButton = findViewById(R.id.button_upload_photo);
        Button login = findViewById(R.id.button_login);
        Button myimages = findViewById(R.id.button_myimages);
        Button allimages = findViewById(R.id.button_publicimages);

        allimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImagesActivityPublic.class));

            }
        });

        myimages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser() != null || isLoggedIn()){
                    startActivity(new Intent(MainActivity.this, ImagesActivity.class));}
                else {
                    Toast.makeText(MainActivity.this,"You need to be signed in to view your photos",Toast.LENGTH_SHORT).show();


                    new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog)
                            .setTitle("You need to log in to view your images")
                            .setMessage("Would you like to sign in?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(MainActivity.this,signin.class));

                                    dialog.dismiss();
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("No", null)
                            .show();

                }
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth.getCurrentUser() != null || isLoggedIn()){

                // External sdcard location
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

                if (! mediaStorageDir.exists()){
                    if (! mediaStorageDir.mkdirs()){
                        //Log.d(IMAGE_DIRECTORY_NAME, "Required media storage does not exist");
                        makeText(getApplicationContext(), "Required media storage does not exist", Toast.LENGTH_SHORT).show();
                    }

                }

                // Create a media file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File mediaFile;
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_"+ timeStamp + ".jpg");

                try {
                    mediaFile = File.createTempFile("IMG_"+ timeStamp,".jpg",mediaStorageDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Log.d(TAG,mediaFile.toString());



                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //needs to be added in order for older phone versions to work on sdk26!!!!!
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT))
                    imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.ptixiakiergasia", mediaFile);
                else
                    imageUri = Uri.fromFile(mediaFile);

                //imageUri = Uri.fromFile(mediaFile);
                //Log.d(TAG,imageUri.toString());
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
                else {
                    Toast.makeText(MainActivity.this,"You need to log in to upload an image",Toast.LENGTH_SHORT).show();


                    new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog)
                            .setTitle("You need to log in to upload an image")
                            .setMessage("Would you like to sign in?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(MainActivity.this,signin.class));

                                    dialog.dismiss();
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("No", null)
                            .show();

                }
        }});

        uploadPictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null || isLoggedIn()) {
                    Intent upload_intent = new Intent(MainActivity.this, UploadImage.class);
                    startActivityForResult(upload_intent, UPLOAD_IMAGE);
                } else {
                    Toast.makeText(MainActivity.this, "You need to log in to upload an image", Toast.LENGTH_SHORT).show();


                    new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog)
                            .setTitle("You need to log in to upload an image")
                            .setMessage("Would you like to sign in?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(MainActivity.this, signin.class));

                                    dialog.dismiss();
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("No", null)
                            .show();

                }


            } });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() == null && isLoggedIn() == false){
                    startActivity(new Intent(getApplicationContext(),signin.class));
                }


                else{
                    Toast.makeText(getApplicationContext(),"You are already signed in " ,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onStart(){
        super.onStart();
        locationAndContactsTask();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dots_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {


            case R.id.sign_out:
                if (mAuth.getCurrentUser() != null){
                    mAuth.signOut();
                    Toast.makeText(getApplicationContext(),"Successfully signed out",Toast.LENGTH_SHORT).show();}
                else if(isLoggedIn()) {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplicationContext(), "Successfully signed out", Toast.LENGTH_SHORT).show();
                }
                    else{
                        Toast.makeText(getApplicationContext(),"Successfully signed out",Toast.LENGTH_SHORT).show();

                }
                break;


        }
        return true;
    }

    public void locationAndContactsTask() {
        String[] perms = {Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA };
        if (EasyPermissions.hasPermissions(this, perms)) {
              } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts),
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK ) {
                    try {

                        Intent upload_intent = new Intent(getApplicationContext(), UploadImage.class);

                        Bundle upload_bundle = new Bundle();
                        upload_bundle.putString("my_key", imageUri.toString());
                        upload_intent.putExtras(upload_bundle);
                        startActivity(upload_intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();

                    }


                }
                break;

            default:
                break;
        }
    }
}



