package com.example.ptixiakiergasia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ptixiakiergasia.MainActivity;
import com.example.ptixiakiergasia.R;
import com.example.ptixiakiergasia.signin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class signup extends AppCompatActivity {
    private EditText name, email_id, passwordcheck, confirmpass;
    private FirebaseAuth mAuth;
    private static final String TAG = "TAG";
    private ProgressBar progressBar;
    private String[] gender = {"Select gender...", "Male", "Female"};

    private static final String USER_AGE = "AGE";

    private static final String USER_GENDER = "GENDER";

    private static final String USER_COUNTRY = "COUNTRY";


    FirebaseFirestore db;


    final List<String> genderList = new ArrayList<>(Arrays.asList(gender));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();


        String country[] = getResources().getStringArray(R.array.countries);

        setContentView(R.layout.activity_signup_activity);
        TextView btnSignUp = (TextView) findViewById(R.id.login_page);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, signin.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        email_id = (EditText) findViewById(R.id.input_email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        passwordcheck = (EditText) findViewById(R.id.input_password);
        confirmpass = (EditText) findViewById(R.id.confirm_password);

        Button ahsignup = (Button) findViewById(R.id.btn_signup);
        ahsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_id.getText().toString();
                String password = passwordcheck.getText().toString();
                String confpass = confirmpass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confpass)) {
                    Toast.makeText(signup.this, "Password Not matching", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(signup.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(signup.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, genderList) {
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


        final ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, country) {
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
        countryArrayAdapter.setDropDownViewResource(R.layout.spinner_item);

    }
}