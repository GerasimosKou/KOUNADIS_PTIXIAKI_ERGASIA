package com.example.ptixiakiergasia;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class signin extends AppCompatActivity {
    private static final String TAG = "";
    private EditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    SignInButton button;
    private final static int RC_SIGN_IN = 123;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListner;
    GoogleSignInAccount googleSignInAccount;

    String GOOGLE_CLIENT_ID = "735580204262-8bb7q66i36kifqr3l5tk8eva8aptmgvc.apps.googleusercontent.com";
    String GOOGLE_CLIENT_SECRET = "0RofKwl1YBbf-fmVVCeXuEfV";

    private static final String USER_BIRTH = "BIRTHDAY";

    private static final String USER_GENDER = "GENDER";

    private static final String USER_LOCATION = "LOCATION";

    private static final String USER_HOMETOWN = "HOMETOWN";

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        //check the current user
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(signin.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_signin);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button ahlogin = (Button) findViewById(R.id.ah_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView btnSignIn = (TextView) findViewById(R.id.sign_in_button);
        button = (SignInButton) findViewById(R.id.sign_in_google);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signin.this, signup.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();

        //    loginButton.setReadPermissions(Arrays.asList("public_profile,user_gender, email, user_birthday, user_hometown, user_location,user_photos"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.d(TAG, "User ID: " +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        "Auth Token: " + loginResult.getAccessToken().getToken());
                //     setFacebookData(loginResult);

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        // Checking the email id and password is Empty
        ahlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // there was an error
                                    Log.d(TAG, "signInWithEmail:success");
                                    Intent intent = new Intent(signin.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d(TAG, "singInWithEmail:Fail");
                                    Toast.makeText(signin.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(signin.this, MainActivity.class));
                }
            }
        };
        Scope myScope = new Scope("https://www.googleapis.com/auth/user.birthday.read");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(GOOGLE_CLIENT_ID)
                .requestServerAuthCode(GOOGLE_CLIENT_ID)
                .requestScopes(new Scope("profile"), myScope)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    // Toast.makeText(getApplicationContext(),R.string.firebase_error_login, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);

                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(signin.this);
                            if (acct != null) {
                                String personName = acct.getDisplayName();
                                String personGivenName = acct.getGivenName();
                                String personFamilyName = acct.getFamilyName();
                                String personEmail = acct.getEmail();
                                String personId = acct.getId();

                                Uri personPhoto = acct.getPhotoUrl();

                                Log.wtf("PERSON NAME EINAI", personName);
                                Log.wtf("PERSON NAME EINAI", personFamilyName);
                                Log.wtf("PERSON NAME EINAI", personGivenName);
                                Log.wtf("PERSON NAME EINAI", personEmail);
                                Log.wtf("PERSON NAME EINAI", personId);
                                System.out.println("TO Name einai" + personName);


                                //   new GoogleAdditionalDetailsTask().execute(acct);
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(signin.this, "Authentication Fail", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

}


